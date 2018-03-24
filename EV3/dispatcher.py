

class Dispatcher():

	def __init__(self, fsm):
		self.fsm = fsm

		self.links = []

		self.last_state = None
		self.last_action = None

	def link_action(self, state, action):
		self.links.append((state, action))

	def determine_action(self):
		target_state = self.fsm.current_state
		
		if target_state == self.last_state:
			return self.last_action

		for (state, action) in self.links:
			if target_state == state:
				self.last_state = state
				self.last_action = action

				return action
		else:
			print("State does not have linked action")
			return None

	def dispatch(self):
		action = self.determine_action()
		if action is not None:
			action()

