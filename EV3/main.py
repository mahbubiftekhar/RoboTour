#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re
from threading import Thread
from sensor_hub import *
from comms import *
from dijkstra import *


####################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 200 # in mm
side_distance = 17
link = "https://homepages.inf.ed.ac.uk/s1553593/receiver.php"
pointerState = ""
startPosition = 10 # Toilet
currentLocation = startPosition
robotOrientation = "N" # N,S,W,E (North South West East)
remainingPicturesToGo = []
oMap = dict() # Map for Orientation between neighbouring points
dMap = dict() # Map for Distance between neighbouring points

############################################################

####################### SETUP SENSORS ######################
hub = SensorHub()
sonarFront = ev3.UltrasonicSensor(ev3.INPUT_2)
sonarFront.mode = 'US-DIST-CM' # Will return value in mm
sonarLeft = HubSonar(hub, 's0')
sonarRight = HubSonar(hub,'s1')
motorPointer = ev3.LargeMotor('outC')
motorLeft = ev3.LargeMotor('outB')
motorRight= ev3.LargeMotor('outD')
colourSensorRight = ev3.ColorSensor(ev3.INPUT_1)
colourSensorLeft = ev3.ColorSensor(ev3.INPUT_4)
colourSensorLeft.mode = 'COL-REFLECT'
colourSensorRight.mode = 'COL-REFLECT'

lineThreshold = 57
wallThreshold = 15


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
    return colourSensorRight.value()

def getColourLeft():
    return colourSensorLeft.value()

def isRightLineDetected(): # Right Lego sensor
    # print(getColourRight())
    return getColourRight() > lineThreshold

def isLeftLineDetected():
    # print(getColourLeft())
    return getColourLeft() > lineThreshold

def isLineDetected():
    return (isLeftLineDetected() or isRightLineDetected())

def isWallDetected():
    return (getColourLeft() < wallThreshold or getColourRight() < wallThreshold)

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

def turnRightNinety(): # 90
    motorLeft.run_timed(speed_sp=175,time_sp=1000)
    motorRight.run_timed(speed_sp=-175, time_sp=1000)

def turnLeftNinety(): # -90
    motorLeft.run_timed(speed_sp=-175,time_sp=1000)
    motorRight.run_timed(speed_sp=175, time_sp=1000)

def stopWheelMotor():
    #print(sonar.value())
    motorLeft.stop(stop_action="hold")
    motorRight.stop(stop_action="hold")

def waitForMotor():
    motorLeft.wait_until_not_moving()
    motorRight.wait_until_not_moving()

def speak(string):
    ev3.Sound.speak(string)

def turnPointer(direction): # Turn 90
    if (direction == "CW"):
        motorPointer.run_timed(speed_sp=-414, time_sp=1000)
        pointerState = "CW"
        time.sleep(5)
    if (direction == "ACW"):
        motorPointer.run_timed(speed_sp=414, time_sp=1000)
        pointerState = "ACW"
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

def initialiseMap():
    # Orientation from point X to Y is N/S/W/E
    # 38 edges in total
    oMap[(0, 1)] = "S"
    oMap[(0, 8)] = "N"
    oMap[(1, 12)] = "S"
    oMap[(1, 0)] = "N"
    oMap[(2, 9)] = "N"
    oMap[(2, 3)] = "S"
    oMap[(3, 2)] = "N"
    oMap[(3, 13)] = "S"
    oMap[(4, 11)] = "S"
    oMap[(4, 14)] = "N"
    oMap[(5, 14)] = "WS" # Special Case
    oMap[(5, 6)] = "E"
    oMap[(6, 5)] = "W"
    oMap[(6, 7)] = "E"
    oMap[(7, 15)] = "ES"
    oMap[(7, 6)] = "W"
    oMap[(8, 0)] = "S"
    oMap[(8, 9)] = "E"
    oMap[(8, 14)] = "W"
    oMap[(9, 15)] = "E"
    oMap[(9, 2)] = "S"
    oMap[(9, 8)] = "W"
    oMap[(10, 11)] = "N"
    oMap[(11, 10)] = "S"
    oMap[(11, 4)] = "N"
    oMap[(11, 12)] = "E"
    oMap[(12, 13)] = "E"
    oMap[(12, 1)] = "N"
    oMap[(12, 11)] = "W"
    oMap[(13, 3)] = "N"
    oMap[(13, 15)] = "EN"
    oMap[(13, 12)] = "W"
    oMap[(14, 4)] = "S"
    oMap[(14, 8)] = "E"
    oMap[(14, 5)] = "NE"
    oMap[(15, 13)] = "SW"
    oMap[(15, 9)] = "W"
    oMap[(15, 7)] = "NW"

    # Distance Map
    dMap = {
        '0': {'1':26, '8':21},
        '1': {'0':26, '12':19.5},
        '2': {'3':26.5, '9':19.5},
        '3': {'2':26.5, '13':20},
        '4': {'11':33.5, '14':31.5},
        '5': {'6':27, '14':46},
        '6': {'5':27, '7':28},
        '7': {'6':28, '15':46.5},
        '8': {'0':21, '9':31.5, '14':28},
        '9': {'2':19.5, '8':31.5, '15':32},
        '10': {'11':20},
        '11': {'4':33.5, '10':20, '12':28},
        '12': {'1':19.5, '11':28, '13':32},
        '13': {'3':20, '12':32, '15':85},
        '14': {'4':31.5, '5':46, '8':28},
        '15': {'7':46.5, '9':32, '13':85}
    }

