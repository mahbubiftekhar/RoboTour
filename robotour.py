#! /usr/bin/env python3
# Core imports
import time
import ev3dev.ev3 as ev3

####################### GLOBAL VARIABLE ####################
obstacle_detection_distance = 250 # in mm

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

def getArtPiecesFromApp(): # returns an list of direction commands
    #example
    pieces = ["Monalisa"]
    return pieces # returns list

def moveForward():
    motorLeft.run_timed(speed_sp=300, time_sp=100)
    motorRight.run_timed(speed_sp=300, time_sp=100)
    #waitForMotor(motorLeft)                         # Can use something like this to prevent the program from progressing
    #waitForMotor(motorRight)                         # Can use something like this to prevent the program from progressing
    pass

def moveBackward():
    motorLeft.run_timed(speed_sp=-100, time_sp=100)
    motorRight.run_timed(speed_sp=-100, time_sp=100)


def turnRight():
    pass


def turnLeft():
    pass

def keepDistance():
    if(abs(sonar.value() - obstacle_detection_distance) > 100):
        moveBackward()

def waitForMotor(motor):
    time.sleep(0.1)         # Make sure that motor has time to start
    while motor.state==["running"]:
        print('Motor is still running')
        time.sleep(0.1)

def lineFinished():

    return True

##################### MAIN #################################

dictionary = {
    "Monalisa" : ["Forward", "Left", "Right"]
}

artPieces = getArtPiecesFromApp()
commands = dictionary[artPieces[0]]

for command in commands:
    if(command == "Forward"):
        while(not lineFinished()):
            if(not isThereObstacle()):
                moveForward()
            else:
                keepDistance()


    elif(command == "Left"):
        pass
    elif(command == "Right"):
        pass
    else:
        pass

"""
while(1):
    if(not isThereObstacle()):
        moveForward()
    else:
        keepDistance()

"""
