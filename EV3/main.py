#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3
from urllib.request import urlopen
import re
from threading import Thread



####################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 100 # in mm
side_distance = 90
link = "https://homepages.inf.ed.ac.uk/s1553593/receiver.php"
command = ""
previouscommandid = "1"
currentcommandid = "0"
preDifference = 0
############################################################

####################### SETUP SENSORS ######################
sonar = ev3.UltrasonicSensor(ev3.INPUT_2)
sonar.mode = 'US-DIST-CM' # Will return value in mm
sonarLeft = ev3.UltrasonicSensor(ev3.INPUT_4)
sonarLeft.mode = 'US-DIST-CM' # Will return value in mm

motorHand = ev3.LargeMotor('outC')
motorLeft = ev3.LargeMotor('outB')
motorRight= ev3.LargeMotor('outD')
colourSensorRight = ev3.ColorSensor(ev3.INPUT_1)
colourSensorLeft = ev3.ColorSensor(ev3.INPUT_3)


if(motorHand.connected & sonar.connected &
       motorLeft.connected & motorRight.connected &
       colourSensorLeft.connected & colourSensorRight.connected &
       sonarLeft.connected):
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
    if(not colourSensorLeft.connected):
        print("ColorLeft not connected")
    if(not colourSensorRight.connected):
        print("ColorRight not connected")
    if(not sonarLeft.connected):
        print("SonarLeft not connected")
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

def isSideObstacle():
    #print(sonar.value())
    if(sonarLeft.value() < side_distance):
        #print("Obstacle detected.")
        #speak("There is an obstacle detected.")
        return True
    else:
        #print("No obstacle detected.")
        return False

def noObstacle():
    if(sonarLeft.value()>250):
        return True
    else:
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

                #if(command.next() == 'LEFT'):
                step1()

                print(sonarLeft.value())
                goAroundObstacle()
                #turnRight(90)
                """elif(command.next() == 'RIGHT'):
                    turnRight(90)
                    while(not isLineDetected()):
                        goAroundObstacle('LEFT')
                    turnLeft(90)
                    """
def step1():        #90 degree
    while(not isSideObstacle()):
        turnRight(5)




def goAroundObstacle():
    preDistance = sonarLeft.value()
    while(not isLineDetected()):


        baseSpeedLeft = 100
        baseSpeedRight = 100

        currentDistance = sonarLeft.value()
        print(currentDistance)
        #if(currentDifference > 20):
            #currentDifference = 20
        timeMod = 1
        k = 0
        sp = 0

        ##For corner
        if(currentDistance > 500):
            print("Corner!")
            #while(sonarLeft.value()>200):

            turn(25,250,50)
            print(sonarLeft.value())
            #print("Out!")
            #turn(100,100,100)
            #exit()


        ## When the gap is too larger/small
        if(currentDistance>side_distance):
            turn(100,200,50)
            timeMod = 0.5
            #turn(200,100,50)
        elif(currentDistance<side_distance):
            turn(400,100,50)
            timeMod = 0.5
            #turn(100,200,50)

        ## For distance incresing/decresing
        if(currentDistance>preDistance):
            k=2
        elif(currentDistance<preDistance):
            k=-2

        turn(baseSpeedLeft - 100*(k-sp), baseSpeedRight + 100*(k+sp), 50* timeMod)

        preDistance = currentDistance


        '''
        if(currentDistance>side_distance & currentDistance>=preDistance):
            turn(50,100,50)
        elif(currentDistance>side_distance &currentDistance<preDistance):
            turn(50,50,50)
        elif(currentDistance<side_distance & currentDistance>preDistance):
            turn(50,50,50)
        elif(currentDistance<side_distance & currentDistance<=preDistance):
            turn(100,50,50)
        elif(currentDistance==side_distance & currentDistance>preDistance):
            turn(50,100,50)
        elif(currentDistance==side_distance & currentDistance<preDistance):
            turn(100,50,50)
        else:
            turn(50,50,50)
        preDistance = currentDistance
        '''


        '''
        while(sonarLeft.value()<95 and turnedRight < 2):
            print("Turn Right: ",sonarLeft.value())
            turnedRight+=1
            turnRight(5)

        moveForward(50,50)
        turnedLeft = 0
        while(sonarLeft.value()>=95 and turnedLeft < 2):
            print("Turn Left: ",sonarLeft.value())
            turnedLeft+=1
            turnLeft(5)


        '''
        #moveForward(300,1000)

        '''
        if(noObstacle()):
            turnLeft(30)
            waitForMotor()
            moveForward(300,1000)
            waitForMotor()
        else:
        '''



        '''
        if(isSideObstacle()):
            print("right")
            turnRight(10)
            moveForward(300,50)
        else:
            if(sonarLeft.value() > 200):
                turnLeft(90)
            else:
                print("left")
                turnLeft(10)
            moveForward(200,50)
        '''
def waitForMotor():
    motorLeft.wait_until_not_moving()
    motorRight.wait_until_not_moving()

def isLineDetected():
    return False

def isLost():
    speak("I am lost, please help.")


############################################################

#speak("Carson, we love you. Group 18. ")

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
