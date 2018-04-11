#! /usr/bin/env python3

from sensor_hub import *

sh = SensorHub()

tries = 3
while not sh.connected:
    sh.poll()
    tries -= 1
    if tries == 0:
        break       
assert sh.connected, "Can't connect to sensor hub"


sh.__DEBUG__ = True

for i in range(3):
	sh.poll()