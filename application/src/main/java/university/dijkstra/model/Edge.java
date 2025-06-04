package university.dijkstra.model;

public class Edge {
  private Vertex destination;
  private double weight;

  public Edge(Vertex target, double weight) {
    this.destination = target;
    this.weight = weight;
  }

  public Vertex getDestination() {
    return destination;
  }

  public double getWeight() {
    return weight;
  }

}
