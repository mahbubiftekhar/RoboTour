# Adapted from source: http://www.gilles-bertrand.com/2014/03/dijkstra-algorithm-python-example-source-code-shortest-path.html

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
        print('shortest path: ' + str(path[::-1]) + " cost=" + str(distances[dest]))
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


if __name__ == "__main__":
    # import sys;sys.argv = ['', 'Test.testName']
    # unittest.main()

    graph = {
        'A': {'B': 10, 'C': 50, 'D': 40},
        'B': {'A': 10, 'D': 20},
        'C': {'A': 50},
        'D': {'B': 20, 'A': 40}
    }

    (path, distance) = dijkstra(graph, 'A', 'D')
    print(str(path))
    print(str(distance))
