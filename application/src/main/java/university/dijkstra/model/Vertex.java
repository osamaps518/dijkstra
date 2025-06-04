package university.dijkstra.model;

import university.dijkstra.data_structures.List;

public class Vertex {
  int id;
  int x;
  int y;
  List<Edge> edges;

  // function to add an edge to the vertex
  public void addEdge(Vertex target, double weight) {
    edges.add(new Edge(target, weight));
  }

  public Vertex(int vertix_id, int x, int y) {
    this.id = vertix_id;
    this.x = x;
    this.y = y;
    this.edges = new List<Edge>();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public List<Edge> getEdges() {
    return edges;
  }

}