def getClosestPainting():
    pass

def getArtPiecesFromApp():
    pictures = server.getCommands()
    picturesToGo = []
    for index in range(10):
        if (pictures[index] == "T"):
            picturesToGo.append(index)
    return picturesToGo

def onPauseCommand():
    pass

def onResumeCommand():
    pass

def isLost():
    speak("I am lost, please help.")

##################### OBSTACLE AVOIDANCE #######################

def getReadyForObstacle(direction): #90 degree
    print("GET READY FOR OBSTACLE")
    if (direction == 'RIGHT'):
        turnRightNinety()
        waitForMotor()
        moveForward(100,500)

    else:  # All default will go through the Left side. IE
        turnLeftNinety()
        waitForMotor()


def goAroundObstacle(direction):
    print("GO AROUND OBSTACLE")
    set_distance = 11
    if (direction == 'RIGHT'):
        while(not isLineDetected()):
            '''
            if (isWallDetected()):
                turnBack()
                waitForMotor()
                goAroundObstacle('LEFT')
                break;
            '''
            if(getSonarReadingsFront() < set_distance*10):
                turnRightNinety()
                waitForMotor()
            elif (getSonarReadingsLeft() < set_distance):
                turn(100, 50, 100)
            else:
                turn(50, 150, 100)

    else: # All default will go through the Left side. IE
        while(not isLineDetected()):
            '''
            if (isWallDetected()):
                turnBack()
                waitForMotor()
                goAroundObstacle('RIGHT')
                break;
            '''
            if(getSonarReadingsFront() < set_distance*10):
                turnLeftNinety()
                waitForMotor()
            elif (getSonarReadingsRight() < set_distance):
                turn(50, 100, 100)
            else:
                turn(150, 50, 100)

def getBackToLine(direction):
    print("GET BACK TO LINE")
    if (direction == 'RIGHT'):
        if(isLeftLineDetected()):
            ## That means when it detect the line, it is not facing to the obstacle
            pass
        else:
            ## That means when it detect the line, it is facing to the obstacle
            while(not isLeftLineDetected()):
                turn(150,-100,100)

        while(isLeftLineDetected()):
            turn(100,100,100)
        while(not isLeftLineDetected()):
            turn(150,-100,100)
        print("Find line again!")
    else:
        if(isRightLineDetected()):
            ## That means when it detect the line, it is not facing to the obstacle
            pass

        else:
            ## That means when it detect the line, it is facing to the obstacle
            while(not isRightLineDetected()):
                turn(-100,150,100)

        while(isRightLineDetected()):
            turn(100,100,100)
        while(not isRightLineDetected()):
            turn(-100,150,100)

        print("Find line again!")

def waitForUserToGetReady():
    print("Press left for single user and press right for double user...")
    buttonEV3 = ev3.Button()
    while(True):
        if (buttonEV3.left):
            print("Waiting for User 1 to complete...")
            server.startUpSingle()
            print("User 1 is ready!")
            break
        elif(buttonEV3.right):
            print("Waiting for User 1 and User 2 to complete...")
            server.startUpDouble()
            print("Both users are ready!")
            break



"""
def keepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        moveBackward(100,100)
"""

############################################################

##################### MAIN #################################

print("SensorHub have set up.")
#speak("Carson, we love you. Group 18. ")

###################SETUP SERVER############################
server = Server()
waitForUserToGetReady()
print("Users are ready!")

###########################################################


target = 40
errorSumR = 0
oldR = colourSensorRight.value()
oldL = colourSensorLeft.value()


remainingPicturesToGo = getArtPiecesFromApp()


try:
    while(True):
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
                    motorRight.run_timed(speed_sp= -150,time_sp = 600)
                    motorLeft.run_timed(speed_sp= 250,time_sp = 800)
                    motorLeft.wait_until_not_moving()
                    motorRight.wait_until_not_moving()
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
                    motorLeft.run_timed(speed_sp= -200,time_sp = 500)
                    motorRight.run_timed(speed_sp= 200,time_sp = 1000)
                    motorLeft.wait_until_not_moving()
                    motorRight.wait_until_not_moving()

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


