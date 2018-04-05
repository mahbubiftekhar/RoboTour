#! /usr/bin/env python3


import ev3dev.ev3 as ev3
import math
from sensor_hub import *
from line_sensor import LineSensor
from model import Model
from environment import Environment
from comms import *

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
		self.colourSensorR.mode = "COL-COLOR"
		self.colourSensorL.mode = "COL-COLOR"

		self.sonarF.mode = "US-DIST-CM"

		self.motorR.stop_action = "hold"
		self.motorL.stop_action = "hold"

		self.sound = ev3.Sound()

		self.position_from_branch = 0

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

		self.odometry_step()
		self.env.update()

	def odometry_step(self):	
		r = self.env.rot_right - self.env.last_rot_right
		l = self.env.rot_left  - self.env.last_rot_left

		r *= self.model.mm_per_degree
		l *= self.model.mm_per_degree

		omega = (r-l) / self.model.wheel_separation
		if abs(omega) > 0.1:
		    radius = r / omega - self.model.wheel_separation/2
		    dx = radius * 1-math.cos(omega)
		    dy = radius * math.sin(omega)
		else:
		    dx = (r+l)/2
		    dy = 0
		# 
		tdx = dx * math.cos(self.env.angle) - dy * math.sin(self.env.angle)
		tdy = dx * math.sin(self.env.angle) + dy * math.cos(self.env.angle)
		# 
		self.env.angle += omega
		self.env.x += tdx
		self.env.y += tdy


	def pointer_motor(self, degrees, speed):
		delta = degrees * self.model.pointer_gear_ratio
		self.motorPointer.run_to_rel_pos(speed_sp = speed, position_sp = delta)

	def motor(self, pL, pR):
		self.motorL.run_timed(speed_sp = pL, time_sp=300) 
		self.motorR.run_timed(speed_sp = pR, time_sp=300)

	def go_forward(self, speed=125):
		self.motor(speed, speed)

	def rotate_branch(self, degrees, speed):
		# rotate at the midpoint between cetnre and wheel
		# outer wheel circle radius - 3/4 of wheel separation
		# inner wheel circle radius - 1/4 of wheel separation

		rotation_outer = 1.5 * self.model.wheel_to_rotation_ratio * abs(degrees)
		rotation_inner = 0.5 * self.model.wheel_to_rotation_ratio * abs(degrees)

		# if turn right, move left wheel forward 
		if degrees > 0: 
			delta_l =  rotation_outer
			delta_r = -rotation_inner
			speed_l = 1.5 * speed
			speed_r = 0.5 * speed
		else:
			delta_l = -rotation_inner
			delta_r =  rotation_outer
			speed_l = 0.5 * speed
			speed_r = 1.5 * speed



		self.motorL.run_to_rel_pos(speed_sp = speed_l, position_sp = delta_l)
		self.motorR.run_to_rel_pos(speed_sp = speed_r, position_sp = delta_r)

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

		self.speak("Hub not responsive!")

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

	def wait_for_motor(self):
		self.motorL.wait_until_not_moving()
		self.motorR.wait_until_not_moving()

	def reset_position_at_branch(self):
		self.position_from_branch = self.motorR.position

	def get_position_from_branch(self):
		return self.motorR.position - self.position_from_branch

	# check if any motors are doing something
	def done_movement(self, env):
		return not (self.motorL.is_running or self.motorR.is_running)

	def speak(self, string):
		self.sound.speak(string).wait()

	def stop(self):
		self.motorL.stop()
		self.motorR.stop()

