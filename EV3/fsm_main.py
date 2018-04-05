#! /usr/bin/env python3
from finite_state_machine import *
from dispatcher import Dispatcher
from algorithm import LineFollowing, Calibration, ObstacleAvoidance
from robot import Robot
from telemetry import *
from transitions import OnBranch
from navigation import Navigation
from comms import *
from threading import Thread
import ev3dev.ev3 as ev3

# instantiate main robot class
robot = Robot(fast_hub=True)
server = Server()

# instantiate and set up line following algorithm
line_follower = LineFollowing(robot)
line_follower.set_gains(2.75, 0.02, 1.5)
line_follower.base_speed = 215

calibration = Calibration(robot)
obstacle_avoidance = ObstacleAvoidance(robot, server)

nav = Navigation(robot, server)

## FSM SETUP ##

st_start = State("Start")

# calibration mode
st_init = State("Initialisation")
st_calibration = State("Calibration")
st_idle = State("Idle")
st_route_planning = State("Calculating route")

# robot waiting for input - distance sensor
st_wait = State("Waiting")


# line following mode
st_line_following = State("Line following")
st_line_lost = State("Lost line")

st_branch = State("Branch detected")
st_taking_branch = State("Navigating branch")
st_clearing_branch = State("Moving away from the branch")

st_at_painting = State("At painting")
st_painting_done = State("Leaving painting")

st_tour_done = State("Tour finished")

st_obstacle_avoidance = State("Obstacle Avoidance")

st_stop = State("Stop")

# define obstacle detection trigger
def obstacle_detected(env):
	return env.dist_front < 200

def users_ready(env):
	if env.users == 1:
		return server.user_1_check() == 'T'
	elif env.users == 2:
		return server.user_1_check() == 'T' and server.user_2_check() == 'T'
	else:
		return False

def user_continue(env):
		return server.stop_check() != 'T'
def user_stop(env):
		return server.stop_check() == 'T'

def arrived_at_painting(env):
	if not env.pictures_to_go:
		return False
	return env.position == env.pictures_to_go[0]

def branch_taken(env):
	if(robot.env.next_turn == 'forward'):
		robot.reset_position_at_branch()
		return True
	else:
		if robot.done_movement(env):
			robot.reset_position_at_branch()
			return True
		else:
			return False

def tour_done(env):
	# no more points to go
	return not env.positions_list and env.position == '10'

def no_path(env):
	return not env.positions_list

def ask_for_mode_press():
	robot.speak("Please select the paintings you want to go to!")

def reset():
	robot.stop()
	robot.env.finished_tour = False
	robot.indicate_zero()
	robot.env.users = 0
	robot.speak("Please select single or multi user mode!")
	# try:
	# 	kp = float(input("Kp? "))
	# 	ki = float(input("Ki? "))
	# 	kd = float(input("Kd? "))

	# 	line_follower.set_gains(kp,ki,kd)
	# except:
	# 	print("no changes")
	# robot.sound.beep()

	# robot.sound.beep('-f 700 -l 100')

def mode_selection():
	if robot.button.left:
		robot.env.users = 1
		robot.indicate_one()
		server.update_user_mode(1)
	if robot.button.right:
		robot.env.users = 2
		robot.indicate_two()
		server.update_user_mode(2)

def line_was_lost(env):
	return robot.line_sensor.no_line(env) and abs(env.line_sens_val - 35) > 10 


def seek_line():
	tmp = line_follower.base_speed
	line_follower.base_speed = 0
	# robot.indicate_error()
	print("LINE LOST")
	line_follower.run()
	line_follower.base_speed = tmp

def branch_routine():

	# print(robot.env.next_turn)
	if True:
		return
	if(robot.env.next_turn == 'left'):
		angle = -90
	elif robot.env.next_turn == 'right':
		angle = 90
		# robot.motor(175,0)
	elif robot.env.next_turn == 'back':
		angle = 180
		# Michal in this part the robot needs to turn 180 degrees,there should be a better way than calling motor()
	# forward
	else:
		angle = 0
	robot.rotate(angle,175)

