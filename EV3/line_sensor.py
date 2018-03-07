
from collections import deque 

# TODO:
# consider gaussian modelling of the probabilistic sensor?

class LineDetector():
	
	def __init__(self, updater_function):

		self.updater_function = updater_function


		self.max_val = 255

		self.min_val = 0

		self.val_range = self.max_val - self.min_val

		self.threshold = (self.min_val + self.max_val)/2

		# parameter to differentiate between transitional values and actual line/floor readings
		# percentage of the separation that is required for a reading
		# to qualify to contribute towards the moving average
		# lower value means harder filtering - fewer values get past it
		self.average_margin = 0.8

		self.lo_thresh = self.max_val
		self.hi_thresh = self.min_val

		# differentiate between light-line-on-dark (True) and dark-line-on-light (False) 
		self.line_low = True

		# size of the readings buffer for estimating the threshold
		self.moving_average_size = 10

		# variables for storing extreme values
		self.hi_max = self.min_val
		self.lo_min = self.max_val

		# fixed-size queues for efficient average calculation
		self.hi_vals = deque([self.hi_max], maxlen=self.moving_average_size)
		self.lo_vals = deque([self.lo_min], maxlen=self.moving_average_size)

		self.hi_avg = self.min_val
		self.lo_avg = self.max_val

		self.mode = "NORM"
		self.possible_modes = ["NORM", "RAW", "BOOL"]
		
	def _update(self, new_value):
		self._update_raw_value(new_value)

		self.on_line = self.line_detected()

		self._update_avgs()

		self._calculate_normalised()
	

	def _update_raw_value(self, new_value):
		self.raw_value = new_value

	def _update_avgs(self):

		if self.below_threshold:
			# see if the value qualifies for the moving average
			if self.raw_value < self.lo_thresh:

				# update the moving average FIFO queue and the average
				self.lo_vals.append(self.raw_value)
				self.lo_avg = sum(self.lo_vals)/len(self.lo_vals)
				
				self._update_threshold()
				
				# threshold is calculated as a point between the primary
				# threshold and the average value
				self.lo_thresh = self.lo_avg + \
							self.average_margin * (self.threshold - self.lo_avg)

		else:
			# see if the value qualifies for the moving average
			if self.raw_value < self.hi_thresh:

				# update the moving average FIFO queue and the average
				self.hi_vals.append(self.raw_value)
				self.hi_avg = sum(self.hi_vals)/len(self.hi_vals)
				
				self._update_threshold()
				
				self.hi_thresh = self.hi_avg - \
							self.average_margin * (self.hi_avg - self.threshold)


	def _update_threshold(self):

		# calculate midpoint value between the two averages
		self.threshold = (self.hi_avg + self.lo_avg) / 2

	def line_detected(self):

		self.below_threshold = self.raw_value < self.threshold

		if line_low:
			return self.below_threshold
		else:
			return not self.below_threshold

	def _calculate_normalised(self):

		norm = (self.raw_value - self.lo_avg)/(self.hi_avg - self.lo_avg)*100
		norm = norm if norm < 100 else 100
		norm = norm if norm > 0 else 0

		self.normalised_value = norm
		return norm

	def set_mode(self, mode):
		if mode in self.possible_modes:
			self.mode = mode


	def value(self):
		new_val = self.updater_function()
		self._update(new_val)

		if self.mode == "NORM":
			return self.normalised_value
		elif self.mode == "RAW":
			return self.raw_value
		elif self.mode == "BOOL":
			return self.on_line
		else:
			print("Please don't mess with the internal settings of the class :C")


class SimpleLineDetector():

	def __init__(self, line_low = True):
		self.max_val = 255
		self.min_val = 0

		self.hi_max = self.min_val
		self.lo_min = self.max_val

		self.threshold = (self.hi_max + self.lo_min) / 2

		self.line_low = line_low

		self.line_detected = False

	def update(self, new_value):
		self.raw_value = new_value

		if self.line_low:
			self.line_detected = self.raw_value < self.threshold
		
		else:
			self.line_detected = self.raw_value > self.threshold

		return self.line_detected

	def calibrate(self):
		
		if self.raw_value > self.hi_max:
			self.hi_max = self.raw_value
			self.threshold = (self.hi_max + self.lo_min) / 2

		if self.raw_value < self.lo_min:
			self.lo_min = self.raw_value
			self.threshold = (self.hi_max + self.lo_min) / 2

class LineSensor():

	def __init__(self, hub):

		self.num_detectors = 6
		self.detector_names = ["l{}".format(i)\
		                       for i in range(self.num_detectors)]

		self.line_low = True

		self.hub = hub

		self.last_poll = -1


		self.raw_val = {}
		self.prev_val = {}
		# intialise the sensor readings in the hub if necessary
		for n in self.detector_names:
			if(n not in self.hub.sensor_values.keys()):
				self.hub.sensor_values[n] = 0

		self.detector = {}
		for n in self.detector_names:
			self.detector[n] = SimpleLineDetector(line_low = self.line_low)
			self.detector[n].update(0)

	def calibrate(self):
		for n in self.detector_names:
			self.detector[n].calibrate()


	def raw_values(self):

		# if a new value is requiested
		if self.last_poll == self.hub.last_poll:
			self.hub.poll()

		self.last_poll = self.hub.last_poll
		for n in self.detector_names:
			self.raw_val[n] = self.hub.sensor_values[n]
			self.detector[n].update(self.raw_val[n])

		return self.raw_val 

	def value_simple(self):

		self.raw_values()
		order = self.detector_names
		weight = 10
		
		err = 0
		activated_sens = 0

		for s in order:
			#check if sensor on line
			if self.detector[s].line_detected:
				err += weight
				activated_sens += 1

			weight += 10

		if activated_sens > 0:
			return err/activated_sens
		else:
			return 0