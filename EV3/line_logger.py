#! /usr/bin/env python3

from sensor_hub import *
from line_sensor import *
from telemetry import *
import time

sh = SensorHub()
ls = LineSensor(sh)
logger = DataLogger("line_sensor_test", folder='../tests/logs/')
values = {}


def update_values():
    global outa
    outa = ls.value_simple()
    val = ls.raw_val
    for i in ls.detector_names:
        values[i] = val[i]
        # values[i] = ls.detector[i].threshold

def value_getter(index):
    def ret_val():
        return values[index]
    return ret_val

def threshold_getter(index):
    def ret_val():
        return ls.detector[index].threshold
    return ret_val

def get_out():
    return outa


for i in ls.detector_names:
    fun = value_getter(i)
    logger.add_channel(DataChannel(i, fun))

for i in ls.detector_names:
    fun = threshold_getter(i)
    logger.add_channel(DataChannel(i+'th', fun))

logger.add_channel(DataChannel("output", get_out))

logger.init()

# in seconds (ish - excludes polling delay)
seconds = 10

calibration_period = 4

# ish
samples_per_second = 25

samples_to_calibrate = samples_per_second * calibration_period

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
    if i < samples_to_calibrate:
        ls.calibrate()
    logger.log()
    total_poll_time += sh.last_poll_time
    print("Taking sample {}/{}, calibration: {}, o: {}"\
        .format(i+1, total_samples, i < samples_to_calibrate, get_out()))
    time.sleep(delay)

elapsed_time = time.perf_counter()-time_start
print("Done in {:.2f}s!".format(elapsed_time))
print("{:.2f}s total poll time ({:.2f}ms average)!"\
    .format(total_poll_time / 1000, total_poll_time/total_samples))
print("{:.2f}ms other activities including sleep (approx)"\
    .format((elapsed_time - total_poll_time/1000)*1000))