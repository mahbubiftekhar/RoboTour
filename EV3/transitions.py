
from finite_state_machine import Transition
from collections import deque

class OnBranch(Transition):

	def __init__(self, next_state, priority=1):
		Transition.__init__(self, next_state, priority=priority)
		self.window_width = 3
		self.window = {}
		self.sensors_to_trigger = 3

	def arm(self, env):
		for n in env.sees_line:
			v = env.sees_line[n]
			self.window[n] = deque([v], maxlen=self.window_width)

	def condition(self, env):

		activated_sensors_in_window = 0
		for n in env.sees_line:
			v = env.sees_line[n]
			self.window[n].append(v)
			if True in self.window[n]:
				activated_sensors_in_window += 1

		# print(activated_sensors_in_window, end=' ')
		return activated_sensors_in_window >= self.sensors_to_trigger
