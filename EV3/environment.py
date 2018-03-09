
import time, math

class Environment():
	def __init__(self):

		self.line_sens_val = 0
		self.dist_front = 0

		self.clock_ms = 0
		self.clock_start = 0

	def init(self):
		self.clock_init()

	def update(self):
		self.clock_update()

	def clock_init(self):
		self.clock_start = time.perf_counter() * 1000
	def clock_update(self):
		self.clock_ms = (time.perf_counter() * 1000) - self.clock_start