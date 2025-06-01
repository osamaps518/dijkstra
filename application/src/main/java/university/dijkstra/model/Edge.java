package university.dijkstra.model;

public class Edge {
  private Vertix target;
  private double weight;

  public Edge(Vertix target, double weight) {
    this.target = target;
    this.weight = weight;
  }

  public Vertix getTarget() {
    return target;
  }

  public double getWeight() {
    return weight;
  }

}
