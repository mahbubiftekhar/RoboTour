#! /usr/bin/env python3

from finite_state_machine import *

'''
In this example we will create a simple program using our FSM framework.
The aim is to show how to create states and how to link them using
transitions.
Our machine will have four states and following progression:
start -> one -> one -> two -> three -> three -> ... -> three
'''

# first lets create the states

starting_state = State("Start")
state_one  = State("First step")
state_two  = State("Second step")
trap_state = State("And I'm trapped")

# then create possible transition triggers

# e.g. unconditional transition
def always(env):
	return True

# or one utilising our environment variable
def after_2_times(env):
	env.counter += 1
	if env.counter == 2:
		env.counter = 0
		return True
	else:
		return False

# lets define the class that our triggers will use
class environment():
	def __init__(self):
		self.counter = 0

# and instantiate it
env = environment()

# now let's link the states

starting_state.add_transition(Transition(state_one, always))
state_one.add_transition(Transition(state_two, after_2_times))
state_two.add_transition(Transition(trap_state, always))
# since we want the trap state to repeat forever, it does not need
# any transition - it will default to itself
# state_two.add_transition(Transition(trap_state)) would be equivalent

# finally, let's put the states into the machine
# you only need to add the starting point as the machine will follow 
# the transitions as necessary

machine = FSM(starting_state)

for i in range(10):
	print("Tick {}, state = {}".format(i, machine.get_state()))
	machine.tick(env)
