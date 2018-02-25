#! /usr/bin/env python3

class DataLogger():

	def __init__(self):
		self.channel_names = []
		self.channels = []
		pass

	def make_entry(self):
		entry = ""
		entry += timestamp.get_record()
		for c in channels:
			entry += ','
			entry += c.get_record
		entry+= '\n'

class DataChannel():
'''
Class for a single channel of data that should be logged by the DataLogger.
'''
	# TODO: Consider adding a logging 
	def __init__(self, name, get_value_callback):
		self.get_value_callback = get_value_callback
		self.name = name
		pass

	def get_record(self):
		self.value = self.get_value_callback()
		return str(value)