
import time, math

class Environment():
	def __init__(self):

		# self.floor_value = 
		self.line_threshold = 57
		self.black_threshold = 15

		self.line_sens_val = 0
		self.colour_left = 0
		self.colour_right = 0
		
		self.dist_front = 0
		self.dist_right = 0
		self.dist_left  = 0

		self.rot_right = 0
		self.rot_left  = 0


		self.clock_ms = 0
		self.clock_start = 0

		self.last_rot_right = 0
		self.last_rot_left = 0

		self.avoidance_direction = 'left'


	def init(self):
		self.clock_init()

	def update(self):
		self.last_rot_left = self.rot_left
		self.last_rot_right = self.rot_right
		self.clock_update()

	def clock_init(self):
		self.clock_start = time.perf_counter() * 1000
	def clock_update(self):
		self.clock_ms = (time.perf_counter() * 1000) - self.clock_start