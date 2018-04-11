#! /usr/bin/env python3

import ev3dev.ev3 as ev3
import time
from sensor_hub import SensorHub, HubLineSensor

class PIDController:
	def __init__(self, kp, ki, kd):
		self.kp = kp
		self.ki = ki
		self.kd = kd

		self.last_error = 0
		self.set_value = 0
		self.i = 0

		self.max_i = 1000

	def calculate(self, current_value):

		error = self.set_value - current_value
		self.p =  error
		self.i += error
		self.d =  error - self.last_error

		self.i = self.i if self.i <  self.max_i else  self.max_i
		self.i = self.i if self.i > -self.max_i else -self.max_i

		self.last_error = error

		return self.p*self.kp \
			+ self.i*self.ki \
			+ self.d*self.kd

#TODO: consider extracting the sensors to classes
#TODO: consider extracting the motors  to classes
class Robot:

	def __init__(self):
		self.state = 'onLine'

		self.motorR = ev3.LargeMotor('outD')
		self.motorL = ev3.LargeMotor('outB')

		self.hub = SensorHub()
		self.sensor = HubLineSensor(self.hub)

		self.pid = PIDController(2.25, 0.01, 1.5)
		self.pid.set_value = 35

		
		self.maxSpeed = 125
		self.maxDrive = 600


	def followLine(self):

		current_value = self.sensor.value_simple()
		steer = -self.pid.calculate(current_value)

		steer_right = self.maxSpeed - steer
		steer_left = self.maxSpeed + steer
		print("{:.1f} {:.1f} {:.0f} {:.0f} {}".format(current_value, \
		steer, steer_right, steer_left, \
		[self.sensor.raw_val[n] for n in ['l1','l0','l2','l3','l4','l5']]))

		self.motorR.run_forever(speed_sp = steer_right)
		self.motorL.run_forever(speed_sp = steer_left)
		


robot = Robot()
direction = True
while True:
	robot.followLine()
	time.sleep(0.001)


