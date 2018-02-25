#! /usr/bin/env python3
# Core imports
# from urllib.parse import urlencode
# from urllib.request import Request, urlopen
# from urllib import request, parse
import urllib.request
import urllib.parse
# import _thread
import time

### GLOBAL VARIABLES ###
previousArtPiece = "-1"

## THIS WILL CONTAIN THE ARTPIECES THAT THE USER
#  WANTS TO GO TO, THIS WILL NOT CHANGE DURING THE TRIP
picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
commands = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]


# Helper function that does a http post request
def httpPost(position, message):
    data = bytes(urllib.parse.urlencode({"command" + str(position): message}).encode())
    urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php", data)


# Helper function that does HTTP get request
def httpGet():
    f = urllib.request.urlopen("http://homepages.inf.ed.ac.uk/s1553593/receiver.php")  # open url
    myfile = f.read()  # read url contents
    command = myfile.decode("utf-8")  # convert bytearray to string
    return command


# Updates the picturesToGoTO as an array, T means that the user wish's to go to the painting, F means they do not,
# This will be the union of both users wishes
def getListFirstTime():
    global picturesToGoTO
    picturesToGoTO = httpGet()


# This will be used to constantly update the list AFTER the first instance
def getListConstant():
    global commands
    commands = httpGet()


# Resets the entire list online,
# should be called once the robot is finnished giving the tour and returns to the
def resetListOnServer():
    global picturesToGoTO
    for x in range(0, 17):
        # Updating the list online
        httpPost(x, "F")
    picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]


# Checks if the user wants to go to the toilet or the exit
def toiletCheck():
    return commands[14]


# Updates the user once they have arrived at the TOILET
def toiletArrived():
    httpPost(14, "A")


# Updates the user once they have arrived at the EXIT
def exitArrived():
    httpPost(15, "A")


# Checks if the user wishes for RoboTOur to stop
def stopCheck():
    return commands[11]


############
# THESE ARE NOT FOR CD2, Don't worry about it now

# Checks if user1 has submitted their painting requests
def user1Check():
    return commands[16]


# Checks if user2 has submitted their painting requests
def user2Check():
    return commands[17]


# Check if the user wishes to change the speed
def speedCheck():
    return commands[13]


# Update the users screen with the artPiece they should be displayed - simply pass in the next optimal artPiece and
# the rest is sorted
def updateArtPiece(nextArtWork):
    global previousArtPiece
    if previousArtPiece != "-1":
        httpPost(previousArtPiece, "F")
    httpPost(nextArtWork, "N")
    previousArtPiece = nextArtWork


###########

# Checks if user wants to cancel the tour
def cancelTourCheck():
    return commands[12]


# Check if the user wishes to skip the tour
def skipCheck():
    return commands[10]


def constantCheck():
    while True:
        getListConstant()
        if toiletCheck() == "T":
            # Do something to take the user to the toilet2
            pass
        if exitArrived() == "T":
            # Take the user to the exit
            pass
        if skipCheck() == "Y":
            # Do something to skip the next artPiece
            pass
        if stopCheck() == "T":
            # Stop the robot until the user presses stop
            pass
        if stopCheck() == "F":
            # Start the robot, iff if is not started already
            pass
        time.sleep(2)  # Sleep for 2 seconds
        print("toilet: " + toiletCheck())
        print("skip: " + skipCheck())
        print("stop: " + stopCheck())


constantCheck()
