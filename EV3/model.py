
import math

class Model():
	def __init__(self):
		# units in mm

		self.wheel_diameter = 81.8
		self.wheel_separation = 165

		# circumference of the rotation in place
		self.rotation_circumference = math.pi * self.wheel_separation 
		self.wheel_circumference = math.pi * self.wheel_diameter

		self.mm_per_degree = self.wheel_circumference / 360

		# how many times a wheel has to rotate per one robot rotation
		self.wheel_to_rotation_ratio = \
			self.rotation_circumference / self.wheel_circumference

		# required distance not to hit anything if rotationg in place
		self.rotation_minimal_spacing =  200

		self.pointer_gear_ratio = -4.8