#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re


####################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 250 # in mm
link = "https://homepages.inf.ed.ac.uk/s1553593/receiver.php"
command = ""
previouscommandid = "1"
currentcommandid = "0"


############################################################

####################### SETUP SENSORS ######################
sonar = ev3.UltrasonicSensor(ev3.INPUT_2)
sonar.mode = 'US-DIST-CM' # Will return value in mm
motorHand = ev3.LargeMotor('outB')
motorLeft=ev3.LargeMotor('outA')
motorRight=ev3.LargeMotor('outC')

if(motorHand.connected & sonar.connected & motorLeft.connected & motorRight.connected):
    print('All sensors and motors connected')
else:
    print('Please check all sensors and actuators are connected.')
    exit()

############################################################

##################### FUNCTIONS ############################
def isThereObstacle():
    print(sonar.value())
    if(sonar.value() < obstacle_detection_distance):
        print("Obstacle detected.")
        return True
    else:
        print("No obstacle detected.")
        return False

def isKeepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        return True
    else:
        return False


def getArtPiecesFromApp(): # returns an list of direction commands
    #example
    pieces = ["Monalisa"]
    return pieces # returns list

def getCommandFromServer():
    global currentcommandid
    global previouscommandid
    f = urlopen(link) #open url
    myfile = f.read() #read url contents
    string = myfile.decode("utf-8") #convert bytearray to string
    array = re.split('-', string)
    command = array[0]
    currentcommandid = array[1]
    #print("currentcommandid fun:" + currentcommandid)
    #print("previouscommandid fun:" + previouscommandid)
    return command

def moveForward(speed, time):
    motorLeft.run_timed(speed_sp=speed, time_sp=time)
    motorRight.run_timed(speed_sp=speed, time_sp=time)
    #waitForMotor(motorLeft)                         # Can use something like this to prevent the program from progressing
    #waitForMotor(motorRight)                         # Can use something like this to prevent the program from progressing
    pass

def moveBackward(speed, time):
    motorLeft.run_timed(speed_sp=-speed, time_sp=time)
    motorRight.run_timed(speed_sp=-speed, time_sp=time)

def turnRight(degree):
    motorLeft.run_timed(speed_sp=100, time_sp = degree*10)
    motorRight.run_timed(speed_sp=-100, time_sp = degree*10)

def turnLeft(degree):
    motorRight.run_timed(speed_sp=100, time_sp=degree*10)
    motorLeft.run_timed(speed_sp=-100, time_sp=degree*10)

def stopWheelMotor():
    motorLeft.stop()
    motorRight.stop()

def speak(string):
    ev3.Sound.speak(string)

def lineFinished():
    return False

##################### MAIN #################################


dictionary = {
    "Monalisa" : ["Forward", "Left", "Right"]
}

#artPieces = getArtPiecesFromApp()
#direction = dictionary[artPieces[0]]

while (True):
    #print("currentcommandid before:" + currentcommandid)
    #print("previouscommandid before" + previouscommandid)
    command = getCommandFromServer()
    if (previouscommandid != currentcommandid):
        previouscommandid = currentcommandid
        #print("currentcommandid after:" + currentcommandid)
        #print("previouscommandid after" + previouscommandid)
        if(command == "STOP"):
            stopWheelMotor()
        elif(command == "FORWARD"):
            moveForward(300, 5000)
        elif(command == "BACKWARDS"):
            moveBackward(300, 5000)
        elif(command == "SPEAK"):
            speak("Hello, I am RoboTour")
        elif(command == "GOAROUND"):
                turnLeft(10000)
        else:
            pass

"""

def waitForMotor(motor):
    time.sleep(0.1)         # Make sure that motor has time to start
    while motor.state==["running"]:
        print('Motor is still running')
        time.sleep(0.1)

for command in commands:
    if(command == "Forward"):
        while(not lineFinished()):
            if(not isThereObstacle()):
                moveForward(300, 100)
            else:
                if(isKeepDistance()):
                    moveBackward(100, 100)
    elif(command == "Left"):
        while(lineFinished()):
            turnLeft(10)
    elif(command == "Right"):
        while(lineFinished()):
            turnRight(10)
    else:
        pass


while(1):
    if(not isThereObstacle()):
        moveForward()
    else:
        keepDistance()

"""
