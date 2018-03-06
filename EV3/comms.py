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

    # Helper function that does a http post request
    def httpPost(self, position, message):
        data = bytes(urllib.parse.urlencode({"command" + str(position): message}).encode())
        urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php", data)

    # Helper function that does HTTP get request
    def httpGet(self):
        f = urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")  # open url
        myfile = f.read()  # read url contents
        self.command = myfile.decode("utf-8")  # convert bytearray to string
        return self.command

    def startUpSingle(self):
        self.commands = ["F", "T", "T", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F","F", "F"]
        self.updatePicturesToGo()
        '''
        self.updateCommands()
        while (self.user1Check() != "T"):
            self.updateCommands()
            time.sleep(0.5)
        self.updatePicturesToGo()
        '''
    def startUpDouble(self):
        self.updateCommands()
        while (self.user1Check() != "T" or self.user2Check() != 'T'):
            self.updateCommands()
            time.sleep(0.5)
        self.updatePicturesToGo()

    # Updates the picturesToGoTO as an array, T means that the user wish's to go to the painting, F means they do not,
    # This will be the union of both users wishes
    def updatePicturesToGo(self):
        self.picturesToGoTO = self.commands[0:10]


    # This will be used to constantly update the list AFTER the first instance
    def updateCommands(self):
        data = self.httpGet()
        for i in range(0, len(self.commands)):
            self.commands[i] = data[i]



    # Resets the entire list online,
    # should be called once the robot is finnished giving the tour and returns to the
    def resetListOnServer(self):
        for x in range(0, 17):
            # Updating the list online
            self.httpPost(x, "F")
        self.picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]

    def checkPosition(self, position):  # get command of Toilet, Stop etc.
        return self.commands[self.id_map[position]]

    # Updates the user once they have arrived at the TOILET
    def arrivedPosition(self, position):
        self.httpPost(self.id_map[position], "A")
        self.httpPost(self.id_map['Stop'], "T")

    def waitForContinue(self):
        print("Wait for user to press continue.")
        self.updateCommands()
        while self.command[self.id_map['Stop']]=='T':
            self.updateCommands()
            time.sleep(0.5)

    ############
    # THESE ARE NOT FOR CD2, Don't worry about it now

    # Checks if user1 has submitted their painting requests
    def user1Check(self):
        return self.commands[16]


    # Checks if user2 has submitted their painting requests
    def user2Check(self):
        return self.commands[17]

    # Update the users screen with the artPiece they should be displayed - simply pass in the next optimal artPiece and
    # the rest is sorted
    def updateArtPiece(self, nextArtWork):  # input string
        if self.previousArtPiece != "-1":
            self.httpPost(self.id_map[self.previousArtPiece], "F")
        self.httpPost(self.id_map[nextArtWork], "N")
        self.previousArtPiece = nextArtWork
        self.updateCommands()  # update command

    ###########

    def constantCheck(self):
        pass

    def getCommands(self):
        return self.commands

    def getPicturesToGo(self):
        return self.picturesToGoTO