def show_painting():
	robot.stop()
	server.set_stop_true()
	turn_pointer()
	server.update_status_arrived(robot.env.position)
	server.update_commands()
	logger.write_buffer()

def leaving_painting():

	robot.env.pictures_to_go.pop(0)
	if server.check_position('Change') == 'T':
		print("Change")
		server.update_status_false('Change')
		robot.env.positions_list = []
		nav.plan_route()

	if not robot.env.pictures_to_go:
		robot.env.finished_tour = True
		server.update_art_piece('Exit')
	else:
		pic = robot.env.pictures_to_go[0]
		server.update_art_piece(pic)

	turn_pointer_back()

def turn_pointer():
	
	if (robot.env.orientation == 'N' and robot.env.motor_map[robot.env.position] == 'E') or (robot.env.orientation == 'E' and robot.env.motor_map[robot.env.position] == 'S') or (robot.env.orientation == 'S' and robot.env.motor_map[robot.env.position] == 'W') or (robot.env.orientation == 'W' and robot.env.motor_map[robot.env.position] == 'N'):
		robot.pointer_motor(45,300)
		robot.env.pointer_orientation = 'E'

	elif (robot.env.orientation == 'N' and robot.env.motor_map[robot.env.position] == 'W') or (robot.env.orientation == 'E' and robot.env.motor_map[robot.env.position] == 'N')  or (robot.env.orientation == 'S' and robot.env.motor_map[robot.env.position] == 'E') or (robot.env.orientation == 'W' and robot.env.motor_map[robot.env.position] == 'S'):
		robot.pointer_motor(-45,300)
		robot.env.pointer_orientation = 'W'

	elif (robot.env.orientation == 'N' and robot.env.motor_map[robot.env.position] == 'S') or (robot.env.orientation == 'E' and robot.env.motor_map[robot.env.position] == 'W') or (robot.env.orientation == 'S' and robot.env.motor_map[robot.env.position] == 'N') or (robot.env.orientation == 'W' and robot.env.motor_map[robot.env.position] == 'E'):
		robot.pointer_motor(180,300)
		robot.env.pointer_orientation = 'S'

	elif (robot.env.orientation == robot.env.motor_map[robot.env.position]):
		pass

	else:
		# error
		pass

def turn_pointer_back():

	if robot.env.pointer_orientation == 'E':
		robot.pointer_motor(-45,300)
	elif robot.env.pointer_orientation == 'W':
		robot.pointer_motor(45,300)
	elif robot.env.pointer_orientation == 'S':
		robot.pointer_motor(-180,300)
	elif robot.env.pointer_orientation == 'N':
		pass
	else:
		# error
		pass

	robot.env.pointer_orientation = 'N'

def reset_robot():
	server.update_status_arrived('Exit')
	align_to_N()
	server.reset_list_on_server()
	logger.reinit()
	robot.env.session_name = logger.full_name

def align_to_N():
	if robot.env.orientation == 'E':
		robot.rotate_branch(-90, 150)
	elif robot.env.orientation == 'W':
		robot.rotate_branch(90, 150)
	elif robot.env.orientation == 'S':
		robot.rotate(180, 150)
	elif robot.env.orientation == 'N':
		pass
	else:
		# error
		pass
	
	robot.env.orientation = 'N'

