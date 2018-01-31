from node import Node
class Node(object):
    def __init__(self, id):
        self.id = id;
        self.right = None;
        self.left = None;
        self.front = None;
        self.back = None;

    def setLeft(self, node):
        self.left = node

    def setRight(self, node):
        self.right = node

    def setFront(self, node):
        self.front = node

    def setBack(self, node):
        self.back = node

    def getNumberOfPath(self):
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

    def getPossiblePaths(self):
        paths = list()
        if(self.right != None):
            paths.append(self.right)
        if (self.left != None):
            paths.append(self.left)
        if (self.front != None):
            paths.append(self.front)
        if (self.back != None):
            paths.append(self.back)

        return paths


class Map(object):
    costDictionary = {  #in mm
        "A-B": 10,
        "A-C": 50,
        "B-D": 20
    }

    nodeA = Node("A")
    nodeB = Node("B")
    nodeC = Node("C")
    nodeD = Node("D")

    nodeA.setRight(nodeB)
    nodeA.setLeft(nodeC)
    nodeB.setRight(nodeD)

    def dijsktraSearch(self, map, initial_node, goal_node):
        visited = list()
        frontier =  initial_node.getPossiblePaths()

        while len(frontier) >= 0:
            pass



