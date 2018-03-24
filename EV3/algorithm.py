
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

		self.calibraton_time = 8000
		sweep_time = self.calibration_time/4

		self.st_calibrate_right = State("Calibrating")
		self.st_calibrate_left = State("Calibrating")
		self.st_calibrate_centre = State("Calibrating")

		self.st_done = State("Done")

		self.st_calibrate_right.add_transition(TransitionTimed(sweep_time,  self.st_calibrate_left))
		self.st_calibrate_left.add_transition(TransitionTimed(2*sweep_time, self.st_calibrate_centre))
		self.st_calibrate_centre.add_transition(TransitionTimed(sweep_time, self.st_done))

		self.fsm = FSM(self.st_calibrate_right)
		self.dsp = Dispatcher(self.fsm)

		self.dsp.link_action(self.st_calibrate_right, self.calibrate_right)
		self.dsp.link_action(self.st_calibrate_left,  self.calibrate_left)
		self.dsp.link_action(self.st_calibrate_centre, self.st_calibrate_centre)



	def calibrate_right(self):
		self.robot.motor(100, -100)
		self.robot.line_sensor.calibrate()

	def calibrate_left(self):
		self.robot.motor(-100, 100)
		self.robot.line_sensor.calibrate()

	def run(self):
		self.fsm.tick(self.env)
		self.dsp.dispatch()

	def done(self, env):
		return self.fsm.current_state == self.st_done

