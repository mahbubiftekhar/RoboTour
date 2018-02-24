#! /usr/bin/env python3
# Core imports
#from urllib.parse import urlencode
#from urllib.request import Request, urlopen
#from urllib import request, parse
import urllib.request, urllib.parse
import  _thread
import time

####################### GLOBAL VARIABLES ####################
previousArtPiece = "1"
picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]

#Does HTTP post
def httpPost(link, message):
    data = bytes( urllib.parse.urlencode( {"command" : message } ).encode() )
    urllib.request.urlopen( link, data )
	
#Does HTTP get request
def httpGet(link):
    f = urllib.request.urlopen(link) #open url
    myfile = f.read() #read url contents
    command = myfile.decode("utf-8") #convert bytearray to string
    return command

#Updates the picturesToGoTO as an array, T means that the user wishs to go to that painint, F means they do not, This will be the union of both users wishes
def getList():
    global picturesToGoTO
    for x in range(0, 9):
        data = httpGet("http://homepages.inf.ed.ac.uk/s1553593/" + str(x) + ".php")
        if(data=="T"):
            #If the user wishes to go to this artPiece, set it here
            picturesToGoTO[x] = "T"
        else:
		    #If the user does not wish to go here
            picturesToGoTO[x] = "F"
    print(picturesToGoTO)
	
#Resets the entire list online, should be called once the robot is finnished giving the tour and returns to the 
def resetList():
	global picturesToGoTO
    for x in range(0, 9):
        httpPost("http://homepages.inf.ed.ac.uk/s1553593/" + str(x) + ".php", "F")
	picturesToGoTO = ["F", "F", "F", "F", "F", "F", "F", "F", "F", "F"]
		
#Checks if the user wants to go to the toilet or the exit
def toiletAndExitCheck():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/toilet.php")

#Updates the user once they have arrived at the exit
#Once the user arrives, pass Toilet or Exit as a parameter 
def toiletAndExitArrived(location):
	if(location == "exit" or location == "Exit" or location == "EXIT"):
		httpPost("http://homepages.inf.ed.ac.uk/s1553593/toilet.php", "A")
		link = "http://homepages.inf.ed.ac.uk/s1553593/stop.php"
		httpPost(link, "T")
	if(location == "toilet" or location == "Toilet" or location == "TOILET"):
		httpPost("http://homepages.inf.ed.ac.uk/s1553593/toilet.php", "B")
		link = "http://homepages.inf.ed.ac.uk/s1553593/stop.php"
		httpPost(link, "T")
    #Need to reset toilet to be 
    
#Checks if the user wishes for RoboTOur to stop
def stopCheck():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/stop.php")


############
#THESE ARE NOT FOR CD2,

#Checks if user1 has submitted their painting requests
def user1Check():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/user1.php")

#Checks if user2 has submitted their painting requests
def user2Check():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/user2.php")

#Check if the user wishes to change the speed
def speedCheck():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/speed.php")

#Update the users screen with the artPiece they should be displayed - simply pass in the next optimal artPiece and the rest is sorted
def updateArtPiece(id):
    global previousArtPiece
    if (previousArtPiece != "-1"):
        link = "http://homepages.inf.ed.ac.uk/s1553593/" + previousArtPiece + ".php"
        httpPost(link, "F")
    link = "http://homepages.inf.ed.ac.uk/s1553593/" + id + ".php"
    httpPost(link, "N")
    previousArtPiece = id
###########

#Checks if user wants to cancel the tour
def cancelTourCheck():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/cancel.php")

#Check if the user wishes to skip the tour
def skipCheck():
    return httpGet("http://homepages.inf.ed.ac.uk/s1553593/skip.php")

def constantCheck():
    while(True):
        toilet = toiletAndExitCheck()
        if(toilet=="T"):
            #Do something to take the user to the toilet2
            pass
        elif(toilet == "E"):
            #Take the user to the exit
            pass
        skip = skipCheck()
        if(skip=="Y"):
            #Do something to skip the next artPiece
            pass
        stop = stopCheck()
        if(stop=="T"):
            #Stop the robot until the user presses stop
            pass
        if(stop=="F"):
            #Start the robot, iff if is not started already
            pass
        time.sleep(2) #Sleep for 2 seconds
        print("toilet: " + toilet) 
        print("skip: " + skip)
        print("stop: " + stop)

constantCheck()
