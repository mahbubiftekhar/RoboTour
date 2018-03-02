#! /usr/bin/env python3

from finite_state_machine import *

#####################################
# NOTE: THIS IS ALTERNATIVE IMPLEMENTATION OF THE EXAMPLE 2
#####################################

# again, let's create a couple states

starting_state = State("Start")
state_one  = State("I want to appear once")
state_two  = State("I want to appear twice")
state_three= State("I want to appear four times")

# to get the states what they want we might use a similar
# approach to the previous problem (see fsm_example1.py : after_2_times)
# but this would require us to make three separate counters! What if
# the states have more complex demands or have to be created in runtime?

#### DIFFERENCE IS HERE ######
# lets create a subclass for this type of transition
class TransitionAfter(Transition):

	def __init__(self, n, next_state):
		# this time instead of pointing to a function we will overload
		# an existing function
		Transition.__init__(self, next_state)
		self.count_to = n
		self.counter = 0

	# note that the function is called condition and takes two arguments
	# it is a function that is automatically linked during object creation
	def condition(self, env):
		self.counter += 1
		if self.counter == self.count_to:
			self.counter = 0
			return True
		else:
			return False

#### SAME BEYOND THIS POINT


# now link the states
state_one.add_transition(TransitionAfter(1, state_two))
state_two.add_transition(TransitionAfter(2, state_three))
state_three.add_transition(TransitionAfter(4, state_one))

# use the default parameter of the state to link the starting to one
starting_state.set_default(state_one)

# instantiate fsm
machine = FSM(starting_state)

# this time we don't have any environment but we still need to pass it
env = None

for i in range(32):
	print("Tick {}, state = {}".format(i, machine.get_state()))
	machine.tick(env)