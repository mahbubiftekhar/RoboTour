

class FSM():

	def __init__(self, initial_state):

		self.current_state = initial_state

	def tick(self, env):
		next_state = self.current_state.next_state(env)
		self.current_state = next_state
	
	def get_state(self):
		return self.current_state.name

class State():

	def __init__(self, name, default=None):

		# a default state if none of the transitions trigger
		self.default_state = default if default is not None else self
		self.name=name
		self.transitions = []

	def next_state(self, env):

		next_state = self.default_state
		current_priority = 0

		for t in self.transitions:
			# if we have found a transition candidate with higher priority,
			# don't bother checking
			if t.priority < current_priority:
				continue
			# at this stage we know that the potential transition
			# has the highest priority so far

			# if the condition for the state transition is met
			if t.condition(env) == True:
				# make it a current candidate
				current_priority = t.priority
				next_state = t.next_state

		return next_state

	def add_transition(self, transition):
		self.transitions.append(transition)

	def set_default(self, state):
		self.default_state = state

class Transition():

	def __init__(self, next_state, trigger_fun=None, name=None, priority=1):
		self.next_state = next_state
		self.priority = priority
		self.trigger_fun = trigger_fun

		if name is None:
			self.name = "Transition to "+self.next_state.name

	def condition(self, env):
		assert self.trigger_fun is not None, "Please define transition for "+self.name
		return self.trigger_fun(env)


