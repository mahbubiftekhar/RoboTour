#! /usr/bin/env python3

class Node(object):
    def __init__(self, id, front, back, right, left):
        self.id = id;
        self.right = right;
        self.left = left;
        self.front = front;
        self.back = back;

    def getPossiblePath(self):
        numberOfPaths = 0
        if(self.right == None):
            numberOfPaths += 1
        if (self.left == None):
            numberOfPaths += 1
        if (self.front == None):
            numberOfPaths += 1
        if (self.back == None):
            numberOfPaths += 1

        return numberOfPaths

