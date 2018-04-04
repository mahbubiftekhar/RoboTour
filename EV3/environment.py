
import time, math

class Environment():
    def __init__(self):

        # self.floor_value = 
        self.line_threshold = 57
        self.black_threshold = 15

        self.line_sens_val = 0
        self.colour_left = 0
        self.colour_right = 0
        
        self.dist_front = 0
        self.dist_right = 0
        self.dist_left  = 0

        self.rot_right = 0
        self.rot_left  = 0



        self.clock_ms = 0
        self.clock_start = 0

        self.last_rot_right = 0
        self.last_rot_left = 0

        self.avoidance_direction = 'left'
        self.loop_time = 0

        self.sees_line = {}

        self.next_turn = 'left'
        self.turns_list = ['forward', 'forward',    'right', 'right', 'forward', 'forward', 'left', 'arrivided']
        # self.turns_list.reverse()

        self.positions_list = []
        # self.positions_list.reverse()


        self.position = '10'
        self.orientation = 'N'
        self.pointer_orientation = 'N'

        self.next_position = '10'
        self.next_orientation = 'N'

        self.users = 0

        self.pictures_to_go = []

        self.route_done = False
        self.finished_tour = False

        self.dijkstra_map = {
        '0': {'1': 26, '8': 21},
        '1': {'0': 26, '12': 19.5},
        '2': {'3': 26.5, '15': 19.5},
        '3': {'2': 26.5, '13': 20},
        '4': {'11': 33.5, '14': 31.5},
        '5': {'6': 27, '14': 46},
        '6': {'5': 27, '7': 28},
        '7': {'6': 28, '9': 46.5},
        '8': {'0': 21, '15': 31.5, '14': 28},
        '9': {'7': 46.5, '15': 32, '13': 85},
        '10': {'11': 20},
        '11': {'4': 33.5, '10': 20, '12': 28},
        '12': {'1': 19.5, '11': 28, '13': 32},
        '13': {'3': 20, '12': 32, '9': 85},
        '14': {'4': 31.5, '5': 46, '8': 28},
        '15': {'2': 19.5, '8': 31.5, '9': 32}
        }

        self.motor_map = {
        '0': "E",
        '1': "W",
        '2': "E",
        '3': "W",
        '4': "E",
        '5': "S",
        '6': "S",
        '7': "N",
        '8': "N",
        '9': "E",
        '10': "S",
        '12': "S",
        'Exit': "S",
        'Toilet': "S"
        }

        self.orientation_map = {}
        self.orientation_map[('0', '1')] = "S"
        self.orientation_map[('0', '8')] = "N"
        self.orientation_map[('1', '12')] = "S"
        self.orientation_map[('1', '0')] = "N"
        self.orientation_map[('2', '15')] = "N"
        self.orientation_map[('2', '3')] = "S"
        self.orientation_map[('3', '2')] = "N"
        self.orientation_map[('3', '13')] = "S"
        self.orientation_map[('4', '11')] = "S"
        self.orientation_map[('4', '14')] = "N"
        self.orientation_map[('5', '14')] = "WS"  # Special Case
        self.orientation_map[('5', '6')] = "E"
        self.orientation_map[('6', '5')] = "W"
        self.orientation_map[('6', '7')] = "E"
        self.orientation_map[('7', '9')] = "ES"
        self.orientation_map[('7', '6')] = "W"
        self.orientation_map[('8', '0')] = "S"
        self.orientation_map[('8', '15')] = "E"
        self.orientation_map[('8', '14')] = "W"
        self.orientation_map[('9', '13')] = "SW"
        self.orientation_map[('9', '15')] = "W"
        self.orientation_map[('9', '7')] = "NW"
        self.orientation_map[('10', '11')] = "N"
        self.orientation_map[('11', '10')] = "S"
        self.orientation_map[('11', '4')] = "N"
        self.orientation_map[('11', '12')] = "E"
        self.orientation_map[('12', '13')] = "E"
        self.orientation_map[('12', '1')] = "N"
        self.orientation_map[('12', '11')] = "W"
        self.orientation_map[('13', '3')] = "N"
        self.orientation_map[('13', '9')] = "EN"
        self.orientation_map[('13', '12')] = "W"
        self.orientation_map[('14', '4')] = "S"
        self.orientation_map[('14', '8')] = "E"
        self.orientation_map[('14', '5')] = "NE"
        self.orientation_map[('15', '9')] = "E"
        self.orientation_map[('15', '2')] = "S"
        self.orientation_map[('15', '8')] = "W"



        self.obstacle_map = {}
        self.obstacle_map[('10', '11')] = 'left'
        self.obstacle_map[('11', '10')] = 'right'
        self.obstacle_map[('11', '12')] = 'right'
        self.obstacle_map[('12', '13')] = 'right'
        self.obstacle_map[('13', '12')] = 'left'
        self.obstacle_map[('13', '9')] = 'right'
        self.obstacle_map[('9', '13')] = 'left'
        self.obstacle_map[('9', '7')] = 'right'
        self.obstacle_map[('7', '9')] = 'left'
        self.obstacle_map[('7', '6')] = 'right'
        self.obstacle_map[('6', '7')] = 'left'
        self.obstacle_map[('6', '5')] = 'right'
        self.obstacle_map[('5', '6')] = 'left'
        self.obstacle_map[('5', '14')] = 'right'
        self.obstacle_map[('14', '5')] = 'left'
        self.obstacle_map[('14', '4')] = 'right'
        self.obstacle_map[('4', '14')] = 'left'
        self.obstacle_map[('4', '11')] = 'right'
        self.obstacle_map[('11', '4')] = 'left'


    def init(self):
        self.clock_init()

    def update(self):
        self.last_rot_left = self.rot_left
        self.last_rot_right = self.rot_right
        
        self.loop_time = -self.clock_ms
        self.clock_update()
        self.loop_time += self.clock_ms

    def clock_init(self):
        self.clock_start = time.perf_counter() * 1000
    def clock_update(self):
        self.clock_ms = (time.perf_counter() * 1000) - self.clock_start