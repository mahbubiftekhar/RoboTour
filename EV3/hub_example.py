#! /usr/bin/env python3
from sensor_hub import *

# Sensor hub object that deals with arduino communication
hub = SensorHub()

# instantiate sonar objects pointing to the hub, each with
# its unique name/key.
# keys are 's0', 's1', 's2', 's3'
sonar0 = HubSonar(hub, 's0')
sonar1 = HubSonar(hub, 's1')

while True:
	print("Sonar 0: {}".format(sonar0.value()))
	print("Sonar 1: {}".format(sonar1.value()))