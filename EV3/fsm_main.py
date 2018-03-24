#! /usr/bin/env python3
from finite_state_machine import *
from dispatcher import Dispatcher
from algorithm import LineFollowing, Calibration
from robot import Robot
from telemetry import *

# instantiate main robot class
robot = Robot()

# instantiate and set up line following algorithm
line_follower = LineFollowing(robot)
line_follower.set_gains(2.5, 0, 1.5)

calibration = Calibration(robot)

## FSM SETUP ##

st_start = State("Start")

# calibration mode
st_init = State("Initialisation")
st_calibration = State("Calibration")
st_idle = State("Idle")
st_route_planning = State("Calculating route")

# robot waiting for input - distance sensor



# line following mode
st_line_following = State("Line following")
st_line_lost = State("Lost line")

st_at_painting = State("At painting")

st_stop = State("Stop")

# define obstacle detection trigger
def obstacle_detected(env):
	return env.dist_front < 300

def seek_line():
	tmp = line_follower.base_speed
	line_follower.base_speed = 0

	print("LINE LOST")
	line_follower.run()
	line_follower.base_speed = tmp


sweep_time = 1750

st_start.add_transition(Transition(st_calibration))

st_calibration.add_transition(Transition(st_idle, calibration.done)

st_idle.add_transition(Transition(st_wait, obstacle_detected))

# if nothing happens - obstacle disapears, go to line-following
st_wait.set_default(st_line_following)
# point back to yourself if 
st_wait.add_transition(Transition(st_wait, obstacle_detected))
# after 5 sec of seeing obstacle finish program NB higher priority transition
st_wait.add_transition(TransitionTimed(5000, st_stop))

st_line_following.add_transition(Transition(st_line_lost, robot.line_sensor.no_line))
st_line_following.add_transition(Transition(st_wait, obstacle_detected))

st_line_lost.set_default(st_line_following)
st_line_lost.add_transition(Transition(st_line_lost, robot.line_sensor.no_line))
st_line_lost.add_transition(Transition(st_wait, obstacle_detected))


fsm = FSM(st_start)
dsp = Dispatcher(fsm)

dsp.link_action(st_calibration, calibration.run)

dsp.link_action(st_idle, robot.stop)
dsp.link_action(st_wait, robot.stop)

dsp.link_action(st_line_following, line_follower.run)
dsp.link_action(st_line_lost, seek_line)

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




# instantiate the logger object
logger = DataLogger("fsm_line_following", folder='./logs/', timer=timer_hook)

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
logger.add_channel(DataChannel("steer", steer_hook))
logger.add_channel(DataChannel("poll_time", polling_hook))

logger.init()

####### 



while fsm.current_state != st_stop:
	# sense
	robot.update_env()
	
	# plan
	fsm.tick(robot.env)
	
	# act
	dsp.dispatch()

	logger.log()

	print(robot.env.line_sens_val, robot.env.dist_front, robot.line_sensor.no_line(None))

robot.stop()