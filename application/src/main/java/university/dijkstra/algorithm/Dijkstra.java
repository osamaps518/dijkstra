package university.dijkstra.algorithm;

import university.dijkstra.data_structures.List;
import university.dijkstra.data_structures.MinHeap;
import university.dijkstra.model.*;

public class Dijkstra {
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

  public void dijkstra(Vertex[] graph, Vertex source, Vertex destination) {
    MinHeap<QueueNode> pq = new MinHeap<QueueNode>();
    double[] distances = new double[graph.length];
    int[] previous = new int[graph.length];

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

        if (currentEdge.getWeight() < distances[currentEdge.getDestination().getId()]) {
          distances[currentEdge.getDestination().getId()] = newDistance;
          previous[currentEdge.getDestination().getId()] = current.vertexId;
          pq.insert(new QueueNode(currentEdge.getDestination().getId(), newDistance));
        }
        node = node.getNext();
      }

    }
  }

  public void initializeDistancesAndPrevious(double[] distances, int[] previous) {
    for (int i = 1; i < distances.length; i++) {
      distances[i] = Double.MAX_VALUE; // Initialize all distances to infinity
      previous[i] = -1;
    }
  }
}
