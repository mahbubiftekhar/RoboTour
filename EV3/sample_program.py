#! /usr/bin/env python3
from robot import Robot
from finite_state_machine import *

robot = Robot()

start = State("Start")
move_forward = State("Forward")
turn_right = State("Right")
stop = State("Stop")

def running_for_6_sec(env):
	return robot.env.timer_ms >= 6000

start.add_transition(Transition(move_forward))

move_forward.add_transition(TransitionTimed(1500, turn_right))

turn_right.add_transition(TransitionTimed(500, move_forward))
turn_right.add_transition(Transition(stop, running_for_6_sec))

fsm = FSM(start)
robot.env.init()

while fsm.current_state != stop:
	
	robot.env.update()
	fsm.tick(env)

	if fsm.current_state == move_forward:
		robot.motor(150,150)
	else if fsm.current_state == turn_right:
		robot.motor(100, -100)
	else if fsm.current_state == stop:
		robot.motor(0,0)

robot.motor(0,0)
