

class FSM():

    def __init__(self, initial_state):

        self.current_state = initial_state

    def tick(self, env):
        next_state = self.current_state.next_state(env)

        # if transitioning to a new state
        if self.current_state != next_state:
            # notify the object
            next_state.activate(env)
            self.current_state = next_state
    
    def get_state(self):
        return self.current_state.name

class State():

    def __init__(self, name, default=None):

        # a default state if none of the transitions trigger
        self.default_state = default
        self.name=name
        self.transitions = []
        self.activate_fun = None

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

        # make next state the default one, if it is defined. Otherwise point to self
        return next_state if next_state is not None else self

    def add_transition(self, transition):
        self.transitions.append(transition)

    def set_default(self, state):
        self.default_state = state

    def on_activate(self, fun):
        self.activate_fun = fun

    def activate(self, env):
        for t in self.transitions:
            t.arm(env)
        
        if self.activate_fun is not None:
            self.activate_fun(env)


class Transition():

    def __init__(self, next_state, trigger_fun=None, name=None, priority=1):
        self.next_state = next_state
        self.priority = priority
        self.trigger_fun = trigger_fun
        # self.trigger_fun = trigger_fun if trigger_fun is not None else self.condition
 
        if name is None:
            self.name = "Transition to "+self.next_state.name

    def arm(self, env):
        pass
 
    def condition(self, env):
        # if our trigger function is not defined, assume an always transition
        if self.trigger_fun is None:
            return True
        return self.trigger_fun(env)