def update_location_on_branch():
	robot.env.position = robot.env.positions_list.pop(0)
	robot.reset_position_at_branch()
	robot.sound.beep()

		# robot.stop()
	if robot.env.finished_tour == False:
		if server.check_position('Cancel') == 'T':
			print("Cancel")
			robot.env.positions_list = []
			robot.env.pictures_to_go = []
			server.update_status_false('Cancel')
		
		elif server.check_position('Toilet') == 'T':
			print("Toilet")
			_, path = nav.get_closest_painting(robot.env.position, ['12'])
			robot.env.positions_list = path[1:]
			# recalculate from toilet and add to positions_list
			server.update_art_piece('Toilet')
			robot.env.pictures_to_go = nav.calculate_paintings_order(robot.env.pictures_to_go, '12')
			robot.env.pictures_to_go.insert(0, '12')
			# server.update_status_false('Toilet')
		
		elif server.check_position('Exit') == 'T':
			print("Exit")
			_, path = nav.get_closest_painting(robot.env.position, ['10'])
			robot.env.positions_list = path[1:]
			# recalculate from exit and add to positions_list
			server.update_art_piece('Exit')
			robot.env.pictures_to_go = nav.calculate_paintings_order(robot.env.pictures_to_go, '10')
			robot.env.pictures_to_go.insert(0, '10')
			# server.update_status_false('Exit')
		elif server.check_position('Skip') == 'T':
			print("Skippito")
			robot.env.pictures_to_go.pop(0)
			# recalculate
			robot.env.positions_list = []
			robot.env.pictures_to_go = nav.calculate_paintings_order(robot.env.pictures_to_go)
			server.update_status_false('Skip')
			if len(robot.env.pictures_to_go) != 0:
				server.update_art_piece(robot.env.pictures_to_go[0])

		if not robot.env.positions_list:
			if robot.env.position != '10':
				_, path = nav.get_closest_painting(robot.env.position, ['10'])
				robot.env.positions_list = path[1:]
				# server.update_art_piece('Exit')


def determine_next_turn():

	if server.check_position('Change') == 'T':
		print("Change")
		server.update_status_false('Change')
		robot.env.positions_list = []
		nav.plan_route()

	print("paintings:", robot.env.pictures_to_go)
	print("position: ", robot.env.positions_list)
	if not robot.env.positions_list:
		robot.env.next_turn = 'stop'

	else:
		
		robot.env.next_position = robot.env.positions_list[0]
		robot.env.next_orientation = robot.env.orientation_map[(robot.env.position, robot.env.next_position)]
		convert_to_direction()
		print(robot.env.turns_list)
		
		robot.env.next_turn = robot.env.turns_list.pop()

			# robot.env.position = robot.env.next_position

		if(robot.env.next_turn == 'left'):
			# robot.go_forward(200)
			angle = -90
			robot.rotate_branch(angle,175)
		elif robot.env.next_turn == 'right':
			# robot.go_forward(200)
			angle = 90
			robot.rotate_branch(angle,175)
			# robot.motor(175,0)
		elif robot.env.next_turn == 'back':
			angle = 180
			robot.rotate(angle,175)
			# Michal in this part the robot needs to turn 180 degrees,there should be a better way than calling motor()
		# forward
		else:
			angle = 0

	robot.reset_position_at_branch()

def calculate_route_to_exit():
	print("TOUR FINISHED. Going to exit")
	_, path = nav.get_closest_painting(robot.env.position, ['10'])
	robot.env.positions_list = path[1:]
	# robot.env.finished_tour = False


def convert_to_direction():
	if (robot.env.orientation == 'N' and robot.env.next_orientation[0] == 'E') or (robot.env.orientation == 'E' and robot.env.next_orientation[0] == 'S') or (robot.env.orientation == 'S' and robot.env.next_orientation[0] == 'W') or (robot.env.orientation == 'W' and robot.env.next_orientation[0] == 'N'):
		robot.env.turns_list = ['right']

	elif (robot.env.orientation == 'N' and robot.env.next_orientation[0] == 'W') or (robot.env.orientation == 'E' and robot.env.next_orientation[0] == 'N')  or (robot.env.orientation == 'S' and robot.env.next_orientation[0] == 'E') or (robot.env.orientation == 'W' and robot.env.next_orientation[0] == 'S'):
		robot.env.turns_list = ['left']

	elif (robot.env.orientation == 'N' and robot.env.next_orientation[0] == 'S') or (robot.env.orientation == 'E' and robot.env.next_orientation[0] == 'W') or (robot.env.orientation == 'S' and robot.env.next_orientation[0] == 'N') or (robot.env.orientation == 'W' and robot.env.next_orientation[0] == 'E'):
		robot.env.turns_list = ['back']

	elif (robot.env.orientation == robot.env.next_orientation[0]):
		robot.env.turns_list = ['forward']

	else:
		# error
		pass

	robot.env.orientation = robot.env.next_orientation[-1]

