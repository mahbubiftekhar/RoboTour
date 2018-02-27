#! /usr/bin/env python3

from sensor_hub import *
from telemetry import *
import time

sh = SensorHub()
ls = HubLineSensor(sh)
logger = DataLogger("line_sensor_test")
values = {}

def update_values():
	val = ls.raw_values()
	for i in ls.sensor_names:
		values[i] = val[i]

def value_getter(index):
	def ret_val():
		return values[index]
	return ret_val


for i in ls.sensor_names:
	fun = value_getter(i)
	logger.add_channel(DataChannel(i, fun))

logger.init()

# in seconds
seconds = 12

# ish
samples_per_second = 50

total_samples = seconds * samples_per_second
delay = 1/samples_per_second

time_start = time.perf_counter()
for i in range(total_samples):
	update_values()
	logger.log()
	print("Taking sample {}/{}".format(i+1, total_samples))
	time.sleep(delay)

elapsed_time = time.perf_counter()-time_start
print("Done in {:.2f}!".format(elapsed_time))
