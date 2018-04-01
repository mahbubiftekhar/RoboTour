# Adapted from source: http://www.gilles-bertrand.com/2014/03/dijkstra-algorithm-python-example-source-code-shortest-path.html
import sys
def dijkstra(graph, src, dest, visited=[], distances={}, predecessors={}):
    """ calculates a shortest path tree routed in src
    """
    # a few sanity checks
    if src not in graph:
        raise TypeError('The root of the shortest path tree cannot be found')
    if dest not in graph:
        raise TypeError('The target of the shortest path cannot be found')
        # ending condition
    if src == dest:
        # We build the shortest path and display it
        path = []
        pred = dest
        while pred != None:
            path.append(pred)
            pred = predecessors.get(pred, None)
        # Debug
        # print('shortest path: ' + str(path[::-1]) + " cost=" + str(distances[dest]))
        return (path[::-1], distances[dest])
    else:
        # if it is the initial  run, initializes the cost
        if not visited:
            distances[src] = 0
        # visit the neighbors
        for neighbor in graph[src]:
            if neighbor not in visited:
                new_distance = distances[src] + graph[src][neighbor]
                if new_distance < distances.get(neighbor, float('inf')):
                    distances[neighbor] = new_distance
                    predecessors[neighbor] = src
        # mark as visited
        visited.append(src)
        # now that all neighbors have been visited: recurse
        # select the non visited node with lowest distance 'x'
        # run Dijskstra with src='x'
        unvisited = {}
        for k in graph:
            if k not in visited:
                unvisited[k] = distances.get(k, float('inf'))
        x = min(unvisited, key=unvisited.get)
        return dijkstra(graph, x, dest, visited, distances, predecessors)




"""
# Test
def getClosestPainting(map, currentLocation, paintings):
    shortestDistance = sys.maxint
    shortestPath = None

    for painting in paintings:
        (path, distance) = dijkstra(map, currentLocation, painting, [], {}, {})
        if(shortestDistance > distance):
            shortestDistance = distance
            shortestPath = path
    return shortestPath
if __name__ == "__main__":
    # import sys;sys.argv = ['', 'Test.testName']
    # unittest.main()
    graph = {
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



    #(path, distance) = dijkstra(graph, '3', '1')
    #print(str(path))
    #print(str(distance))
    # Test
    currentLocation = '10'
    print(getClosestPainting(['12', '14', '13']))



"""