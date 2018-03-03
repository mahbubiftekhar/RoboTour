#! /usr/bin/env python3


import ev3dev.ev3 as ev3
from sensor_hub import *
from model import Model
from environment import Environment

class Robot():
	def __init__(self):
		self.setup_hardware()
		self.hardware_check()

		self.model = Model()
		self.env = Environment()


	def setup_hardware(self):
		self.motorR = ev3.LargeMotor("outD")
		self.motorL = ev3.LargeMotor("outB")

		self.motorPointer = ev3.LargeMotor('outD')

		self.colourSensorR = ev3.ColorSensor("in1") 
		self.colourSensorL = ev3.ColorSensor("in4")

		self.sonarF = ev3.UltrasonicSensor("in2")

		self.hub = SensorHub()

		self.sonarR = HubSonar(self.hub, "s0")
		self.sonarL = HubSonar(self.hub, "s1")


		# setup modes if appropriate
		self.colourSensorR.mode = "COL-REFLECT"
		self.colourSensorL.mode = "COL-REFLECT"

		self.sonarF.mode = "US-DIST-CM"

		self.motorR.stop_action = "hold"
		self.motorL.stop_action = "hold"

	def hardware_check(self):
		if(motorPointer.connected & sonarFront.connected &
		       motorLeft.connected & motorRight.connected):
		    print('All sensors and motors connected')
		else:
		    if(not motorPointer.connected)
		        print("motorPointer not connected")
		    if(not sonarFront.connected):
		        print("Sonar not connected")
		    if(not motorLeft.connected):
		        print("MotorLeft not connected")
		    if(not motorRight.connected):
		        print("MotorRight not connected")
		    if(not colourSensorLeft.connected):
		        print("ColorLeft not connected")
		    if(not colourSensorRight.connected):
		        print("ColorRight not connected")
		    if(not sonarLeft.connected):
		        print("SonarLeft not connected")
		    print('Please check all sensors and actuators are connected.')
		    exit()

	def motor(self, pL, pR):
		self.motorL = pL 
		self.motorR = pR 

