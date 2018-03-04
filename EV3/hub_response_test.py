#! /usr/bin/env python3

from sensor_hub import *
from telemetry import *
from line_sensor import *
import time

'''
This tests  aims to deremine the maxmimum polling speed.
It is to be used to examine the impact of different modifications to the
sensor hub as well as dependency on differnt platforms.
'''

# the sensor setup
sh = SensorHub()
ls = LineSensor(sh)
s0 = HubSonar(sh, 's0')
s1 = HubSonar(sh, 's1')

tries = 3
while not sh.connected:
    sh.poll()
    tries -= 1
    if tries == 0:
        break       
assert sh.connected, "Can't connect"

test_case = input("Test description? >>> ")

test_length = int(input("Test length (s)? >>> "))
delay_s = float(input("Delay (ms)? >>> "))/1000

# set up the logger
test_name = "hub_resp_"+test_case+"_t{}s_d{}ms".format(test_length,delay_s*1000)
# logger = DataLogger(test_name, folder='./logs/') # for ev3
logger = DataLogger(test_name, folder='../tests/logs/') # for PC

# set up callbacks for logging
def get_s0():
	return s0.val

def get_s1():
	return s1.val

def get_poll_time():
	return sh.last_poll_time

logger.add_channel(DataChannel("s0", get_s0))
logger.add_channel(DataChannel("s1", get_s1))
logger.add_channel(DataChannel("poll_time", get_poll_time))

logger.init()

samples_taken = 0
polling_time = 0

start_time = time.perf_counter()
stop_time = start_time + test_length


while time.perf_counter() < stop_time:
	sh.poll()
	logger.log()

	samples_taken += 1
	polling_time += get_poll_time()

	time.sleep(delay_s)

elapsed_time = time.perf_counter() - start_time

print("Time elapsed: {:.3f}s".format(elapsed_time))
print("Samples taken: {} | dropped : {} ({:.2f}%)"\
	.format(samples_taken, sh.dropped_frames,100*sh.dropped_frames/samples_taken))
print("Average poll time: {:.3f}ms".format(polling_time/samples_taken))
print("Polling time: {:.2f}s ({:.3f})%".format(polling_time, polling_time/(elapsed_time*10)))