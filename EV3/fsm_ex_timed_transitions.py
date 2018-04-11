#! /usr/bin/env python3

from finite_state_machine import *
import time

'''
In this example we will go over implementing a more complex transition
based on time. As there might be several transitions from a given state
the timed ones cannot block the execution of the program. Additionally
it will be useful to have a single clock for all events and measure the
differences. We will then use a global clock that will live inside the
environment class

This example will cover a few more complex concepts so we will try to
walk quite slowly through it
'''

# let's start by creating the environment object

class Environment():
	def __init__(self):
		self.update()

	# in each loop we will update the clock using system timer
	# it returns the time in seconds, but we will use miliseconds as
	# they tend to be more convenient
	def update(self):
		self.clock_ms = time.perf_counter() * 1000

env = Environment()

# let's create the states we want to transition between

start = State("Start")
state1 = State("Tick")
state2 = State("Tock")


# we have to create a new type of transition to utilise the env.clock_ms
class DelayedTransition(Transition):
	# override the constructor
	def __init__(self, time_ms, next_state):
		Transition.__init__(self, next_state)
		self.transition_after = time_ms

	# we need to utilise the function that gets called when we enter
	# the state. Override it to remember starting time
	def arm(self, env):
		self.start_time = env.clock_ms

	# now override the condition method that triggers the transition
	def condition(self, env):
		# calculate how much time has elapsed so far
		time_since_arm = env.clock_ms - self.start_time
		# return whether we've waited long enough
		return time_since_arm >= self.transition_after


# time to link the states

# first, a default transition with no delay or conditions
start.add_transition(Transition(state1))

# now the timed ones
# transition to state2 after 500ms
state1.add_transition(DelayedTransition(500, state2))
# transition to state1 after 1500ms
state2.add_transition(DelayedTransition(1500, state1))

# since we will be running our loop pretty fast, we need a way
# to know when the transitions take place. We can use on_activate hooks

# first, define functions to be called (needs env as an argument)
def ticking(env):
	print("Tick!")

def tocking(env):
	print("Tock!")

# then, add them to the state
state1.on_activate(ticking)
state2.on_activate(tocking)

# finally initialise an FSM

machine = FSM(start)

# now lets run the function (use ctrl+c to break out)
print("Running the clock! Use CTRL+C to stop")
while True:
	env.update()
	machine.tick(env)

# NOTE THAT A TIMED TRANSITION IS PART OF THE FRAMEWORK AND DOES NOT NEED TO BE
# DEFINED EVERY TIME YOU WANT TO USE IT. SEE TransitionTimed
# THIS EXAMPLE WAS TO SHOW HOW YOU CAN DESIGN YOUR OWN TRANSITIONS THAT ARE
# BASED ON SIMILAR PRINCIPLES