#! /usr/bin/env python3


import ev3dev.ev3 as ev3
from sensor_hub import *
from line_sensor import LineSensor
from model import Model
from environment import Environment

class Robot():
	def __init__(self):
		self.setup_hardware()
		#self.hardware_check()

		self.model = Model()
		self.env = Environment()


	def setup_hardware(self):
		self.motorR = ev3.LargeMotor("outD")
		self.motorL = ev3.LargeMotor("outB")

		self.motorPointer = ev3.LargeMotor("outC")

		self.colourSensorR = ev3.ColorSensor("in1") 
		self.colourSensorL = ev3.ColorSensor("in4")

		self.sonarF = ev3.UltrasonicSensor("in2")

		self.hub = SensorHub()

		self.sonarR = HubSonar(self.hub, "s0")
		self.sonarL = HubSonar(self.hub, "s1")

		self.line_sensor = LineSensor(self.hub)


		# setup modes if appropriate
		self.colourSensorR.mode = "COL-REFLECT"
		self.colourSensorL.mode = "COL-REFLECT"

		self.sonarF.mode = "US-DIST-CM"

		self.motorR.stop_action = "hold"
		self.motorL.stop_action = "hold"

	def hardware_check(self):
		pass	

	def update_env(self):

		self.env.line_sens_val = self.line_sensor.value_simple()
		
		for n in self.line_sensor.detector:
			self.env.sees_line[n] = self.line_sensor.detector[n].line_detected

		self.env.colour_left   = self.colourSensorL.value()
		self.env.colour_right  = self.colourSensorR.value()

		self.env.dist_front = self.sonarF.value() 

		self.env.dist_right = self.sonarR.value() * 10
		self.env.dist_left  = self.sonarL.value() * 10

		self.env.rot_right = self.motorR.position
		self.env.rot_left  = self.motorL.position



		self.env.update()


	def motor(self, pL, pR):
		self.motorL.run_forever(speed_sp = pL) 
		self.motorR.run_forever(speed_sp = pR)

	def rotate(self, degrees, speed):
		# calculate by how much the wheels need to rotate
		delta = degrees * self.model.wheel_to_rotation_ratio
		
		# rotate right
		if degrees > 0:
			delta_l =  delta
			delta_r = -delta
		# rotate left
		else:
			delta_l = -delta
			delta_r =  delta

		self.motorL.run_to_rel_pos(speed_sp = speed, position_sp = delta_l)
		self.motorR.run_to_rel_pos(speed_sp = speed, position_sp = delta_r)


	# check if any motors are doing something
	def done_movement(self, env):
		return not (self.motorL.is_running or self.motorR.is_running)

	def stop(self):
		self.motorL.stop()
		self.motorR.stop()

