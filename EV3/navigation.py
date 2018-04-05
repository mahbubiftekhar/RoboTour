from dijkstra import *

class Navigation():

	def __init__(self, robot, server):
		self.server = server
		self.robot = robot

	def plan_route(self):
		# robot.env.route_done = False
		self.get_art_pieces_from_app()
		self.robot.env.pictures_to_go = self.calculate_paintings_order(self.robot.env.pictures_to_go)
		print("Determined route: ", self.robot.env.positions_list)
		if len(self.robot.env.pictures_to_go) != 0:
			self.server.update_art_piece(self.robot.env.pictures_to_go[0])
		# self.server.update_status_true('onTour')
		# self.robot.env.route_dene = True


	def get_art_pieces_from_app(self):
		self.server.update_commands()
		self.server.update_pictures_to_go()
		pictures = self.server.get_pictures_to_go()
		print("Server state: ", pictures)
		self.robot.env.pictures_to_go = []
		for index in range(len(pictures)):
			if pictures[index] in ["T","N","0","1","2","3","4","5","6","7","8","9"]:
				self.robot.env.pictures_to_go.append(str(index))
		print("Selected pictures: ", self.robot.env.pictures_to_go)
		return self.robot.env.pictures_to_go

	def get_closest_painting(self, location, pictures_lists):
		shortest_distance = sys.maxsize
		short_path = None
		closest_painting = None
		d_map = self.robot.env.dijkstra_map
		for painting in pictures_lists:
			(path, distance) = dijkstra(d_map, location, painting, [], {}, {})
			if shortest_distance > distance:
				shortest_distance = distance
				short_path = path
				closest_painting = path[-1]
		return closest_painting, short_path

	def calculate_paintings_order(self, picture_to_go, virtual_location=None):
		print("Calculate paintings order")
		if virtual_location is None:
			virtual_location = self.robot.env.position
		virtual_remaining_pictures_to_go = []
		total = len(picture_to_go)
		for i in range(total):
			closest_painting, path = self.get_closest_painting(virtual_location, picture_to_go)
			print("closest painting ({}/{}): ".format(i+1,total), closest_painting)
			self.server.http_post(int(closest_painting), str(i))
			picture_to_go.remove(closest_painting)
			virtual_remaining_pictures_to_go.append(closest_painting)
			virtual_location = path[-1]

			self.robot.env.positions_list.extend(path[1:])
			# self.robot.env.positions_list.append('arrived')

		self.robot.env.finished_tour = False
		return virtual_remaining_pictures_to_go


	