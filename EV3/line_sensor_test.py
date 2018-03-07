#! /usr/bin/env python3

from sensor_hub import *
import time

sh = SensorHub()
ls = HubLineSensor(sh)

while(1):
	vals = ls.raw_values().values()
	print(vals)
	time.sleep(0.1)