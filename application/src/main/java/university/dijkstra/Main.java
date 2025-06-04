package university.dijkstra;

import java.io.BufferedReader;
import java.io.FileReader;

import university.dijkstra.algorithm.Dijkstra;
import university.dijkstra.data_structures.List;
import university.dijkstra.io.DataProccessor;
import university.dijkstra.model.Edge;
import university.dijkstra.model.Vertex;

public class Main {

  // ================= Test DataProcessing =============================
  // public static void main(String[] args) {
  // String filename =
  // "/home/osamaps/Downloads/University/HomeWorks/Semester2_Year3/Algorithm/Third_Project/USA.txt";
  // try {
  // Vertex[] graph = DataProccessor.parseFile(filename);
  // System.out.println("Graph loaded successfully with " + graph.length +
  // "vertices.");

  // // Print first few vertices as a sample
  // System.out.println("\nSample vertices (first 5):");
  // for (int i = 0; i < Math.min(5, graph.length); i++) {
  // printVertex(graph[i]);
  // }

  // } catch (Exception e) {
  // System.err.println("Error processing file: " + e.getMessage());
  // e.printStackTrace();
  // }
  // }

  // public static void printVertex(Vertex vertex) {
  // System.out.println("\nVertex ID: " + vertex.getId() +
  // ", Coordinates: (" + vertex.getX() + ", " + vertex.getY() + ")");

  // List<Edge> edges = vertex.getEdges();
  // if (edges.isEmpty()) {
  // System.out.println(" No edges connected to this vertex.");
  // return;
  // }

  // System.out.println(" Edges:");
  // // Use a temporary reference to traverse without modifying the list
  // List.Node<Edge> current = edges.getHead();
  // int edgeCount = 0;
  // while (current != null && edgeCount < 10) { // Limit output for large graphs
  // Edge edge = current.getData();
  // System.out.printf(" → Vertex %d (weight: %.2f)\n",
  // edge.getDestination().getId(), edge.getWeight());
  // current = current.getNext();
  // edgeCount++;
  // }

  // if (current != null) {
  // System.out.println(" ... and more edges");
  // }
  // }

  // public static void printGraph(Vertex[] graph) {
  // System.out.println("\nFull Graph:");
  // for (Vertex vertex : graph) {
  // printVertex(vertex);
  // }
  // }

  // ================= Test Dijkstra =============================

  public static void testDijkstraWithQueries() {
    System.out.println("\n=== Dijkstra Algorithm Test with Query File ===");

    try {
      // First, load the USA graph
      String graphFile = "/home/osamaps/Downloads/University/HomeWorks/Semester2_Year3/Algorithm/Third_Project/USA.txt";
      System.out.println("Loading graph from USA.txt...");
      Vertex[] graph = DataProccessor.parseFile(graphFile);
      System.out.println("Graph loaded successfully with " + graph.length + " vertices.");

      // Now read the test queries from Test.txt
      String testFile = "/home/osamaps/Downloads/University/HomeWorks/Semester2_Year3/Algorithm/Third_Project/Test.txt";
      System.out.println("\nReading test queries from " + testFile + "...");

      BufferedReader reader = new BufferedReader(new FileReader(testFile));
      String line;
      int testCase = 1;

      // Create Dijkstra instance
      Dijkstra dijkstra = new Dijkstra(graph.length);

      // Track timing for performance analysis
      long totalTime = 0;
      List<Long> queryTimes = new List<>();

      while ((line = reader.readLine()) != null) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 2) {
          System.out.println("Skipping invalid line: " + line);
          continue;
        }

        int sourceId = Integer.parseInt(parts[0]);
        int destinationId = Integer.parseInt(parts[1]);

        System.out.println("\nTest Case " + testCase + ":");
        System.out.println("Finding shortest path from vertex " + sourceId +
            " to vertex " + destinationId);

        // Time the query
        long startTime = System.nanoTime();

        // Run Dijkstra's algorithm
        dijkstra.findShortestPath(graph, graph[sourceId], graph[destinationId]);

        long endTime = System.nanoTime();
        long queryTime = endTime - startTime;
        totalTime += queryTime;
        queryTimes.add(queryTime);

        // Get the results
        double distance = dijkstra.getDistance(destinationId);

        if (distance == Double.MAX_VALUE) {
          System.out.println("No path found!");
        } else {
          System.out.printf("Shortest distance: %.2f\n", distance);

          // Reconstruct and print the path
          List<Integer> path = dijkstra.reconstructPath(sourceId, destinationId);
          if (path != null) {
            System.out.print("Path: ");
            printPath(path);
          }
        }

        System.out.printf("Query time: %.3f ms\n", queryTime / 1_000_000.0);
        testCase++;
      }

      reader.close();

      // Print performance summary
      System.out.println("\n=== Performance Summary ===");
      System.out.printf("Total queries: %d\n", testCase - 1);
      System.out.printf("Total time: %.3f ms\n", totalTime / 1_000_000.0);
      System.out.printf("Average query time: %.3f ms\n",
          (totalTime / (testCase - 1)) / 1_000_000.0);

    } catch (Exception e) {
      System.err.println("Error in Dijkstra test: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // Helper method to print the path
  private static void printPath(List<Integer> path) {
    List.Node<Integer> current = path.getHead();
    boolean first = true;
    while (current != null) {
      if (!first) {
        System.out.print(" → ");
      }
      System.out.print(current.getData());
      first = false;
      current = current.getNext();
    }
    System.out.println();
  }

  // Add this to your main method
  public static void main(String[] args) {
    // Your existing code...

    // Add this line to run the Dijkstra test
    testDijkstraWithQueries();
  }
}