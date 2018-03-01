#! /usr/bin/env python3

from sensor_hub import *
from telemetry import *
import time

sh = SensorHub()
ls = HubLineSensor(sh)
logger = DataLogger("line_sensor_test", folder='./logs/')
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

# in seconds (ish - excludes polling delay)
seconds = 10

# ish
samples_per_second = 25

total_samples = seconds * samples_per_second
delay = 1/samples_per_second

tries = 3
while not sh.connected:
    sh.poll()
    tries -= 1
    if tries == 0:
        break       
assert sh.connected, "Can't connect"

total_poll_time = 0
time_start = time.perf_counter()
for i in range(total_samples):
    update_values()
    logger.log()
    total_poll_time += sh.last_poll_time
    print("Taking sample {}/{}".format(i+1, total_samples))
    time.sleep(delay)

elapsed_time = time.perf_counter()-time_start
print("Done in {:.2f}s!".format(elapsed_time))
print("{:.2f}s total poll time ({:.2f}ms average)!"\
    .format(total_poll_time / 1000, total_poll_time/total_samples))
print("{:.2f}ms other acitiviteis excluding sleep (approx)"\
    .format((elapsed_time-seconds-total_poll_time)/1000))