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
        self.command = myfile.decode("utf-8")  # convert bytearray to string
        self.previousArtPiece = "-1"
        self.picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
        self.commands = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]

    # Helper function that does a http post request
    def httpPost(self, position, message):
        data = bytes(urllib.parse.urlencode({"command" + str(position): message}).encode())
        urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php", data)

    # Helper function that does HTTP get request
    def httpGet(self):
        f = urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")  # open url
        myfile = f.read()  # read url contents
        return self.command

    def startUp(self):
        self.getListConstant()
        while self.user1Check() != "T":
            time.sleep(0.5)
        self.getListFirstTime()

    # Updates the picturesToGoTO as an array, T means that the user wish's to go to the painting, F means they do not,
    # This will be the union of both users wishes
    def getListFirstTime(self):
        self.picturesToGoTO = self.httpGet()

    # This will be used to constantly update the list AFTER the first instance
    def getListConstant(self):
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

    # Checks if the user wants to go to the toilet or the exit
    def toiletCheck(self):
        return self.commands[14]

    # Updates the user once they have arrived at the TOILET
    def toiletArrived(self):
        self.httpPost(14, "A")

    # Updates the user once they have arrived at the EXIT
    def exitArrived(self):
        self.httpPost(15, "A")

    # Checks if the user wishes for RoboTOur to stop
    def stopCheck(self):
        return self.commands[11]

    ############
    # THESE ARE NOT FOR CD2, Don't worry about it now

    # Checks if user1 has submitted their painting requests
    def user1Check(self):
        return self.commands[16]

    # Checks if user2 has submitted their painting requests
    def user2Check(self):
        return self.commands[17]

    # Check if the user wishes to change the speed
    def speedCheck(self):
        return self.commands[13]

    # Update the users screen with the artPiece they should be displayed - simply pass in the next optimal artPiece and
    # the rest is sorted
    def updateArtPiece(self, nextArtWork):
        if self.previousArtPiece != "-1":
            # self.httpPost(previousArtPiece, "F")
            self.httpPost(nextArtWork, "N")
            self.previousArtPiece = nextArtWork

    ###########

    # Checks if user wants to cancel the tour
    def cancelTourCheck(self):
        return self.commands[12]

    # Check if the user wishes to skip the tour
    def skipCheck(self):
        return self.commands[10]

    def constantCheck(self):
        while True:
            self.getListConstant()
            if self.toiletCheck() == "T":
                # Do something to take the user to the toilet2
                pass
            if self.exitArrived() == "T":
                # Take the user to the exit
                pass
            if self.skipCheck() == "Y":
                # Do something to skip the next artPiece
                pass
            if self.stopCheck() == "T":
                # Stop the robot until the user presses stop
                pass
            if self.stopCheck() == "F":
                # Start the robot, iff if is not started already
                pass
            time.sleep(2)  # Sleep for 2 seconds
            print("toilet: " + self.toiletCheck())
            print("skip: " + self.skipCheck())
            print("stop: " + self.stopCheck())

    def getCommands(self):
        return self.commands

    def getPicturesToGo(self):
        return self.picturesToGoTO

    while True:
        print(user1Check())
