# Adapted from source: https://stackoverflow.com/questions/22897209/dijkstras-algorithm-in-python
nodes = ('A', 'B', 'C', 'D')
distances = {
    'A': {'B': 10, 'C': 50, 'D': 40},
    'B': {'A': 10, 'D': 20},
    'C': {'A': 50},
    'D': {'B': 20, 'A': 40}
}

unvisited = {node: None for node in nodes} #using None as +inf
print(unvisited)
visited = {}
start_node = 'A'
currentDistance = 0
unvisited[start_node] = currentDistance

path = list()
while True:
    #path.append(start_node)
    for neighbour, distance in distances[start_node].items():
        if neighbour not in unvisited: continue
        newDistance = currentDistance + distance

        if unvisited[neighbour] is None or unvisited[neighbour] > newDistance:
            unvisited[neighbour] = newDistance



    #(_,_,path) = visited[start_node]

    visited[start_node] = (currentDistance, path)
    del unvisited[start_node]
    if not unvisited: break
    candidates = [node for node in unvisited.items() if node[1]]
    print(candidates)
    start_node, currentDistance = sorted(candidates, key = lambda x: x[1])[0]


print(visited)

