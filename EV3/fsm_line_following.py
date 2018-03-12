#! /usr/bin/env python3
from finite_state_machine import *
from dispatcher import Dispatcher
from algorithm import LineFollowing
from robot import Robot
from telemetry import *

# instantiate main robot class
robot = Robot()

# instantiate and set up line following algorithm
line_follower = LineFollowing(robot)
line_follower.set_gains(2.25, 0, 1.5)

## FSM SETUP ##

st_start = State("Start")

# calibration mode
st_calibrate_right = State("Calibrating")
st_calibrate_left = State("Calibrating")
st_calibrate_centre = State("Calibrating")

# line following mode
st_line_following = State("Line following")
st_stop = State("Stop")

# define obstacle detection trigger
def obstacle_detected(env):
	return env.dist_front < 300

# define actions for calibration
def calibrate_right():
	robot.motor(100, -100)
	robot.line_sensor.calibrate()

def calibrate_left():
	robot.motor(-100, 100)
	robot.line_sensor.calibrate()

sweep_time = 1750

st_start.add_transition(Transition(st_calibrate_right))

st_calibrate_right.add_transition(TransitionTimed(sweep_time, st_calibrate_left))
st_calibrate_left.add_transition(TransitionTimed(2*sweep_time, st_calibrate_centre))
st_calibrate_centre.add_transition(TransitionTimed(sweep_time, st_line_following))

st_line_following.add_transition(Transition(st_stop, obstacle_detected))


fsm = FSM(st_start)
dsp = Dispatcher(fsm)

dsp.link_action(st_calibrate_right, calibrate_right)
dsp.link_action(st_calibrate_left, calibrate_left)
dsp.link_action(st_calibrate_centre, calibrate_right)

dsp.link_action(st_line_following, line_follower.run)

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

	print(robot.env.line_sens_val, robot.env.dist_front)

robot.stop()