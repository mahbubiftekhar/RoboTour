
from finite_state_machine import *
from dispatcher import Dispatcher

class Algorithm():

	def __init__(self, robot):
		self.robot = robot
		self.env = self.robot.env

	def run(self):
		pass


class PIDController:
	def __init__(self, kp, ki, kd):
		self.kp = kp
		self.ki = ki
		self.kd = kd

		self.last_error = 0
		self.set_value = 0
		self.i = 0

		self.max_i = 1000

	def calculate(self, current_value):

		error = self.set_value - current_value
		self.p =  error
		self.i += error
		self.d =  error - self.last_error

		self.i = self.i if self.i <  self.max_i else  self.max_i
		self.i = self.i if self.i > -self.max_i else -self.max_i

		self.last_error = error

		return self.p*self.kp \
			+ self.i*self.ki \
			+ self.d*self.kd

	def reset(self):
		self.last_error = 0
		self.i = 0

class LineFollowing(Algorithm):

	def __init__(self, robot):
		Algorithm.__init__(self, robot)
		self.pid = PIDController(2.25, 0, 1.5)

		self.pid.set_value = 35

		self.base_speed = 125

		self.steer = 0

	def set_gains(self, kp, ki, kd):
		self.pid.kp = kp
		self.pid.ki = ki
		self.pid.kd = kd

	def run(self):
		current_value = self.robot.env.line_sens_val
		self.steer = -self.pid.calculate(current_value)

		steer_right = self.base_speed - self.steer
		steer_left = self.base_speed + self.steer

		self.robot.motor(steer_left, steer_right)




class Calibration(Algorithm):
	def __init__(self, robot):
		Algorithm.__init__(self, robot)

		self.calibration_time = 8000
		sweep_time = self.calibration_time/4

		self.st_start = State("Start")
		self.st_calibrate_right = State("Calibrating")
		self.st_calibrate_left = State("Calibrating")
		self.st_calibrate_centre = State("Calibrating")

		self.st_done = State("Done")

		self.st_start.add_transition(Transition(self.st_calibrate_right))
		self.st_calibrate_right.add_transition(TransitionTimed(sweep_time,  self.st_calibrate_left))
		self.st_calibrate_left.add_transition(TransitionTimed(2*sweep_time, self.st_calibrate_centre))
		self.st_calibrate_centre.add_transition(TransitionTimed(sweep_time, self.st_done))

		self.fsm = FSM(self.st_start)
		self.dsp = Dispatcher(self.fsm)

		self.dsp.link_action(self.st_calibrate_right, self.calibrate_right)
		self.dsp.link_action(self.st_calibrate_left,  self.calibrate_left)
		self.dsp.link_action(self.st_calibrate_centre, self.calibrate_right)
		self.dsp.link_action(self.st_done, self.robot.stop)



	def calibrate_right(self):
		self.robot.motor(100, -100)
		self.robot.line_sensor.calibrate()

	def calibrate_left(self):
		self.robot.motor(-100, 100)
		self.robot.line_sensor.calibrate()

	def run(self):
		self.fsm.tick(self.env)
		print(self.fsm.get_state())
		self.dsp.dispatch()

	def done(self, env):
		return self.fsm.current_state == self.st_done



class ObstacleAvoidance(Algorithm):
	def __init__(self, robot):
		Algorithm.__init__(self, robot)

		self.base_speed = 100
		self.rotation_speed = 145
		self.recentre_speed = 90
		self.follow_distance = 200
		
		self.pid = PIDController(0.25, 0.001, 0.13)
		self.pid_set_value = self.follow_distance



		self.st_init = State("Init ObstacleAvoidance")
		self.st_turn = State("Turn")
		self.st_follow_around = State("Wall following")
		self.st_recentre = State("Reinitialising")
		self.st_done = State("Done")

		self.st_init.add_transition(Transition(self.st_turn))
		self.st_turn.add_transition(Transition(self.st_follow_around, self.robot.done_movement))
		self.st_follow_around.add_transition(Transition(self.st_recentre, self.recentered))
		self.st_recentre.add_transition(Transition(self.st_done))

		self.fsm = FSM(self.st_init)
		self.dsp = Dispatcher(self.fsm)
	
		self.dsp.link_action(self.st_init, self.initialise)
		# use as a one-shot action
		self.st_turn.on_activate(self.turn_away)
		self.dsp.link_action(self.st_follow_around, self.follow_wall)
		self.dsp.link_action(self.st_recentre, self.recentre)
		self.dsp.link_action(self.st_done, self.robot.stop)

	def initialise(self):
		self.robot.stop()
		self.avoidance_direction = self.env.avoidance_direction
		self.pid.reset()


	def turn_away(self):

		if self.avoidance_direction == 'left':
			angle = -90
		else:
			angle =  90

		self.robot.rotate(angle, speed=self.rotation_speed)


	def follow_wall(self):

		if self.avoidance_direction == 'left':
			# following towards left, follow wall with ride side
			steer = self.pid.calculate(self.env.dist_right)
			steer_left = self.base_speed  - steer
			steer_right = self.base_speed + steer
		else:
			# following towards right, follow wall with left side
			steer = self.pid.calculate(self.env.dist_left)
			steer_left = self.base_speed  + steer
			steer_right = self.base_speed - steer

		self.robot.motor(steer_left, steer_right)


	def recentre(self):
		if self.avoidance_direction == 'left':
			self.robot.motor(-self.recentre_speed,  self.recentre_speed)
		else:
			self.robot.motor( self.recentre_speed, -self.recentre_speed)

	def back_on_line(self):
		return self.env.colour_left  > self.env.line_thershold or \
			   self.env.colour_right > self.env.line_thershold

	def recentered(self):
		return abs(self.env.line_sens_val - 35) < 15






	def run(self):
		self.fsm.tick(self.env)
		self.dsp.dispatch()	

	def done(self, env):
		return self.fsm.current_state == self.st_done

		


