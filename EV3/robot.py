#! /usr/bin/env python3


import ev3dev.ev3 as ev3
from sensor_hub import *
from line_sensor import LineSensor
from model import Model
from environment import Environment

class Robot():
	def __init__(self, fast_hub=False):
		self.fast_hub = fast_hub
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

		if self.fast_hub:
			self.hub = SensorHubFast()
		else:
			self.hub = SensorHub()

		self.sonarR = HubSonar(self.hub, "s1")
		self.sonarL = HubSonar(self.hub, "s0")

		self.line_sensor = LineSensor(self.hub)

		self.button = ev3.Button()

		self.LED = ev3.Leds()

		# setup modes if appropriate
		self.colourSensorR.mode = "COL-REFLECT"
		self.colourSensorL.mode = "COL-REFLECT"

		self.sonarF.mode = "US-DIST-CM"

		self.motorR.stop_action = "hold"
		self.motorL.stop_action = "hold"

		self.sound = ev3.Sound

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
		self.motorL.run_timed(speed_sp = pL, time_sp=300) 
		self.motorR.run_timed(speed_sp = pR, time_sp=300)

	def rotate(self, degrees, speed):
		# calculate by how much the wheels need to rotate
		delta = degrees * self.model.wheel_to_rotation_ratio
		

		delta_l =  delta
		delta_r = -delta
		print("ROTATING: {} {}".format(delta_l, delta_r))

		self.motorL.run_to_rel_pos(speed_sp = speed, position_sp = delta_l)
		self.motorR.run_to_rel_pos(speed_sp = speed, position_sp = delta_r)

	def indicate_error(self):
		self.LED.set_color(self.LED.LEFT, self.LED.RED)
		self.LED.set_color(self.LED.RIGHT, self.LED.RED)

		self.sound.beep('r 3')

	def indicate_zero(self):
		self.LED.set_color(self.LED.LEFT,  self.LED.AMBER)
		self.LED.set_color(self.LED.RIGHT, self.LED.AMBER)
		pass
		

	def indicate_one(self):
		self.LED.set_color(self.LED.LEFT,  self.LED.GREEN)
		self.LED.set_color(self.LED.RIGHT, self.LED.RED)
		pass

	def indicate_two(self):
		self.LED.set_color(self.LED.LEFT,  self.LED.GREEN)
		self.LED.set_color(self.LED.RIGHT, self.LED.GREEN)
		pass

	# check if any motors are doing something
	def done_movement(self, env):
		return not (self.motorL.is_running or self.motorR.is_running)

	def stop(self):
		self.motorL.stop()
		self.motorR.stop()

