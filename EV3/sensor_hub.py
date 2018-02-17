#! /usr/bin/python3

import serial

#ser = serial.Serial(port='/dev/ttyACM0', baudrate=9600)

class SensorHub():

	def __init__(self):
		self.baud = 9600
		self.serial_port = serial.Serial(port='/dev/ttyACM0',\
										 baudrate=self.baud)
		self.n_sonars = 4
		self.sonar_maxrange = 255

		self.poll_timeout = 100

		self.__DEBUG__ = False

		self.sensor_values = {}

	def poll(self):
		self.send_request()
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
				out = out[:-2]
				break

		return out if out != '' else None

	def extract_from_frame(self, frame):
		if frame is None:
			return
		try:

			readings = frame.split(',')
			for r in readings:
				data = r.split(':')
				
				self.sensor_values[data[0]] = data[1]
				if self.__DEBUG__:
					print("Sensor {}: {}cm".format(data[0], data[1]))
		except:
			print("Error processing frame: {}".format(frame))
		


if __name__ == "__main__":
	sh = SensorHub()
	while True:
		sh.poll()