
from finite_state_machine import *
from dispatcher import Dispatcher
from telemetry import *


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

		# print(current_value)
		self.robot.motor(steer_left, steer_right)




class Calibration(Algorithm):
	def __init__(self, robot):
		Algorithm.__init__(self, robot)

		self.calibration_time = 5000
		sweep_time = self.calibration_time/4

		self.st_start = State("Start")
		self.st_calibrate_right = State("right")
		self.st_calibrate_left = State("left")
		self.st_calibrate_centre = State("recentre")

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
		# print(self.fsm.get_state())
		self.dsp.dispatch()

	def done(self, env):
		return self.fsm.current_state == self.st_done


# class Branching(Algorithm):
# 	def __init__(self, robot):
# 		Algorithm.__init__(self, robot)

# 		st_start = State("Start")

# 		st_turning = State("Turning")
# 		st_clearing = State("Moving away from branch")


# 	def run(self):
# 		self.fsm.tick(self.env)
# 		self.dsp.dispatch()

# 	def done(self, env):
# 		reurn self.fsm.current_state == self.st_done



class ObstacleAvoidance(Algorithm):
	def __init__(self, robot, server):
		Algorithm.__init__(self, robot)
		
		self.server = server
		
		self.base_speed = 100
		self.rotation_speed = 125
		self.recentre_speed = 90
		self.follow_distance = 250
		
		self.pid = PIDController(0.025, 0.0001, 0.013)
		self.pid_set_value = self.follow_distance

		self.logger = None


		self.st_start = State("Start")
		self.st_init = State("Init ObstacleAvoidance")
		self.st_stop_and_wait_white = State("Obstacle at white line")
		self.st_update_server_white = State("No Obstacle at white")
		self.st_turn = State("Turn")
		self.st_follow_around = State("Wall following")
		self.st_go_through = State("Passing over the line")
		self.st_recentre = State("Reinitialising")
		self.st_done = State("Done")
		self.st_at_black_line = State("Detected black line")
		self.st_align_with_black = State("Aligning with black line")
		self.st_pass_through_black = State("Passing through the black line")
		self.st_turn_at_black_line = State("Turn and face to the obstacle")
		self.st_update_and_continue = State("Miss a branch and update position")
		self.st_follow_black_line = State("Follow black line")
		self.st_stop_and_wait_black = State("Obstacle at black line")
		self.st_update_server_black = State("No Obstacle at black")

		self.st_start.add_transition(Transition(self.st_init))
		self.st_init.add_transition(Transition(self.st_turn))
		self.st_init.add_transition(Transition(self.st_stop_and_wait_white, self.is_stop))

		self.st_stop_and_wait_white.add_transition(Transition(self.st_update_server_white, self.not_obstacle_in_front))
		self.st_update_server_white.add_transition(Transition(self.st_done))

		self.st_turn.add_transition(Transition(self.st_follow_around, self.robot.done_movement))
		
		self.st_follow_around.add_transition(Transition(self.st_go_through, self.line_detected))
		self.st_follow_around.add_transition(Transition(self.st_align_with_black, self.black_line_detected))
		
		self.st_align_with_black.add_transition(Transition(self.st_at_black_line, self.aligned_with_black))

		self.st_go_through.add_transition(TransitionTimed(1000, self.st_recentre))

		self.st_recentre.add_transition(Transition(self.st_done, self.recentered))

		
		self.st_at_black_line.add_transition(Transition(self.st_pass_through_black))
		self.st_at_black_line.add_transition(Transition(self.st_update_and_continue, self.is_forward))

		self.st_pass_through_black.add_transition(Transition(self.st_turn_at_black_line, self.no_black_line))

		self.st_turn_at_black_line.add_transition(Transition(self.st_follow_black_line, self.robot.done_movement))

		self.st_update_and_continue.add_transition(Transition(self.st_follow_around, self.no_black_line))

		self.st_follow_black_line.add_transition(Transition(self.st_stop_and_wait_black, self.obstacle_in_front))
		self.st_follow_black_line.add_transition(Transition(self.st_done, self.line_detected))
		
		self.st_stop_and_wait_black.add_transition(Transition(self.st_update_server_black, self.not_obstacle_in_front))
		self.st_update_server_black.add_transition(Transition(self.st_follow_black_line))

		

		self.fsm = FSM(self.st_start)
		self.dsp = Dispatcher(self.fsm)
	
		self.dsp.link_action(self.st_init, self.initialise)
		self.dsp.link_action(self.st_stop_and_wait_white, self.stop_and_wait)
		self.st_stop_and_wait_white.on_activate(self.speak_obstacle_in_front)
		self.dsp.link_action(self.st_update_server_white, self.server.set_obstacle_false)
		# use as a one-shot action
		self.st_turn.on_activate(self.turn_away)
		self.dsp.link_action(self.st_follow_around, self.follow_wall)
		self.dsp.link_action(self.st_go_through, self.robot.go_forward)
		self.st_turn_at_black_line.on_activate(self.turn_at_black_line)
		self.dsp.link_action(self.st_update_and_continue, self.follow_wall)
		self.st_update_and_continue.on_activate(self.update_and_continue)
		self.dsp.link_action(self.st_align_with_black, self.align_with_black_line)
		self.dsp.link_action(self.st_pass_through_black, self.robot.go_forward)
		self.dsp.link_action(self.st_follow_black_line, self.follow_black_line)
		self.dsp.link_action(self.st_stop_and_wait_black, self.stop_and_wait)
		self.st_stop_and_wait_black.on_activate(self.speak_obstacle_in_front)
		self.dsp.link_action(self.st_update_server_black, self.server.set_obstacle_false)
		self.dsp.link_action(self.st_recentre, self.recentre)
		self.dsp.link_action(self.st_done, self.finish)

	def reset(self):
		self.fsm.reset()

	def initialise(self):
		self.robot.stop()
		self.avoidance_direction = self.robot.env.avoidance_direction
		if self.robot.get_position_from_branch() <= 100:
			self.avoidance_direction = 'stop'
		self.pid.reset()
		self.logger = DataLogger("it_obstacle_"+self.avoidance_direction, folder='../logs/',)
		self.logger.lines_per_write = 1000

		def right_hook():
			return self.env.dist_right
		def left_hook():
			return self.env.dist_left
		def rot_left_hook():
			return self.env.rot_left
		def rot_right_hook():
			return self.env.rot_right
		def sees_line_hook():
			return sum(self.env.sees_line.values())

		self.logger.add_channel(DataChannel("sensor_right", right_hook))
		self.logger.add_channel(DataChannel("sensor_left", left_hook))
		self.logger.add_channel(DataChannel("line_sensor", sees_line_hook))
		# self.logger.add_channel(DataChannel("left_wheel", rot_left_hook))
		# self.logger.add_channel(DataChannel("right_wheel", rot_right_hook))

		self.logger.init()


	def turn_away(self):
		
		print("turning:",self.avoidance_direction)
		if self.avoidance_direction == 'left':
			angle = -90
			self.robot.rotate(angle, speed=self.rotation_speed)
		else:
			angle = 90
			self.robot.rotate(angle, speed=self.rotation_speed)


	def follow_wall_2(self):

		if self.avoidance_direction == 'left':
			# following towards left, follow wall with ride side
			steer = self.pid.calculate(self.env.dist_right)
			steer_left = self.base_speed  + steer
			steer_right = self.base_speed - steer
		else:
			# following towards right, follow wall with left side
			steer = self.pid.calculate(self.env.dist_left)
			steer_left = self.base_speed  - steer
			steer_right = self.base_speed + steer

		self.robot.motor(steer_left, steer_right)

	def follow_wall(self):
		spd = 0.5
		if self.env.dist_front < 200:
			# if somethings in front then stop?
			self.robot.stop()
			
		elif self.avoidance_direction == 'left':
			if self.env.dist_right < 110:
				self.robot.motor(50*spd, 150*spd)
			elif self.env.dist_right > 150:
				self.robot.motor(250*spd, 100*spd)
			elif self.env.dist_right > 110:
				self.robot.motor(100*spd, 50*spd)
			else:
				self.robot.motor(100*spd, 100*spd)
		else:
			if self.env.dist_left < 110:
				self.robot.motor(150*spd, 50*spd)
			elif self.env.dist_left > 150:
				self.robot.motor(100*spd, 250*spd)
			elif self.env.dist_left > 110:
				self.robot.motor(50*spd, 100*spd)
			else:
				self.robot.motor(100*spd, 100*spd)

	def align_with_black_line(self):
		if self.env.colour_left == 1 and self.env.colour_right != 1:
			self.robot.motor(-100, 100)
		if self.env.colour_right == 1 and self.env.colour_left != 1:
			self.robot.motor(100,-100)
		if self.env.colour_left != 1 and self.env.colour_right != 1:
			self.robot.motor(100,100)

	def follow_black_line(self):
		self.robot.motor(100, 100) 

	def line_detected(self, env):
		return True in self.env.sees_line.values()
		# return self.env.colour_left  == 6 or \
		# 	   self.env.colour_right == 6

	def recentre(self):
		if self.avoidance_direction == 'left':
			self.robot.motor(-self.recentre_speed,  self.recentre_speed)
		else:
			self.robot.motor( self.recentre_speed, -self.recentre_speed)

	def finish(self):
		self.robot.stop()
		
		if self.logger is not None:
			self.logger.write_buffer()


	def aligned_with_black(self, env):
		return self.env.colour_left  == 1 and \
			   self.env.colour_right == 1

	def black_line_detected(self, env):
		return self.env.colour_left  == 1 or \
			   self.env.colour_right == 1
	def no_black_line(self, env):
		return not self.black_line_detected(env)

	def is_stop(self, env):
		return self.avoidance_direction == 'stop'

	def is_forward(self, env):
		if env.pictures_to_go:
			# if next position is painting that means this is painting
			# happened in tour
			if env.next_position == env.pictures_to_go[0]: 
				return False
			else:
				if env.orientation == env.orientation_map[(env.next_position, env.positions_list[1])][0]:
					env.orientation = env.orientation_map[(env.next_position, env.positions_list[1])][-1]
					return True
				else:
					return False
		else:
			# if next turn is same orientation thet means is forward, return true
			# happened when go back to exit
			if env.next_position=='10':
				return False
			else:
				if env.orientation == env.orientation_map[(env.next_position, env.positions_list[1])][0]:
					env.orientation = env.orientation_map[(env.next_position, env.positions_list[1])][-1]
					return True
				else:
					return False

	def is_not_forward(self,env):
		return not self.is_forward(env)

	def update_and_continue(self):
		print("popping on black")
		self.env.position = self.env.positions_list.pop(0)

		self.env.next_position = self.env.positions_list[0]
		self.robot.reset_position_at_branch()

	def turn_at_black_line(self):
		if self.avoidance_direction == 'left':
			angle = 90
			self.robot.rotate(angle, speed=self.rotation_speed)
			self.robot.wait_for_motor()

			if self.env.orientation == 'N':
				self.env.orientation = 'E'
			elif self.env.orientation == 'E':
				self.env.orientation = 'S'
			elif self.env.orientation == 'S':
				self.env.orientation = 'W'
			elif self.env.orientation == 'W':
				self.env.orientation = 'N'
				
		elif self.avoidance_direction == 'right':
			angle = -90
			self.robot.rotate(angle, speed=self.rotation_speed)
			self.robot.wait_for_motor()

			if self.env.orientation == 'N':
				self.env.orientation = 'W'
			elif self.env.orientation == 'W':
				self.env.orientation = 'S'
			elif self.env.orientation == 'S':
				self.env.orientation = 'E'
			elif self.env.orientation == 'E':
				self.env.orientation = 'N'

	def obstacle_in_front(self, env):
		return env.dist_front < 200

	def not_obstacle_in_front(self,env):
		return not self.obstacle_in_front(env)

	def stop_and_wait(self):
		self.robot.stop()

	def recentered(self, env):
		return abs(self.env.line_sens_val - 35) <= 5

	def speak_obstacle_in_front(self):
		self.server.set_obstacle_true()
		self.robot.speak('Please remove the obstacle in front of me! Thank you!')

	def run(self):
		self.fsm.tick(self.env)
		# print(self.fsm.get_state())
		self.dsp.dispatch()	
		self.logger.log()

	def done(self, env):
		return self.fsm.current_state == self.st_done

		


