#! /usr/bin/python3

import serial
import time
#ser = serial.Serial(port='/dev/ttyACM0', baudrate=9600)

class SensorHub():

	def __init__(self):
		self.baud = 57600
		self.serial_port = serial.Serial(port='/dev/ttyACM0',\
										 baudrate=self.baud)
		self.n_sonars = 4
		self.sonar_maxrange = 255

		self.tries_limit = 1000

		self.__DEBUG__ = False

		self.sensor_values = {}

		self.last_poll = 0

		self.poll()

	def poll(self):
		self.last_poll += 1

		tries = 0
		self.serial_port.flushInput()
		while self.serial_port.inWaiting() < 1:
			self.send_request()
			time.sleep(0.001)
			if(tries >= self.tries_limit):
				print("POLLING TIMEOUT")
				return False
			tries += 1

		frame = self.get_frame()
		self.extract_from_frame(frame)

	def send_request(self):
		self.serial_port.write(b'r\n')
		if self.__DEBUG__:
			print("sending request")

	def get_frame(self):
		out = ''
		while self.serial_port.inWaiting() > 0:
			out += self.serial_port.read(1).decode('ascii')
			if(out[-1] == '\n'):
				# remove newline and last comma
				out = out[:-2]
				break

		return out if out != '' else None

	def extract_from_frame(self, frame):
		if frame is None:
			return
		try:

			# data comes in as 'id0:val0,id1:val1,[...]'
			# split to get identifier-value pairs
			readings = frame.split(',')
			for r in readings:
				#split each pair
				data = r.split(':')
				
				self.sensor_values[data[0]] = data[1]
				if self.__DEBUG__:
					print("Sensor {}: {}cm".format(data[0], data[1]))
		except:
			print("Error processing frame: {}".format(frame))
		

class HubSonar():

	def __init__(self, hub, name):
		self.hub = hub
		self.hub_key = name
		self.last_poll = -1
		self.maxrange = self.hub.sonar_maxrange

		self.hub.sensor_values[name] = 0

	def value(self):
		# if a new value is requested
		if self.last_poll == self.hub.last_poll:
			self.hub.poll()

		self.last_poll = self.hub.last_poll
		self.val = self.hub.sensor_values[self.hub_key]
		self.val = 0 if self.val == 0 else self.maxrange
		return self.val

class HubLineSensor():
	def __init__(self, hub):
		self.num_sensors = 6
		self.sensor_names = ["l{}".format(i) for i in range(self.num_sensors)]

		self.hub = hub

		self.last_poll = -1

		self.raw_val = {}
		self.prev_val = {}
		# intialise the sensor readings
		for n in self.sensor_names:
			self.hub.sensor_values[n] = 0


	def raw_values(self):

		# if a new value is requiested
		if self.last_poll == self.hub.last_poll:
			self.hub.poll()

		self.last_poll = self.hub.last_poll
		for n in self.sensor_names:
			self.raw_val[n] = self.hub.sensor_values[n]

		return self.raw_val 

	def value_simple(self):

		self.raw_values()
		order = ['l0', 'l1', 'l2', 'l3', 'l4', 'l5']

		weight = 10
		floor = 235
		line = 185
		threshold = (floor+line)/2

		err = 0
		activated_sens = 0

		for s in order:
			try:
				val = int(self.raw_val[s])
			except:
				val = floor
				print("GLITTCHEZZZ BITCHEZZ: {}".format(self.raw_val[s])) 

			#check if sensor on line
			if val < threshold:
				err += weight
				activated_sens += 1

			weight += 10

		if activated_sens > 0:
			return err/activated_sens
		else:
			return 0 


if __name__ == "__main__":
	sh = SensorHub()
	while True:
		sh.poll()