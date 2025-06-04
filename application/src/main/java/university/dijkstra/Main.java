package university.dijkstra;

import university.dijkstra.data_structures.List;
import university.dijkstra.io.DataProccessor;
import university.dijkstra.model.Edge;
import university.dijkstra.model.Vertex;

public class Main {
  public static void main(String[] args) {
    String filename = "/home/osamaps/Downloads/University/HomeWorks/Semester2_Year3/Algorithm/Third_Project/USA.txt";
    try {
      Vertex[] graph = DataProccessor.parseFile(filename);
      System.out.println("Graph loaded successfully with " + graph.length + " vertices.");

      // Print first few vertices as a sample
      System.out.println("\nSample vertices (first 5):");
      for (int i = 0; i < Math.min(5, graph.length); i++) {
        printVertex(graph[i]);
      }

    } catch (Exception e) {
      System.err.println("Error processing file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void printVertex(Vertex vertex) {
    System.out.println("\nVertex ID: " + vertex.getId() +
        ", Coordinates: (" + vertex.getX() + ", " + vertex.getY() + ")");

    List<Edge> edges = vertex.getEdges();
    if (edges.isEmpty()) {
      System.out.println("  No edges connected to this vertex.");
      return;
    }

    System.out.println("  Edges:");
    // Use a temporary reference to traverse without modifying the list
    List<Edge>.Node<Edge> current = edges.getHead();
    int edgeCount = 0;
    while (current != null && edgeCount < 10) { // Limit output for large graphs
      Edge edge = current.getData();
      System.out.printf("    → Vertex %d (weight: %.2f)\n",
          edge.getTarget().getId(), edge.getWeight());
      current = current.getNext();
      edgeCount++;
    }

    if (current != null) {
      System.out.println("    ... and more edges");
    }
  }

  public static void printGraph(Vertex[] graph) {
    System.out.println("\nFull Graph:");
    for (Vertex vertex : graph) {
      printVertex(vertex);
    }
  }
}