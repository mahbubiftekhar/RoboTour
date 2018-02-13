#! /usr/bin/python3

import serial

ser = serial.Serial(port='/dev/ttyACM0', baudrate=9600)



def Sonar_read():

	out =' '
	while out[-1] != '\n':
		while ser.inWaiting() > 0:
			out += ser.read(1).decode('ascii')

	if(out != ''):
		reads = out.split(',')

		for r in reads:
			data = r.split(":")
			print(data)
			#print("Sensor {} : {}".format(data[0], data[1]))

if __name__ == "__main__":
	while True:
		Sonar_read()