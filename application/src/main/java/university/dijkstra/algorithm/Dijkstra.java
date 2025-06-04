package university.dijkstra.algorithm;

import university.dijkstra.data_structures.List;
import university.dijkstra.data_structures.MinHeap;
import university.dijkstra.model.*;

public class Dijkstra {
  private int[] previous; // To reconstruct the path
  private double[] distances; // To store the shortest distances from the source
  // Simple node class for the priority queue

  static class QueueNode implements Comparable<QueueNode> {
    int vertexId;
    double distance;

    QueueNode(int vertexId, double distance) {
      this.vertexId = vertexId;
      this.distance = distance;
    }

    @Override
    public int compareTo(QueueNode other) {
      return Double.compare(this.distance, other.distance);
    }
  }

  public void findShortestPath(Vertex[] graph, Vertex source, Vertex destination) {
    MinHeap<QueueNode> pq = new MinHeap<QueueNode>();
    distances = new double[graph.length];
    previous = new int[graph.length];

    initializeDistancesAndPrevious(distances, previous);
    distances[source.getId()] = 0;
    pq.insert(new QueueNode(source.getId(), 0));

    while (!pq.isEmpty()) {
      QueueNode current = pq.extractMin();
      List<Edge> edges = graph[current.vertexId].getEdges();
      if (current.distance > distances[current.vertexId]) {
        continue; // Skip if we already found a better path
      }
      if (current.vertexId == destination.getId()) {
        break;
      }

      List.Node<Edge> node = edges.getHead();
      while (node != null) {
        Edge currentEdge = node.getData();
        double newDistance = distances[current.vertexId] + currentEdge.getWeight();

        if (newDistance < distances[currentEdge.getDestination().getId()]) {
          distances[currentEdge.getDestination().getId()] = newDistance;
          previous[currentEdge.getDestination().getId()] = current.vertexId;
          pq.insert(new QueueNode(currentEdge.getDestination().getId(), newDistance));
        }
        node = node.getNext();
      }

    }
  }

  public void initializeDistancesAndPrevious(double[] distances, int[] previous) {
    for (int i = 0; i < distances.length; i++) {
      distances[i] = Double.MAX_VALUE; // Initialize all distances to infinity
      previous[i] = -1;
    }
  }

  public List<Integer> reconstructPath(int source, int destination) {
    List<Integer> path = new List<>();
    int current = destination;

    while (current != -1 && current != source) {
      path.add(current);
      current = previous[current];
    }

    if (current == source) {
      path.add(source);
      // Reverse the path since we built it backwards
      return reversePath(path);
    }

    return null; // No path found
  }

  // Use List from package university.dijkstra.data_structures.List;
  // "This list doesn't have .get(), so instead use .getNext"
  public List<Integer> reversePath(List<Integer> path) {
    List<Integer> reversedPath = new List<>();
    List.Node<Integer> current = path.getTail();
    while (current != null) {
      reversedPath.add(current.getData());
      current = current.getPrevious();
    }
    return reversedPath;
  }

  public double getDistance(int vertexId) {
    return distances[vertexId];
  }

  public int getPrevious(int vertexId) {
    return previous[vertexId];
  }
}
