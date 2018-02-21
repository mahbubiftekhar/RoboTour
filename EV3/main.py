#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re
from threading import Thread
from sensor_hub import *



####################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 150 # in mm
side_distance = 12
link = "https://homepages.inf.ed.ac.uk/s1553593/receiver.php"
command = ""
previouscommandid = "1"
currentcommandid = "0"
pointerState = ""
preDifference = 0
############################################################

####################### SETUP SENSORS ######################
hub = SensorHub()
sonarFront = ev3.UltrasonicSensor(ev3.INPUT_2)
sonarFront.mode = 'US-DIST-CM' # Will return value in mm
sonarLeft = HubSonar(hub, 's0')
sonarRight = HubSonar(hub,'s1')
#sonarLeft = ev3.UltrasonicSensor(ev3.INPUT_4)
#sonarLeft.mode = 'US-DIST-CM' # Will return value in mm
#sonarRight = None
#sonarRight.mode = 'US-DIST-CM' # Will return value in mm

motorPointer = ev3.LargeMotor('outC')
motorLeft = ev3.LargeMotor('outB')
motorRight= ev3.LargeMotor('outD')
#colourSensorRight = ev3.ColorSensor(ev3.INPUT_1)
#colourSensorLeft = ev3.ColorSensor(ev3.INPUT_3)


if(motorPointer.connected & sonarFront.connected &
       motorLeft.connected & motorRight.connected):
    print('All sensors and motors connected')
else:
    if(not motorPointer.connected):
        print("motorPointer not connected")
    if(not sonarFront.connected):
        print("Sonar not connected")
    if(not motorLeft.connected):
        print("MotorLeft not connected")
    if(not motorRight.connected):
        print("MotorRight not connected")
    '''
    if(not colourSensorLeft.connected):
        print("ColorLeft not connected")
    if(not colourSensorRight.connected):
        print("ColorRight not connected")
    if(not sonarLeft.connected):
        print("SonarLeft not connected")
    '''
    print('Please check all sensors and actuators are connected.')
    exit()

############################################################

##################### SENSOR AND ACTUATOR FUNCTIONS ############################
def getSonarReadingsFront():
    total = 0
    size = 5
    for i in range(0, size):
        total += sonarFront.value()
    return total/size

def getSonarReadingsLeft():
    total = 0
    size = 1
    for i in range(0, size):
        total += sonarLeft.value()
    return total/size

def getSonarReadingsRight():
    total = 0
    size = 1
    for i in range(0, size):
        total += sonarRight.value()
    return total/size

def isFrontObstacle():
    if(getSonarReadingsFront() < obstacle_detection_distance):
        return True
    else:
        return False

def isLeftSideObstacle():
    if(getSonarReadingsLeft() < side_distance):
        return True
    else:
        return False

def isRightSideObstacle():
    if(getSonarReadingsRight() < side_distance):
        #print("Find right obstale at: ",getSonarReadingsRight())
        return True
    else:
        return False

def moveForward(speed, time):
    motorLeft.run_timed(speed_sp=speed, time_sp=time)
    motorRight.run_timed(speed_sp=speed, time_sp=time)
    #waitForMotor(motorLeft)                         # Can use something like this to prevent the program from progressing
    #waitForMotor(motorRight)                         # Can use something like this to prevent the program from progressing

def moveBackward(speed, time):
    motorLeft.run_timed(speed_sp=-speed, time_sp=time)
    motorRight.run_timed(speed_sp=-speed, time_sp=time)

def turnRight(degree):
    motorLeft.run_timed(speed_sp=degree*10, time_sp = 300)
    motorRight.run_timed(speed_sp=-degree*10, time_sp = 300)

def turnLeft(degree):
    motorRight.run_timed(speed_sp=degree*10, time_sp=300)
    motorLeft.run_timed(speed_sp=-degree*10, time_sp=300)

def turn(left,right,time):
    motorLeft.run_timed(speed_sp=left,time_sp=time)
    motorRight.run_timed(speed_sp=right, time_sp=time)

def stopWheelMotor():
    #print(sonar.value())
    motorLeft.stop(stop_action="hold")
    motorRight.stop(stop_action="hold")

