#! /usr/bin/env python3
# Core imports
# from urllib.parse import urlencode
# from urllib.request import Request, urlopen
# from urllib import request, parse
import urllib.request
import urllib.parse
# import _thread
import time

class Server():
    ## THIS WILL CONTAIN THE ARTPIECES THAT THE USER
    #  WANTS TO GO TO, THIS WILL NOT CHANGE DURING THE TRIP
    def __init__(self):
        self.previousArtPiece = "-1"
        self.picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
        self.commands = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F","F", "F"]
        self.id_map = {
            '0': 0,
            '1': 1,
            '2': 2,
            '3': 3,
            '4': 4,
            '5': 5,
            '6': 6,
            '7': 7,
            '8': 8,
            '9': 9,
            'Skip': 10,
            'Stop': 11,
            'Cancel': 12,
            'Speed': 13,
            'Toilet': 14,
            'Exit': 15,
            'User 1': 16,
            'User 2': 17
        }

    def get_commands(self):
        return self.commands

    def get_pictures_to_go(self):
        return self.picturesToGoTO

    # Helper function that does a http post request
    def http_post(self, position, message):
        data = bytes(urllib.parse.urlencode({"command" + str(position): message}).encode())
        urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php", data)

    # Helper function that does HTTP get request
    def http_get(self):
        f = urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")  # open url
        myfile = f.read()  # read url contents
        self.command = myfile.decode("utf-8")  # convert bytearray to string
        return self.command

    def start_up_single(self):
        self.commands = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
        self.update_pictures_to_go()

        self.update_commands()
        while (self.user_1_check() != "T"):
            self.update_commands()
            time.sleep(0.5)
        self.update_pictures_to_go()

    def start_up_double(self):
        self.update_commands()
        while (self.user_1_check() != "T" or self.user_2_check() != 'T'):
            self.update_commands()
            time.sleep(0.5)
        self.update_pictures_to_go()

    # Updates the picturesToGoTO as an array, T means that the user wish's to go to the painting, F means they do not,
    # This will be the union of both users wishes
    def update_pictures_to_go(self):
        self.picturesToGoTO = self.commands[0:10]


    # This will be used to constantly update the list AFTER the first instance
    def update_commands(self):
        data = self.http_get()
        for i in range(0, len(self.commands)):
            self.commands[i] = data[i]



    # Resets the entire list online,
    # should be called once the robot is finnished giving the tour and returns to the
    def reset_list_on_server(self):
        print("Resetting Server")
        for x in range(0, 17):
            # Updating the list online
            self.http_post(x, "F")
        self.picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
        self.commands = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]

    def check_position(self, position):  # get command of Toilet, Stop etc.
        return self.commands[self.id_map[position]]

    def check_stop(self):       # Stop should be keep pulling and checking
        return self.commands[self.id_map['Stop']]

    def check_speed(self):       # Stop should be keep pulling and checking
        return self.commands[self.id_map['Speed']]

    # Updates the user once they have arrived at the TOILET
    def update_status_arrived(self, position):
        self.http_post(self.id_map[position], "A")

    def update_status_true(self, position):
        self.http_post(self.id_map[position], "T")

    def update_status_false(self, position):
        self.http_post(self.id_map[position], "F")

    def set_stop_true(self):
        self.http_post(self.id_map['Stop'], "T")

    def set_stop_false(self):
        self.http_post(self.id_map['Stop'], "F")

    def wait_for_continue(self):
        print("Wait for user to press continue.")
        self.update_commands()
        while self.command[self.id_map['Stop']]=='T':
            self.update_commands()
            time.sleep(0.5)
        print("Continue")

    ############
    # THESE ARE NOT FOR CD2, Don't worry about it now

    # Checks if user1 has submitted their painting requests
    def user_1_check(self):
        return self.commands[16]


    # Checks if user2 has submitted their painting requests
    def user_2_check(self):
        return self.commands[17]

    # Update the users screen with the artPiece they should be displayed - simply pass in the next optimal artPiece and
    # the rest is sorted
    def update_art_piece(self, nextArtWork):  # input string
        if self.previousArtPiece != "-1":
            self.http_post(self.id_map[self.previousArtPiece], "F")
        self.http_post(self.id_map[nextArtWork], "N")
        self.previousArtPiece = nextArtWork
        self.update_commands()  # update command
