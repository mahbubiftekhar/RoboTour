#! /usr/bin/env python3
from finite_state_machine import *
from dispatcher import Dispatcher
from algorithm import LineFollowing
from robot import Robot
from telemetry import *

robot = Robot()

line_follower = LineFollowing(robot)
line_follower.set_gains(2.25, 0, 1.5)

st_start = State("Start")

st_calibrate_right = State("Calibrating")
st_calibrate_left = State("Calibrating")
st_calibrate_centre = State("Calibrating")

st_line_following = State("Line following")
st_stop = State("Stop")

def obstacle_detected(env):
	return env.dist_front < 300

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




while fsm.current_state != st_stop:
	# sense
	robot.update_env()
	
	# plan
	fsm.tick(robot.env)
	
	# act
	dsp.dispatch()

	print(robot.env.line_sens_val, robot.env.dist_front)

robot.stop()