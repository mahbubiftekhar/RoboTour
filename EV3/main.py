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
colourSensorRight = ev3.ColorSensor(ev3.INPUT_1)
colourSensorLeft = ev3.ColorSensor(ev3.INPUT_4)


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
    if(not colourSensorLeft.connected):
        print("ColorLeft not connected")
    if(not colourSensorRight.connected):
        print("ColorRight not connected")
    if(not sonarLeft.connected):
        print("SonarLeft not connected")
    print('Please check all sensors and actuators are connected.')
    exit()

############################################################

##################### SENSOR AND ACTUATOR FUNCTIONS ############################
def getColourRight():
    return colourSensorRight.color

def getColourLeft():
    return colourSensorLeft.color

def isRightLineDetected(): # Right Lego sensor
    print(getColourRight())
    return getColourRight() == 6

def isLeftLineDetected():
    print(getColourLeft())
    return getColourLeft() == 6

def isLineDetected():
    answer = (isLeftLineDetected() or isRightLineDetected())
    print(answer)
    return answer

def isWallDetected():
    return (getColourLeft == 1 or getColourRight == 1)

def getSonarReadingsFront():
    return sonarFront.value()

def getSonarReadingsLeft():
    return sonarLeft.value()

def getSonarReadingsRight():
    return sonarRight.value()

def isFrontObstacle():
    return (getSonarReadingsFront() < obstacle_detection_distance)

def isLeftSideObstacle():
    return (getSonarReadingsLeft() < side_distance)

def isRightSideObstacle():
    return (getSonarReadingsRight() < side_distance)


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

def turnBack(): # 180
    motorLeft.run_timed(speed_sp=400,time_sp=1000)
    motorRight.run_timed(speed_sp=-400, time_sp=1000)

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

def isLost():
    speak("I am lost, please help.")
 
##################### OBSTACLE AVOIDANCE #######################
def obstacleAvoidance():
    while(True):
        #Tests
        #while (True):
        #    print("Left:", getColourLeft(), "    Right:", getColourRight())
        #    time.sleep(0.2)

        if(command=="FORWARD"):
            if(isFrontObstacle()):
                stopWheelMotor()
                print("Stop at: (Front) ", sonarFront.value())
                commandNext = 'RIGHT' # Example
                getReadyForObstacle(commandNext) # step 1
                print("Stop at: (Right) ",sonarRight.value())
                goAroundObstacle(commandNext)
                getBackToLine(commandNext)


def getReadyForObstacle(direction): #90 degree
    if (direction == 'RIGHT'):
        while(not isLeftSideObstacle()):
            turnRight(10)

        while(isLineDetected()):
            turnRight(10)
    else:  # All default will go through the Left side. IE
        #print(sonarRight.value())
        while (not isRightSideObstacle()):
            turnLeft(10)
        while(isLineDetected()):
            turnLeft(10)

    while(isLineDetected()):
        turnLeft(10)

def goAroundObstacle(direction):
    set_distance = 15
    stopping_distance = 8 # for stopping of the other 2 sensors
    if (direction == 'RIGHT'):
        while(not isLineDetected()):
            if (getSonarReadingsLeft() < set_distance):
                turn(200, 100, 100)
            #if (isWallDetected()):
                #turnBack()
                #goAroundObstacle('LEFT')
                #break;
            #if (getSonarReadingsRight() < stopping_distance or getSonarReadingsFront() < stopping_distance*10):
                #time.sleep(1)
            else:
                turn(100, 300, 100)
    else: # All default will go through the Left side. IE
        while(not isLineDetected()):
            if (getSonarReadingsRight() < set_distance):
                turn(100, 200, 100)
            else:
                turn(300, 100, 100)

def getBackToLine(direction):
    print("Find line!")
    if (direction == 'RIGHT'):
        while(isLeftLineDetected()):
            turn(100,-100,100)
        while(not isLeftLineDetected()):
            turn(250,0,100)
        print("Find line again!")
    else:
        while(isRightLineDetected()):
            turn(-100,100,100)
        while(not isRightLineDetected()):
            turn(0,250,100)
        print("Find line again!")

"""
def keepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        moveBackward(100,100)
"""


############################################################
obstacleAvoidanceThread = Thread(target=obstacleAvoidance)
#obstacleAvoidanceThread.start()

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
from line_sensor import LineDetector

command = "FORWARD"
colourSensorLeft.mode = 'COL-COLOR'
colourSensorRight.mode = 'COL-REFLECT'

target = 40

errorSumR = 0
oldR = colourSensorRight.value()
oldL = colourSensorLeft.value()
turning = False
timesTurned = 0
nextDirection = 'RIGHT'
#ld = LineDetector(colourSensorRight.value)
try:
    while(True):
#        colourSensorLeft.mode = 'COL-COLOR'
        #colourSensorRight.mode = 'COL-REFLECT'
        colourSensorLeft.mode = 'COL-REFLECT'
        baseSpeed = 90
        currR = colourSensorRight.value()
        currL = colourSensorLeft.value()
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
        print(str(currL) + "  "  + str(currR))
        if(currL > 60 and currR > 60):
            print("BRANCH")
            if(nextDirection == 'RIGHT'):
                motorRight.run_timed(speed_sp= -150,time_sp = 600)
                motorLeft.run_timed(speed_sp= 250,time_sp = 800)
                motorLeft.wait_until_not_moving()
                motorRight.wait_until_not_moving()
                nextDirection = 'LEFT'
            elif(nextDirection == 'FORWARD'):
                motorRight.run_timed(speed_sp= 100,time_sp = 600)
                motorLeft.run_timed(speed_sp= 100,time_sp = 600)
                motorLeft.wait_until_not_moving()
                motorRight.wait_until_not_moving()
                nextDirection = 'LEFT'
            elif(nextDirection == 'LEFT'):
                motorLeft.run_timed(speed_sp= -200,time_sp = 500)
                motorRight.run_timed(speed_sp= 200,time_sp = 1000)
                motorLeft.wait_until_not_moving()
                motorRight.wait_until_not_moving()
                nextDirection = 'RIGHT'
except:
    motorLeft.stop()
    motorRight.stop()
    raise







#moveForward(300,10000)

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
        elif(command == "FORWARD"):colourSensorLeft
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