def update_on_tour():
	server.update_status_true('onTour')

sweep_time = 1750

st_start.add_transition(Transition(st_calibration))

st_calibration.add_transition(Transition(st_idle, calibration.done))

# st_idle.add_transition(Transition(st_wait, obstacle_detected))
st_idle.add_transition(Transition(st_route_planning, users_ready))
st_idle.on_activate(ask_for_mode_press)

st_route_planning.add_transition(Transition(st_taking_branch))
st_route_planning.on_activate(update_on_tour)

# # if nothing happens - obstacle disapears, go to line-following
# st_wait.set_default(st_line_following)
# # point back to yourself if 
# st_wait.add_transition(Transition(st_wait, obstacle_detected))
# # after 5 sec of seeing obstacle finish program NB higher priority transition
# st_wait.add_transition(TransitionTimed(5000, st_stop))

st_line_following.add_transition(Transition(st_line_lost, line_was_lost))
st_line_following.add_transition(Transition(st_obstacle_avoidance, obstacle_detected))
st_line_following.add_transition(OnBranch(st_branch))
st_line_following.add_transition(Transition(st_wait, user_stop))
# st_line_following.on_activate(determine_next_turn)

st_wait.add_transition(Transition(st_line_following, user_continue))

st_line_lost.set_default(st_line_following)
st_line_lost.add_transition(Transition(st_line_lost, robot.line_sensor.no_line))
st_line_lost.add_transition(Transition(st_obstacle_avoidance, obstacle_detected))


st_branch.add_transition(Transition(st_taking_branch))
st_branch.add_transition(Transition(st_at_painting, arrived_at_painting))
st_branch.add_transition(Transition(st_tour_done, tour_done))
st_branch.on_activate(update_location_on_branch)

st_at_painting.add_transition(Transition(st_painting_done, user_continue))

st_painting_done.add_transition(Transition(st_taking_branch))
st_painting_done.add_transition(Transition(st_tour_done, tour_done))

st_taking_branch.on_activate(determine_next_turn)
st_taking_branch.add_transition(Transition(st_clearing_branch, branch_taken))

st_clearing_branch.add_transition(TransitionTimed(1000, st_line_following))
st_clearing_branch.add_transition(Transition(st_obstacle_avoidance, obstacle_detected))


st_obstacle_avoidance.add_transition(Transition(st_line_following, obstacle_avoidance.done))
st_obstacle_avoidance.on_activate(obstacle_avoidance.reset)

st_tour_done.add_transition(Transition(st_idle, robot.done_movement))

fsm = FSM(st_start)
dsp = Dispatcher(fsm)

dsp.link_action(st_calibration, calibration.run)
dsp.link_action(st_obstacle_avoidance, obstacle_avoidance.run)


dsp.link_action(st_idle, mode_selection)
st_idle.on_activate(reset)
dsp.link_action(st_wait, robot.stop)

dsp.link_action(st_route_planning, nav.plan_route)

dsp.link_action(st_line_following, line_follower.run)
dsp.link_action(st_line_lost, seek_line)

dsp.link_action(st_clearing_branch, line_follower.run)

dsp.link_action(st_at_painting, robot.stop)
st_at_painting.on_activate(show_painting)

st_painting_done.on_activate(leaving_painting)

st_tour_done.on_activate(reset_robot)

