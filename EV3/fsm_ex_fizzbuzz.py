#! /usr/bin/env python3

from finite_state_machine import *


# in this example we will use the FSMs to create FizzBuzz
#
# we want to print Fizz if the tick count is divisible by three
#  and Buzz if it is divisible by 5

# lets identify states

# first there is the waiting state where we dont print anything
waiting = State("...")

# there are also states where we print Fizz and Buzz
fizz = State("Fizz!")
buzz = State("Buzz!")

# since there can be only one state at a given time we need a FizzBuzz state
fizzbuzz = State("FizzBuzz!")

# now the state transitions. They are somewhat more complicated/
# From waiting we can go to either Fizz, Buzz or FizzBuzz, but FizzBuzz
# should always take priority over the other two.
# From fizz or buzz we can go to the other one but not itself (there are
# no two consecutive numbers divisible by the same number) Hence alse FizzBuzz
# will always go to waiting

# Okay, lest get it all going - get the trigger functions

def divisible_3(env):
	return (env.counter % 3) == 0

def divisible_5(env):
	return (env.counter % 5) == 0

def divisible_3_and_5(env):
	return divisible_3(env) and divisible_5(env)

# lets get the environment set up
class Environment:
	def __init__(self):
		self.counter = 0

	# define a function we will call with each tick
	def update(self):
		self.counter+=1

env = Environment()

# tie up the states

waiting.add_transition(Transition(fizz, divisible_3))
waiting.add_transition(Transition(buzz, divisible_5))
waiting.add_transition(Transition(fizzbuzz, divisible_3_and_5), priority=2)
# the last one needs to have higher priority (1 is default)


fizz.add_transition(Transition(buzz, divisible_5))
fizz.set_default(waiting)

buzz.add_transition(Transition(fizz, divisible_3))
buzz.set_default(waiting)

fizzbuzz.set_default(waiting)

# set up FSM

machine = FSM(waiting)

for i in range(32):
	env.update()
	print("Counter = {}, state = {}".format(i, machine.get_state()))
	machine.tick(env)