#! /usr/bin/env python3

import time
import random

class DataLogger():

	def __init__(self, name, folder='./logs', timer=None):

		self.name = name

		self.channel_names = []
		self.channels = []

		# attach timer channel - either generic or passed on (e.g. global)
		if timer is None:
			self.timestamp = DataChannel("timestamp", time.perf_counter)
		else:
			self.timestamp = DataChannel("timestamp", timer)

		self.initiated = False

		self.lines_per_write = 1
		self.lines_in_buffer = 0
		self.buffer = []
		self.ensure_path_exists(folder)
		self.filename = folder+'/'+self.name+time.strftime("_%d_%m_%H-%M.csv")
		
	def init(self):
		# TODO: consider using binary mode
		try:
			self.file = open(self.filename, mode='w')
			line = self.create_header()
			self.file.write(line)
		except:
			print("Failed to open the file {}".format(self.filename))
			self.initiated = False
			return False

		self.file.close()
		self.initiated = True
	def ensure_path_exists(self, folder):
		if not os.path.exists(folder):
			print("No directory: {} !\nCreating...".format(folder))
			os.makedirs(folder)

	def write_buffer(self):
		with open(self.filename, mode='a') as f:
			f.write(''.join(self.buffer))

	def create_header(self):
		entry = ""
		entry += self.timestamp.name
		for c in self.channels:
			entry += ','
			entry += c.name
		entry += '\n'
		return entry
		


	def add_channel(self, data_channel):
		# don't add the channel if the logger is already in use,
		# as it would corrupt the CSV file
		if self.initiated:
			return False

		self.channel_names.append(data_channel.name)
		self.channels.append(data_channel)


	def make_entry(self):
		if not self.initiated:
			print("Please initiate the logger before making an entry")
			return

		entry = []
		entry.append(self.timestamp.get_record())
		for c in self.channels:
			entry.append(",{}".format(c.get_record()))

		entry.append('\n')
		return ''.join(entry)

	def log(self):
		if not self.initiated:
			print("Please initiate the logger before making a log")
			return
		self.buffer.append(self.make_entry())
		self.lines_in_buffer += 1
		if self.lines_in_buffer == self.lines_per_write:
			self.write_buffer()
			self.buffer = []
			self.lines_in_buffer = 0
	

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
		return str(self.value)




if __name__ == '__main__':


	dl = DataLogger("test")
	dl.add_channel(DataChannel("random", random.random))
	dl.add_channel(DataChannel('random2', random.random))
	dl.init()
	for i in range(100):
		dl.log()
		