dsp.link_action(st_taking_branch, branch_routine)

dsp.link_action(st_stop, robot.stop)


def server_check():
    global default_speed
    global is_stop
    global is_skip
    global is_toilet
    global is_exit
    global is_cancel

    while True:
        server.update_commands()

        if server.check_position('Speed') == "3":
            line_follower.base_speed = 225
        elif server.check_position('Speed') == "2":
            line_follower.base_speed = 150
        elif server.check_position('Speed') == "1":
            line_follower.base_speed = 75
        else:
            line_follower.base_speed = 150

        if server.check_position('Stop') == "T":
            is_stop = True
        else:
            is_stop = False

        if server.check_position('Skip') == "T":
            is_skip = True
        else:
            is_skip = False

        if server.check_position('Toilet') == "T":
            is_toilet = True
        else:
            is_toilet = False

        if server.check_position('Exit') == "T":
            is_exit = True
        else:
            is_exit = False

        if server.check_position('Cancel') == "T":
            is_cancel = True
        else:
            is_cancel = False

        time.sleep(1)

server_check_thread = Thread(target=server_check)
server_check_thread.daemon = True
server_check_thread.start()


####### LOGGER SET UP
## Value hooks
def timer_hook():
	return robot.env.clock_ms

def get_detector_hook(index):
	def detector_hook():
		return robot.line_sensor.raw_val[index]
	return detector_hook

def get_detector_threshold_hook(index):
	def threshold_hook():
		return robot.line_sensor.detector[index].threshold
	return threshold_hook

def line_sensor_hook():
	return robot.env.line_sens_val

def steer_hook():
	return line_follower.steer

def polling_hook():
	return robot.hub.last_poll_time

def loop_time_hook():
	return robot.env.loop_time

def rot_right_hook():
	return robot.env.rot_right

def rot_left_hook():
	return robot.env.rot_left

def od_x_hook():
	return robot.env.x

def od_y_hook():
	return robot.env.y

def od_angle_hook():
	return robot.env.angle



# instantiate the logger object
logger = DataLogger("integration_test", folder='../logs/', timer=timer_hook)
logger.lines_per_write = 7500

# add channels for sensor values
for s in robot.line_sensor.detector_names:
	hook = get_detector_hook(s)
	logger.add_channel(DataChannel(s, hook))

# add channels for thresholds
for s in robot.line_sensor.detector_names:
	hook = get_detector_threshold_hook(s)
	logger.add_channel(DataChannel(s+'th', hook))

# add channels for combined value and steer
logger.add_channel(DataChannel("line_sensor", line_sensor_hook))
# logger.add_channel(DataChannel("steer", steer_hook))
logger.add_channel(DataChannel("left_wheel", rot_left_hook))
logger.add_channel(DataChannel("right_wheel", rot_right_hook))

logger.add_channel(DataChannel("odometry_x", od_x_hook))
logger.add_channel(DataChannel("odometry_y", od_y_hook))
logger.add_channel(DataChannel("odometry_angle", od_angle_hook))

logger.add_channel(DataChannel("poll_time", polling_hook))
logger.add_channel(DataChannel("loop_time", loop_time_hook))




####### 

for i in range(10):
	robot.hub.poll()

while not robot.hub.connected:
	robot.indicate_error()
	time.sleep(5)
	robot.hub.poll()
robot.env.init()
robot.update_env()

logger.init()
robot.env.session_name = logger.full_name

try:
	while fsm.current_state != st_stop:
		# sense
		robot.update_env()
		
		# plan
		fsm.tick(robot.env)
		# print(fsm.get_state())
		
		# act
		dsp.dispatch()

		logger.log()

		# print(robot.env.line_sens_val, robot.env.dist_front, robot.line_sensor.no_line(None))
except:
	robot.stop()
	logger.write_buffer()
	turn_pointer_back()
	server.reset_list_on_server()
	raise
	
