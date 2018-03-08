#! /usr/bin/env python3
# Core imports
import time
import sys
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re
from threading import Thread
from sensor_hub import *
from comms import *
from dijkstra import *


# ###################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 200  # in mm
side_distance = 17
link = "https://homepages.inf.ed.ac.uk/s1553593/receiver.php"
pointerState = ""
startPosition = '10'  # Toilet
robot_location = startPosition
robot_orientation = 'N'  # N,S,W,E (North South West East)
robot_pointer = 'N'  # N,S,W,E (North of the robot)
orientation_map = dict()  # Map for Orientation between neighbouring points
dijkstra_map = dict()  # Map for Distance between neighbouring points
motor_map = dict()
art_pieces_map = dict()


###############################################################

# ###################### INITIALISING MAP #############################

def initialising_map():
    # Orientation from point X to Y is N/S/W/E
    # 38 edges in total
    global orientation_map
    orientation_map[('0', '1')] = "S"
    orientation_map[('0', '8')] = "N"
    orientation_map[('1', '12')] = "S"
    orientation_map[('1', '0')] = "N"
    orientation_map[('2', '15')] = "N"
    orientation_map[('2', '3')] = "S"
    orientation_map[('3', '2')] = "N"
    orientation_map[('3', '13')] = "S"
    orientation_map[('4', '11')] = "S"
    orientation_map[('4', '14')] = "N"
    orientation_map[('5', '14')] = "WS"  # Special Case
    orientation_map[('5', '6')] = "E"
    orientation_map[('6', '5')] = "W"
    orientation_map[('6', '7')] = "E"
    orientation_map[('7', '9')] = "ES"
    orientation_map[('7', '6')] = "W"
    orientation_map[('8', '0')] = "S"
    orientation_map[('8', '15')] = "E"
    orientation_map[('8', '14')] = "W"
    orientation_map[('9', '13')] = "SW"
    orientation_map[('9', '15')] = "W"
    orientation_map[('9', '7')] = "NW"
    orientation_map[('10', '11')] = "N"
    orientation_map[('11', '10')] = "S"
    orientation_map[('11', '4')] = "N"
    orientation_map[('11', '12')] = "E"
    orientation_map[('12', '13')] = "E"
    orientation_map[('12', '1')] = "N"
    orientation_map[('12', '11')] = "W"
    orientation_map[('13', '3')] = "N"
    orientation_map[('13', '9')] = "EN"
    orientation_map[('13', '12')] = "W"
    orientation_map[('14', '4')] = "S"
    orientation_map[('14', '8')] = "E"
    orientation_map[('14', '5')] = "NE"
    orientation_map[('15', '9')] = "E"
    orientation_map[('15', '2')] = "S"
    orientation_map[('15', '8')] = "W"

    # Distance Map for Dijkstra
    global dijkstra_map
    dijkstra_map = {
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

    global motor_map
    motor_map = {
        '0': "W",
        '1': "W",
        '2': "W",
        '3': "W",
        '4': "W",
        '5': "N",
        '6': "N",
        '7': "N",
        '8': "N",
        '9': "E",
        '10': "S"
    }

    global art_pieces_map
    art_pieces_map = {
        '0': "The Birth of Venus",
        '1': "The Creation of Adam",
        '2': "David",
        '3': "Girl with a Pearl Earring",
        '4': "Mona Lisa",
        '5': "Napoleon Crossing the Alps",
        '6': "The Starry Night",
        '7': "The Last Supper",
        '8': "The Great Wave of Kanagawa",
        '9': "Water Lilies",
        '10': "Exit"
    }


#####################################################################

# ###################### SETUP SENSORS ######################
hub = SensorHub()
sonar_front = ev3.UltrasonicSensor(ev3.INPUT_2)
sonar_front.mode = 'US-DIST-CM'  # Will return value in mm
sonar_left = HubSonar(hub, 's0')
sonar_right = HubSonar(hub, 's1')
motor_pointer = ev3.LargeMotor('outC')
motor_left = ev3.LargeMotor('outB')
motor_right = ev3.LargeMotor('outD')
colour_sensor_right = ev3.ColorSensor(ev3.INPUT_1)
colour_sensor_left = ev3.ColorSensor(ev3.INPUT_4)
colour_sensor_left.mode = 'COL-REFLECT'
colour_sensor_right.mode = 'COL-REFLECT'

line_threshold = 57
wall_threshold = 15


if motor_pointer.connected & sonar_front.connected & motor_left.connected & motor_right.connected:
    print('All sensors and motors connected')
else:
    if not motor_pointer.connected:
        print("motorPointer not connected")
    if not sonar_front.connected:
        print("Sonar not connected")
    if not motor_left.connected:
        print("MotorLeft not connected")
    if not motor_right.connected:
        print("MotorRight not connected")
    if not colour_sensor_left.connected:
        print("ColorLeft not connected")
    if not colour_sensor_right.connected:
        print("ColorRight not connected")
    print('Please check all sensors and actuators are connected.')
    exit()

############################################################

# #################### SENSOR AND ACTUATOR FUNCTIONS ############################

def get_colour_right():
    return colour_sensor_right.value()


def get_colour_left():
    return colour_sensor_left.value()


def is_right_line_detected():  # Right Lego sensor
    # print(getColourRight())
    return get_colour_right() > line_threshold


def is_left_line_detected():
    # print(getColourLeft())
    return get_colour_left() > line_threshold


def is_line_detected():
    return is_left_line_detected() or is_right_line_detected()


def is_wall_detected():
    return get_colour_left() < wall_threshold or get_colour_right() < wall_threshold


def get_sonar_readings_front():
    return sonar_front.value()


def get_sonar_readings_left():
    return sonar_left.value()


def get_sonar_readings_right():
    return sonar_right.value()


def is_front_obstacle():
    return get_sonar_readings_front() < obstacle_detection_distance


def is_left_side_obstacle():
    return get_sonar_readings_left() < side_distance


def is_right_side_obstacle():
    return get_sonar_readings_right() < side_distance


def is_branch_detected(left, right):
    return left > 60 and right > 60


def is_painting_detected():
    pass


def move_forward(speed, running_time):
    motor_left.run_timed(speed_sp=speed, time_sp=running_time)
    motor_right.run_timed(speed_sp=speed, time_sp=running_time)


def move_backward(speed, running_time):
    motor_left.run_timed(speed_sp=-speed, time_sp=running_time)
    motor_right.run_timed(speed_sp=-speed, time_sp=running_time)


def turn_right():
    motor_right.run_timed(speed_sp=-150, time_sp=600)
    motor_left.run_timed(speed_sp=250, time_sp=800)
    motor_left.wait_until_not_moving()
    motor_right.wait_until_not_moving()


def turn_left():
    motor_left.run_timed(speed_sp=-250, time_sp=850)
    motor_right.run_timed(speed_sp=150, time_sp=800)
    motor_left.wait_until_not_moving()
    motor_right.wait_until_not_moving()
    motor_left.run_timed(speed_sp=200, time_sp=150)
    motor_right.run_timed(speed_sp=200, time_sp=150)
    motor_left.wait_until_not_moving()
    motor_right.wait_until_not_moving()


def turn(left, right, running_time):  # For unclear speed
    motor_left.run_timed(speed_sp=left, time_sp=running_time)
    motor_right.run_timed(speed_sp=right, time_sp=running_time)


def turn_back():  # 180
    motor_left.run_timed(speed_sp=400, time_sp=1000)
    motor_right.run_timed(speed_sp=-400, time_sp=1000)
    motor_left.wait_until_not_moving()
    motor_right.wait_until_not_moving()


def turn_right_ninety():  # 90
    motor_left.run_timed(speed_sp=175, time_sp=1000)
    motor_right.run_timed(speed_sp=-175, time_sp=1000)
    wait_for_motor()


def turn_left_ninety():  # -90
    motor_left.run_timed(speed_sp=-175, time_sp=1000)
    motor_right.run_timed(speed_sp=175, time_sp=1000)
    wait_for_motor()


def stop_wheel_motor():
    motor_left.stop(stop_action="hold")
    motor_right.stop(stop_action="hold")


def wait_for_motor():
    motor_left.wait_until_not_moving()
    motor_right.wait_until_not_moving()


def speak(string):
    ev3.Sound.speak(string)


def turn_pointer(direction):  # Turn 45
    if direction == "CW":
        motor_pointer.run_timed(speed_sp=-414, time_sp=500)
        time.sleep(0.5)

    if direction == "ACW":
        motor_pointer.run_timed(speed_sp=414, time_sp=500)
        time.sleep(0.5)


def turn_and_reset_pointer(direction):
    if direction == "CW":
        turn_pointer("CW")
        turn_pointer("ACW")

    elif direction == "ACW":
        turn_pointer("ACW")
        turn_pointer("CW")


def point_to_painting(picture_id):
    if is_orientation_left(motor_map[picture_id]):
        turn_pointer("ACW")
        global robot_pointer
        robot_pointer = 'W'
    elif is_orientation_right(motor_map[picture_id]):
        turn_pointer("CW")
        global robot_pointer
        robot_pointer = 'E'
    elif is_orientation_back(motor_map[picture_id]):
        turn_pointer("CW")
        turn_pointer("CW")
        turn_pointer("CW")
        turn_pointer("CW")
        global robot_pointer
        robot_pointer = 'S'
    else:
        pass


def turn_back_pointer():
    if robot_pointer == 'S':
        turn_pointer("ACW")
        turn_pointer("ACW")
        turn_pointer("ACW")
        turn_pointer("ACW")
    elif robot_pointer == 'W':
        turn_pointer("CW")
    elif robot_pointer == 'E':
        turn_pointer("ACW")
    else:
        pass

    global robot_pointer
    robot_pointer = 'N'

######################################################################

# ###################### ROBOTOUR FUNCTIONS ###########################


def get_closest_painting(d_map, location, pictures_lists):
    shortest_distance = sys.maxsize
    short_path = None
    closed_painting = None
    for painting in pictures_lists:
        (path, distance) = dijkstra(d_map, location, painting, [], {}, {})
        if shortest_distance > distance:
            shortest_distance = distance
            short_path = path
            closed_painting = path[-1]
    return closed_painting, short_path


def get_art_pieces_from_app():
    pictures = server.get_pictures_to_go()
    print(pictures)
    pictures_to_go = []
    for index in range(len(pictures)):
        if pictures[index] == "T":
            pictures_to_go.append(str(index))
    print(pictures_to_go)
    return pictures_to_go


def align_orientation(desired_orientation):

    first_char = desired_orientation[0]
    if robot_orientation == first_char:
        print("FORWARD!")
        wait_for_motor()
    elif is_orientation_right(first_char):
        print("Turn right")
        turn_right()
    elif is_orientation_left(first_char):
        print("Turn left")
        turn_left()
    elif is_orientation_back(first_char):
        print("Turn back")
        turn_back()
    else:
        print("Errors on aligning orientation - Robot orientation ", robot_orientation,
              " Desired orientation: ", desired_orientation)
    # Update orientation
    global robot_orientation
    robot_orientation = first_char

    if len(desired_orientation) == 2:
        # Update in special case
        robot_orientation = desired_orientation[1]

    print("Current orientation is "+robot_orientation)


def is_orientation_right(desired_orientation):
    if robot_orientation == "N" and desired_orientation == "E":
        return True
    elif robot_orientation == "E" and desired_orientation == "S":
        return True
    elif robot_orientation == "S" and desired_orientation == "W":
        return True
    elif robot_orientation == "W" and desired_orientation == "N":
        return True
    else:
        return False


def is_orientation_left(desired_orientation):
    if robot_orientation == "N" and desired_orientation == "W":
        return True
    elif robot_orientation == "E" and desired_orientation == "N":
        return True
    elif robot_orientation == "S" and desired_orientation == "E":
        return True
    elif robot_orientation == "W" and desired_orientation == "S":
        return True
    else:
        return False


def is_orientation_back(desired_orientation):
    if robot_orientation == "N" and desired_orientation == "S":
        return True
    elif robot_orientation == "E" and desired_orientation == "W":
        return True
    elif robot_orientation == "S" and desired_orientation == "N":
        return True
    elif robot_orientation == "W" and desired_orientation == "E":
        return True
    else:
        return False


def on_pause_command():
    pass


def on_resume_command():
    pass


def is_lost():
    speak("I am lost, please help.")

# #################### OBSTACLE AVOIDANCE #######################


def get_ready_for_obstacle(direction):  # 90 degree
    print("GET READY FOR OBSTACLE")
    if direction == 'RIGHT':
        turn_right_ninety()

        while is_line_detected():
            move_forward(100, 100)

    else:  # All default will go through the Left side. IE
        turn_left_ninety()

        while is_line_detected():
            move_forward(100, 100)


def go_around_obstacle(direction, tries):
    print("GO AROUND OBSTACLE Direction: ", direction)
    set_distance = 11
    set_sharp_distance = 18
    is_sharp_before = False
    if direction == 'RIGHT':
        while not is_line_detected():

            if is_wall_detected():
                print("Go around the Right")
                print('Detect wall!')
                turn_back()
                # Go back to line
                go_around_obstacle('LEFT', tries=tries+1)
                #get_ready_for_obstacle('LEFT')
                while is_line_detected():
                    move_forward(100, 100)
                print("Try to go around the other side")
                go_around_obstacle('LEFT', tries=1)
                # get_back_to_line('LEFT')

                #print("Both way tried, cannot go through.")
                #time.sleep(10)
                return

            if get_sonar_readings_front() < set_distance*10:
                turn_right_ninety()
                is_sharp_before = False
            else:
                left = get_sonar_readings_left()
                if left < set_distance:
                    turn(100, 50, 100)
                    is_sharp_before = False
                elif left > set_distance:
                    if (not is_sharp_before) and left > set_sharp_distance:
                        print("Find a sharp!")
                        turn(100, 100, 100)
                        is_sharp_before = True
                    else:
                        turn(50, 150, 100)
                else:
                    turn(100, 100, 100)
                    is_sharp_before = False

    else:  # All default will go through the Left side. IE
        while not is_line_detected():

            if is_wall_detected():
                print("Go around the Left")
                print('Detect wall!')
                turn_back()
                # Go back to line
                go_around_obstacle('RIGHT', tries=2)
                # turn_left_ninety()
                get_back_to_line('LEFT')

                speak("Carson please remove the obstacle in front of me. Thank you. I love you.")
                while(is_front_obstacle()):
                    stop_wheel_motor()
                    time.sleep(1)

                return

            if get_sonar_readings_front() < set_distance*10:
                turn_left_ninety()
                is_sharp_before = False
            else:
                right = get_sonar_readings_right()
                if right < set_distance:
                    turn(50, 100, 100)
                    is_sharp_before = False
                elif right > set_distance:
                    if (not is_sharp_before) and right > set_sharp_distance:
                        print("Find a sharp!")
                        turn(100, 100, 100)
                        is_sharp_before = True
                    else:
                        turn(150, 50, 100)
                else:
                    turn(100, 100, 100)
                    is_sharp_before = False
    if (tries == 1):
        get_back_to_line(direction)


def get_back_to_line(turning_direction):
    print("GET BACK TO LINE")

    if turning_direction == 'RIGHT':
        if is_left_line_detected():
            while not is_right_line_detected():
                turn(0, 100, 100)
            while not is_left_line_detected():
                turn(-100, 0, 100)

        elif is_right_line_detected():
            while not is_left_line_detected():
                turn(100, 0, 100)
            while not is_right_line_detected():
                turn(0, -100, 100)

        turn_right_ninety()

    else:
        if is_right_line_detected():
            while not is_left_line_detected():
                turn(0, 100, 100)
            while not is_right_line_detected():
                turn(-100, 0, 100)

        elif is_left_line_detected():
            while not is_right_line_detected():
                turn(100, 0, 100)
            while not is_left_line_detected():
                turn(0, -100, 100)

        turn_left_ninety()

    '''
    if turning_direction == 'RIGHT':
        if is_left_line_detected():
            # That means when it detect the line, it is not facing to the obstacle
            pass
        else:
            # That means when it detect the line, it is facing to the obstacle
            while not is_left_line_detected():
                turn(150, -100, 100)

        while is_left_line_detected():
            turn(100, 100, 100)
        while not is_left_line_detected():
            turn(150, -100, 100)
        print("Find line again!")
    else:
        if is_right_line_detected():
            # That means when it detect the line, it is not facing to the obstacle
            pass

        else:
            # That means when it detect the line, it is facing to the obstacle
            while not is_right_line_detected():
                turn(-100, 150, 100)

        while is_right_line_detected():
            turn(100, 100, 100)
        while not is_right_line_detected():
            turn(-100, 150, 100)

        print("Find line again!")
    '''

def wait_for_user_to_get_ready():
    print("Press left for single user and press right for double user...")
    button_ev3 = ev3.Button()

    while True:
        if button_ev3.left:
            print("Waiting for User 1 to complete...")
            server.start_up_single()
            print("User 1 is ready!")
            break
        elif button_ev3.right:
            print("Waiting for User 1 and User 2 to complete...")
            server.start_up_double()
            print("Both users are ready!")
            break


def go_to_closest_painting(painting, path):

    index = 1
    while index < len(path):
        location = path[index]
        while is_branch_detected(colour_sensor_left.value(), colour_sensor_right.value()):
            move_forward(100, 100)

        print("Going to " + location)
        align_orientation(orientation_map[(robot_location, location)])
        # Follow line until reaching a painting OR a branch
        while True:

            # Line following
            base_speed = 130
            curr_r = colour_sensor_right.value()
            curr_l = colour_sensor_left.value()
            difference_l = curr_l - target
            difference_r = curr_r - target
            global errorSumR
            errorSumR += difference_r
            if abs(errorSumR) > 400:
                errorSumR = 400 * errorSumR / abs(errorSumR)
            d = curr_r - oldR
            base_speed -= abs(errorSumR) * 0.14
            if base_speed < 45:
                base_speed = 45
            motor_right.run_forever(speed_sp=base_speed - difference_r * 6.5 - errorSumR * 0.05 - d * 2)
            motor_left.run_forever(speed_sp=base_speed + difference_r * 6.5 + errorSumR * 0.05 + d * 2)
            global oldR
            oldR = curr_r
            global oldL
            oldL = curr_l

            if is_front_obstacle():
                stop_wheel_motor()
                print("Stop at: (Front) ", sonar_front.value())
                obstacle_turn = 'RIGHT'
                get_ready_for_obstacle(obstacle_turn)  # step 1
                print("Stop at: (Side) ", sonar_right.value())
                go_around_obstacle(obstacle_turn, tries= 1)

            elif is_branch_detected(curr_l, curr_r):
                stop_wheel_motor()
                print("Find a branch")
                global robot_location
                robot_location = location
                print("Current location is ", robot_location)
                index = index + 1

                server.update_commands()
                if server.check_position('Skip') == 'T':  # Only skip at branch
                    print("User press skip!")
                    index = len(path) + 1
                    remainingPicturesToGo.remove(painting)
                    server.update_status_false('Skip')
                elif server.check_position('Toilet') == 'T':
                    print("User press toilet!")
                    index = len(path) + 1
                    server.update_status_false('Toilet')
                    go_to_toilet()
                elif server.check_position('Exit') == 'T':
                    print("User press exit!")
                    index = len(path) + 1
                    server.update_status_false('Exit')
                    global remainingPicturesToGo
                    remainingPicturesToGo = []
                elif server.check_position('Cancel') == 'T':
                    print("User press cancel!")
                    index = len(path) + 1
                    server.update_status_false('Cancel')
                    global remainingPicturesToGo
                    remainingPicturesToGo = []

                break



    if index == len(path):
        #speak("This is " + art_pieces_map[painting])
        point_to_painting(painting)
        server.update_status_arrived(painting)  # tell the app the robot is arrived to this painting
        server.set_stop_true()

        server.wait_for_continue()  # check for user ready to go

        server.update_status_false(painting)
        server.set_stop_false()

        turn_back_pointer()  # Continue when the stop command become 'F'

        remainingPicturesToGo.remove(painting)


def go_to_toilet():

    toilet_position, path = get_closest_painting(dijkstra_map, robot_location, ['10'])

    index = 1

    while index < len(path):

        location = path[index]

        while is_branch_detected(colour_sensor_left.value(), colour_sensor_right.value()):
            move_forward(100, 100)

        print("Going to " + location)
        align_orientation(orientation_map[(robot_location, location)])
        # Follow line until reaching a painting OR a branch
        while True:

            base_speed = 130
            curr_r = colour_sensor_right.value()
            curr_l = colour_sensor_left.value()
            difference_l = curr_l - target
            difference_r = curr_r - target
            global errorSumR
            errorSumR += difference_r
            if abs(errorSumR) > 400:
                errorSumR = 400 * errorSumR / abs(errorSumR)
            d = curr_r - oldR
            base_speed -= abs(errorSumR) * 0.14
            if base_speed < 45:
                base_speed = 45
            motor_right.run_forever(speed_sp=base_speed - difference_r * 6.5 - errorSumR * 0.05 - d * 2)
            motor_left.run_forever(speed_sp=base_speed + difference_r * 6.5 + errorSumR * 0.05 + d * 2)
            global oldR
            oldR = curr_r
            global oldL
            oldL = curr_l

            if is_front_obstacle():
                stop_wheel_motor()
                print("Stop at: (Front) ", sonar_front.value())
                obstacle_turn = 'RIGHT'
                get_ready_for_obstacle(obstacle_turn)  # step 1
                print("Stop at: (Side) ", sonarRight.value())
                go_around_obstacle(obstacle_turn)
                get_back_to_line(obstacle_turn)

            elif is_branch_detected(curr_l, curr_r):
                stop_wheel_motor()
                print("Find a branch")
                global robot_location
                robot_location = location
                print("Current location is ", robot_location)
                index = index + 1

                server.update_commands()
                if server.check_position('Skip') == 'T':  # Only skip at branch
                    index = len(path) + 1
                    server.update_status_false('Skip')
                elif server.check_position('Exit') == 'T':
                    index = len(path) + 1
                    server.update_status_false('Exit')
                    global remainingPicturesToGo
                    remainingPicturesToGo = []
                elif server.check_position('Cancel') == 'T':
                    index = len(path) + 1
                    server.update_status_false('Cancel')
                    global remainingPicturesToGo
                    remainingPicturesToGo = []

                break

    if index == len(path):
        #speak("This is " + art_pieces_map[painting])
        point_to_painting(toilet_position)
        server.update_status_arrived('Toilet')  # tell the app the robot is arrived to this painting
        server.set_stop_true()

        server.wait_for_continue()  # check for user ready to go

        server.update_status_false('Toilet')
        server.set_stop_false()

        turn_back_pointer()  # Continue when the stop command become 'F'


def go_to_exit():

    exit_position, path = get_closest_painting(dijkstra_map, robot_location, ['10'])

    index = 1

    while index < len(path):

        location = path[index]

        while is_branch_detected(colour_sensor_left.value(), colour_sensor_right.value()):
            move_forward(100, 100)


        print("Going to " + location)
        align_orientation(orientation_map[(robot_location, location)])
        # Follow line until reaching a painting OR a branch
        while True:

            base_speed = 130
            curr_r = colour_sensor_right.value()
            curr_l = colour_sensor_left.value()
            difference_l = curr_l - target
            difference_r = curr_r - target
            global errorSumR
            errorSumR += difference_r
            if abs(errorSumR) > 400:
                errorSumR = 400 * errorSumR / abs(errorSumR)
            d = curr_r - oldR
            base_speed -= abs(errorSumR) * 0.14
            if base_speed < 45:
                base_speed = 45
            motor_right.run_forever(speed_sp=base_speed - difference_r * 6.5 - errorSumR * 0.05 - d * 2)
            motor_left.run_forever(speed_sp=base_speed + difference_r * 6.5 + errorSumR * 0.05 + d * 2)
            global oldR
            oldR = curr_r
            global oldL
            oldL = curr_l

            if is_front_obstacle():
                stop_wheel_motor()
                print("Stop at: (Front) ", sonar_front.value())
                obstacle_turn = 'RIGHT'
                get_ready_for_obstacle(obstacle_turn)  # step 1
                print("Stop at: (Side) ", sonarRight.value())
                go_around_obstacle(obstacle_turn)
                get_back_to_line(obstacle_turn)

            elif is_branch_detected(curr_l, curr_r):
                stop_wheel_motor()
                print("Find a branch")
                global robot_location
                robot_location = location
                print("Current location is ", robot_location)
                index = index + 1
                break


############################################################

# #################### MAIN #################################


print("SensorHub have set up.")
# speak("Carson, we love you. Group 18. ")

# ################### SETUP ############################
initialising_map()
print("Map has been initialised.")
server = Server()
print("Waiting for users...")
#wait_for_user_to_get_ready()
server.start_up_single()
print("Users are ready!")
print("Current location is ", robot_location, ", facing ", robot_orientation)

remainingPicturesToGo = get_art_pieces_from_app()


###########################################################

# ################ MAIN ##########################

target = 40
errorSumR = 0
oldR = colour_sensor_right.value()
oldL = colour_sensor_left.value()
try:
    while not len(remainingPicturesToGo) == 0:

        print("Remain picture: ", remainingPicturesToGo)
        closest_painting, shortest_path = get_closest_painting(dijkstra_map, robot_location, remainingPicturesToGo)

        # Sanity check, is robot's location the starting position of the shortest path?
        if shortest_path[0] != robot_location:
            print("Robot's location is not the starting position of the shortest path")
            exit()

        print("Going to picture ", closest_painting)
        server.update_art_piece(closest_painting)    # tell the app the robot is going to this painting
        go_to_closest_painting(closest_painting, shortest_path)

    # If not skip do this
    if not robot_location == '10':
        print("No more pictures to go. Go to exit.")
        go_to_exit()   # Go to exit

    align_orientation('N')
    server.update_status_arrived('Exit')
    server.set_stop_true()
    print("Finish program!")
    server.reset_list_on_server()
    exit()


except KeyboardInterrupt:
    motor_left.stop()
    motor_right.stop()
    raise

# ################ For testing ####################
"""
static_dictionary = {
    'Monalisa': ['FORWARD', 'LEFT', 'CW'],
    'The Last Supper': ['RIGHT', 'FORWARD', 'CW']
}



command_index = 0
pictures = server.getCommands()
if (pictures[4] == "T"):
    commands = static_dictionary['Monalisa']
elif (pictures[7] == "T"):
    commands = static_dictionary['The Last Supper']
else:
    print ("No pictures selected")

print(commands)

##################################################

try:
    while(True):
        for range
        if(command_index == len(commands)):
            print("All commands finished")
            stopWheelMotor()
            exit()
        elif(isFrontObstacle()):
            stopWheelMotor()
            print("Stop at: (Front) ", sonarFront.value())
            commandNext = 'RIGHT' # Example
            getReadyForObstacle(commands[command_index]) # step 1
            print("Stop at: (Right) ",sonarRight.value())
            goAroundObstacle(commands[command_index])
            getBackToLine(commands[command_index])
        else:#follow lines
            baseSpeed = 90
            currR = colourSensorRight.value()
            currL = colourSensorLeft.value()
            #print("currR=",currR," currL",currL)
            if(currL > 60 and currR > 60):
                print("BRANCH")
                if(commands[command_index] == 'RIGHT'):
                    command_index+=1
                    turnRight()
                    #nextDirection = 'LEFT'

                elif(commands[command_index] == 'FORWARD'):
                    command_index+=1
                    motorRight.run_timed(speed_sp= 100,time_sp = 600)
                    motorLeft.run_timed(speed_sp= 100,time_sp = 600)
                    motorLeft.wait_until_not_moving()
                    motorRight.wait_until_not_moving()
                    #nextDirection = 'LEFT'

                elif(commands[command_index] == 'LEFT'):
                    command_index+=1
                    turnLeft()

                    #nextDirection = 'RIGHT'
                elif(commands[command_index] == 'CW'):
                    command_index+=1
                    stopWheelMotor()
                    if (pictures[4] == "T"):
                        speak("This is Mona Lisa!")
                    elif (pictures[7] == "T"):
                        speak("This is The last supper!")
                    turnPointer('CW')
                    turnPointer('ACW')

                print(command_index)

            differenceL = currL - target
            differenceR = currR - target
            errorSumR +=differenceR
            if(abs(errorSumR) > 400):
                errorSumR = 400*errorSumR/abs(errorSumR)
            D = currR - oldR
            baseSpeed -= abs(errorSumR)*0.16
            motorRight.run_forever(speed_sp = baseSpeed- differenceR*3 -errorSumR*0.05 - D*2)
            motorLeft.run_forever(speed_sp = baseSpeed+ differenceR*3 + errorSumR*0.05 + D*2)
            oldR = currR
            oldL = currL
            #print(str(currL) + "  "  + str(currR))
            # was 60 before

except:
    motorLeft.stop()
    motorRight.stop()
    raise

"""
