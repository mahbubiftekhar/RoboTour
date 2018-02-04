#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re
from threading import Thread



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
motorLeft = ev3.LargeMotor('outA')
motorRight= ev3.LargeMotor('outC')

if(motorHand.connected & sonar.connected & motorLeft.connected & motorRight.connected):
    print('All sensors and motors connected')
else:
    if(not motorHand.connected):
        print("MotorHand not connected")
    if(not sonar.connected):
        print("Sonar not connected")
    if(not motorLeft.connected):
        print("MotorLeft not connected")
    if(not motorRight.connected):
        print("MotorRight not connected")
    print('Please check all sensors and actuators are connected.')
    exit()

############################################################

##################### FUNCTIONS ############################
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
    motorLeft.run_timed(speed_sp=200, time_sp = degree*10)
    motorRight.run_timed(speed_sp=-200, time_sp = degree*10)

def turnLeft(degree):
    motorRight.run_timed(speed_sp=300, time_sp=degree*10)
    motorLeft.run_timed(speed_sp=-300, time_sp=degree*10)

def stopWheelMotor():
    print(sonar.value())
    motorLeft.stop()
    motorRight.stop()

def speak(string):
    ev3.Sound.speak(string)

def lineFinished():
    return False

def onPauseCommand():
    pass

def onResumeCommand():
    pass

##################### Multithreading #######################
def isThereObstacle():
    #print(sonar.value())
    if(sonar.value() < obstacle_detection_distance):
        #print("Obstacle detected.")
        #speak("There is an obstacle detected.")
        return True
    else:
        #print("No obstacle detected.")
        return False
"""
def keepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        moveBackward(100,100)

class ObstacleAvoidanceThread(Thread):
    def __init__(self):
        ''' Constructor. '''
        Thread.__init__(self)

    def run(self):
        while(True):
            if(isThereObstacle()):
                keepDistance()
"""

def obstacleAvoidance():
    while(True):
        if(command=="FORWARD"):
            if(isThereObstacle()):
                stopWheelMotor()


############################################################

speak("Carson, we love you. Group 18. ")

"""
obstacleAvoidanceThread = ObstacleAvoidanceThread()
obstacleAvoidanceThread.setName("ObstacleAvoidanceThread")
obstacleAvoidanceThread.start()
"""

t = Thread(target=obstacleAvoidance)
t.start()
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
            moveForward(300, 10000)
        elif(command == "BACKWARDS"):
            moveBackward(300, 5000)
        elif(command == "SPEAK"):
            speak("Hello, I am RoboTour")
        elif(command == "RIGHT"):
            turnRight(10000)
        elif(command == "LEFT"):
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
        moveForward(300, 100)
    else:
        keepDistance()

"""
