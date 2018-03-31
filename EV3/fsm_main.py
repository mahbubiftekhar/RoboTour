#! /usr/bin/env python3
from finite_state_machine import *
from dispatcher import Dispatcher
from algorithm import LineFollowing, Calibration, ObstacleAvoidance
from robot import Robot
from telemetry import *
from transitions import OnBranch

# instantiate main robot class
robot = Robot()

# instantiate and set up line following algorithm
line_follower = LineFollowing(robot)
line_follower.set_gains(2.5, 0, 1.5)

calibration = Calibration(robot)
obstacle_avoidance = ObstacleAvoidance(robot)

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

st_at_painting = State("At painting")

st_obstacle_avoidance = State("Obstacle Avoidance")

st_stop = State("Stop")

# define obstacle detection trigger
def obstacle_detected(env):
	return env.dist_front < 200



def seek_line():
	tmp = line_follower.base_speed
	line_follower.base_speed = 0

	print("LINE LOST")
	line_follower.run()
	line_follower.base_speed = tmp

def branch_routine():
	print(robot.env.next_turn)
	if(robot.env.next_turn == 'left'):
		robot.motor(0,175)
		
	elif robot.env.next_turn == 'right':
		robot.motor(175,0)
	elif robot.env.next_turn == 'forward':
		line_follower.run()
	elif robot.env.next_turn == 'back':
		# Michal in this part the robot needs to turn 180 degrees,there should be a better way than calling motor()
		robot.motor(250,-250)
	elif robot.env.next_turn == 'stop':
		robot.stop()
	else:
		pass  # arrvided, turn pointer

def determine_next_turn():

	print(robot.env.positions_list)
	if not robot.env.positions_list:
		robot.env.next_turn = 'stop'
	else:
		robot.env.next_position = robot.env.positions_list.pop()
		robot.env.next_orientation = robot.env.orientation_map[(robot.env.position, robot.env.next_position)]
		convert_to_direction()
		print(robot.env.turns_list)
		robot.env.next_turn = robot.env.turns_list.pop()

		if (robot.env.position, robot.env. next_position) in robot.env.obstacle_map:

			robot.env.avoidance_direction = robot.env.obstacle_map[(robot.env.position, robot.env.next_position)]
		else:
			robot.env.avoidance_direction = 'stop'

		robot.env.position = robot.env.next_position

def black_line_detected(env):
	if robot.env.next_turn == 'forward':
			# count for the branch, update location, orientation, etc.
			determine_next_turn()
			pass
	else:

		if robot.env.avoidance_direction == 'left':
			# turn right and follow the black line, update the orientation, not the lccation
			robot.motor(175,0)

			# follow black line until it saw the white line
			pass
		else:
			# turn right and follow the black line, update the orientation, not the lccation
			robot.motor(0,175)

			# follow black line until it saw the white line
			pass

def convert_to_direction():
	if (robot.env.orientation == 'N' and robot.env.next_orientation == 'E') or (robot.env.orientation == 'E' and robot.env.next_orientation == 'S') or (robot.env.orientation == 'S' and robot.env.next_orientation == 'W') or (robot.env.orientation == 'W' and robot.env.next_orientation == 'N'):
		robot.env.turns_list = ['right']

	elif (robot.env.orientation == 'N' and robot.env.next_orientation == 'W') or (robot.env.orientation == 'E' and robot.env.next_orientation == 'N')  or (robot.env.orientation == 'S' and robot.env.next_orientation == 'E') or (robot.env.orientation == 'W' and robot.env.next_orientation == 'S'):
		robot.env.turns_list = ['left']

	elif (robot.env.orientation == 'N' and robot.env.next_orientation == 'S') or (robot.env.orientation == 'E' and robot.env.next_orientation == 'W') or (robot.env.orientation == 'S' and robot.env.next_orientation == 'N') or (robot.env.orientation == 'W' and robot.env.next_orientation == 'E'):
		robot.env.turns_list = ['back']

	elif (robot.env.orientation == robot.env.next_orientation):
		robot.env.turns_list = ['forward']

	else:
		# error
		pass

	robot.env.orientation = robot.env.next_orientation



sweep_time = 1750

st_start.add_transition(Transition(st_calibration))

st_calibration.add_transition(Transition(st_idle, calibration.done))

st_idle.add_transition(Transition(st_wait, obstacle_detected))

# if nothing happens - obstacle disapears, go to line-following
st_wait.set_default(st_line_following)
# point back to yourself if 
st_wait.add_transition(Transition(st_wait, obstacle_detected))
# after 5 sec of seeing obstacle finish program NB higher priority transition
st_wait.add_transition(TransitionTimed(5000, st_stop))

st_line_following.add_transition(Transition(st_line_lost, robot.line_sensor.no_line))
st_line_following.add_transition(Transition(st_obstacle_avoidance, obstacle_detected))
st_line_following.add_transition(OnBranch(st_branch))

st_line_lost.set_default(st_line_following)
st_line_lost.add_transition(Transition(st_line_lost, robot.line_sensor.no_line))
st_line_lost.add_transition(Transition(st_obstacle_avoidance, obstacle_detected))


st_branch.add_transition(TransitionTimed(1500, st_line_following))
st_branch.on_activate(determine_next_turn)


st_obstacle_avoidance.add_transition(Transition(st_line_following, obstacle_avoidance.done))
st_obstacle_avoidance.on_activate(obstacle_avoidance.reset)


fsm = FSM(st_start)
dsp = Dispatcher(fsm)

dsp.link_action(st_calibration, calibration.run)
dsp.link_action(st_obstacle_avoidance, obstacle_avoidance.run)


dsp.link_action(st_idle, robot.stop)
dsp.link_action(st_wait, robot.stop)

dsp.link_action(st_line_following, line_follower.run)
dsp.link_action(st_line_lost, seek_line)

dsp.link_action(st_branch, branch_routine)

dsp.link_action(st_stop, robot.stop)


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




# instantiate the logger object
logger = DataLogger("integration_test", folder='../logs/', timer=timer_hook)

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

logger.add_channel(DataChannel("poll_time", polling_hook))
logger.add_channel(DataChannel("loop_time", loop_time_hook))



logger.init()

####### 

for i in range(10):
	robot.hub.poll()

assert robot.hub.connected
robot.update_env()


while fsm.current_state != st_stop:
	# sense
	robot.update_env()
	
	# plan
	fsm.tick(robot.env)
	# print(fsm.get_state(),end=' ')
	
	# act
	dsp.dispatch()

	logger.log()

	# print(robot.env.line_sens_val, robot.env.dist_front, robot.line_sensor.no_line(None))

robot.stop()