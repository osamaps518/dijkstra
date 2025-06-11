package university.dijkstra.algorithm;

import java.util.Arrays;

import university.dijkstra.data_structures.List;
import university.dijkstra.data_structures.MinHeap;
import university.dijkstra.model.*;

public class Dijkstra {
  private int[] previous; // To reconstruct the path
  private double[] distances; // To store the shortest distances from the source
  private List<Integer> visitedVertices;
  private boolean[] known;

  public Dijkstra(int numVertices) {
    distances = new double[numVertices];
    previous = new int[numVertices];
    visitedVertices = new List<>();
    known = new boolean[numVertices];

    initializeArrays();
  }

  public void initializeArrays() {
    for (int i = 0; i < distances.length; i++) {
      distances[i] = Double.MAX_VALUE;
      previous[i] = -1;
      known[i] = false;
    }
  }

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

    // Reset ONLY touched vertices from previous query
    resetVisitedVertices();

    distances[source.getId()] = 0;
    visitedVertices.add(source.getId());
    pq.insert(new QueueNode(source.getId(), 0));

    while (!pq.isEmpty()) {
      QueueNode current = pq.dequeue();
      // Skip if already visited
      if (known[current.vertexId]) {
        continue;
      }
      known[current.vertexId] = true;

      List<Edge> edges = graph[current.vertexId].getEdges();

      if (current.vertexId == destination.getId()) {
        break; // early termination
      }

      List.Node<Edge> node = edges.getHead();
      while (node != null) {
        Edge currentEdge = node.getData();

        if (!known[currentEdge.getDestination().getId()]) {
          double newDistance = distances[current.vertexId] + currentEdge.getWeight();
          if (newDistance < distances[currentEdge.getDestination().getId()]) {
            distances[currentEdge.getDestination().getId()] = newDistance;
            previous[currentEdge.getDestination().getId()] = current.vertexId;
            pq.insert(new QueueNode(currentEdge.getDestination().getId(), newDistance));
            visitedVertices.add(currentEdge.getDestination().getId());
          }
        }

        node = node.getNext();
      }

    }
  }

  private void resetVisitedVertices() {
    // Iterate without destroying the list
    List.Node<Integer> current = visitedVertices.getHead();
    while (current != null) {
      int vertexId = current.getData();
      distances[vertexId] = Double.MAX_VALUE;
      previous[vertexId] = -1;
      current = current.getNext();
      known[vertexId] = false;
    }
    // Now clear for next query
    visitedVertices.clear();
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