def waitForMotor():
    motorLeft.wait_until_not_moving()
    motorRight.wait_until_not_moving()

def speak(string):
    ev3.Sound.speak(string)

def turnPointer(direction):
    if (direction == "CW"):
        motorPointer.run_timed(speed_sp=-414, time_sp=1000)
        time.sleep(2)
    if (direction == "ACW"):
        motorPointer.run_timed(speed_sp=414, time_sp=1000)
        time.sleep(2)

def resetPointer():
    global pointerState
    print(pointerState)
    if (pointerState == "CW"):
        turnPointer("ACW")
    if (pointerState == "ACW"):
        turnPointer("CW")

def turnAndResetPointer(direction):
    global pointerState
    if (direction == "CW"):
        turnPointer("CW")
        pointerState="CW"
        print("2" + pointerState)
        resetPointer()
    elif (direction == "ACW"):
        turnPointer("ACW")
        pointerState="ACW"
        print("2" + pointerState)
        resetPointer()

######################################################################

####################### ROBOTOUR FUNCTIONS ###########################

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

def onPauseCommand():
    pass

def onResumeCommand():
    pass

def isLineDetected():
    return False

def isLost():
    speak("I am lost, please help.")

##################### OBSTACLE AVOIDANCE #######################
def obstacleAvoidance():
    while(True):
        if(command=="FORWARD"):
            if(isFrontObstacle()):
                stopWheelMotor()
                print("Stop at: (Front) ", sonarFront.value())
                commandNext = 'LEFT' # Example
                getReadyForObstacle(commandNext) # step 1
                print("Stop at: (Right) ",sonarRight.value())
                goAroundObstacle(commandNext)
                getBackToLine(commandNext)


def getReadyForObstacle(direction): #90 degree
    if (direction == 'RIGHT'):
        while(not isLeftSideObstacle()):
            turnRight(10)
    else:  # All default will go through the Left side. IE
        print(sonarRight.value())
        while (not isRightSideObstacle()):
            turnLeft(10)


def goAroundObstacle(direction):
    set_distance = 15
    if (direction == 'RIGHT'):
        while(not isLineDetected()):
            if (getSonarReadingsLeft() < set_distance):
                turn(300, 100, 100)
            else:
                turn(100, 200, 100)
    else: # All default will go through the Left side. IE
        while(not isLineDetected()):
            if (getSonarReadingsRight() < set_distance):
                turn(100, 300, 100)
            else:
                turn(200, 100, 100)


def getBackToLine(direction):
    pass

"""getSonarReadingsFront()
def keepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        moveBackward(100,100)
"""


############################################################
obstacleAvoidanceThread = Thread(target=obstacleAvoidance)
obstacleAvoidanceThread.start()

##################### MAIN #################################
while(getSonarReadingsRight()==0):
    time.sleep(5)
print("SensorHub have set up.")
#speak("Carson, we love you. Group 18. ")

#Use to test the pointer. The following code turns and resets in both direction 3x
#turnAndResetPointer("CW")
#time.sleep(1)
#turnAndResetPointer("CW")
#time.sleep(1)
#turnAndResetPointer("CW")
#time.sleep(1)
#turnAndResetPointer("ACW")
#time.sleep(1)
#turnAndResetPointer("ACW")
#time.sleep(1)
#turnAndResetPointer("ACW")


command = "FORWARD"
moveForward(300,10000)

"""
while (True):
    #print("currentcommandid before:" + currentcommandid)
    #print("previouscommandid before" + previouscommandid)
    command = getCommandFromServer()
    if (previouscommandid != currentcommandid):
        previouscommandid = currentcommandid
        #print("currentcommandid after:" + currentcommandid)
        #print("previouscommandid after" + previouscommandid)
        if(command == "STOP"):95
            stopWheelMotor()
        elif(command == "FORWARD"):
            moveForward(300, 10000)
        elif(command == "BACKWARDS"):
            moveBackward(300, 10000)
        elif(command == "SPEAK"):
            speak("Hello, I am RoboTour")
        elif(command == "RIGHT"):
            turnRight(10000)
        elif(command == "LEFT"):
            turnLeft(10000)
        else:
            pass

"""
