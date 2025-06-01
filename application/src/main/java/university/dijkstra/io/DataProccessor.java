package university.dijkstra.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import university.dijkstra.model.Vertix;

public class DataProccessor {
  public static Vertix[] parseFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      int[] graphInfo = parseGraphInfo(reader);
      int numVertices = graphInfo[0];
      int numEdges = graphInfo[1];
      Vertix[] graph = new Vertix[numVertices];
      readVertices(graph, reader);
      connectVertices(graph, reader, numEdges);
      // At this point, the graph is fully constructed with vertices and edges
      return graph;
    }
  }

  private static int[] parseGraphInfo(BufferedReader reader) throws IOException {
    String firstLine = reader.readLine();
    if (firstLine == null) {
      throw new IOException("The file is empty");
    }

    try {
      String[] parts = firstLine.trim().split("\\s+"); // Split by any whitespace
      if (parts.length != 2) {
        throw new IOException("First line must contain exactly 2 numbers");
      }

      int numVertices = Integer.parseInt(parts[0]);
      int numEdges = Integer.parseInt(parts[1]);

      if (numVertices <= 0 || numEdges < 0) {
        throw new IOException("Invalid numbers: vertices must be positive, edges non-negative");
      }

      return new int[] { numVertices, numEdges };
    } catch (NumberFormatException e) {
      throw new IOException("Invalid format for graph dimensions: " + firstLine);
    }
  }

  private static void readVertices(Vertix[] graph, BufferedReader reader) throws IOException {
    for (int i = 0; i < graph.length; i++) {
      String line = reader.readLine();
      if (line == null) {
        throw new IOException("Unexpected end of file while reading vertices");
      }
      String[] parts = line.trim().split("\\s+");
      if (parts.length != 3) {
        throw new IOException("Expected Format: vertix_id x y");
      }

      try {
        int vertix_id = Integer.parseInt(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);

        graph[vertix_id] = new Vertix(vertix_id, x, y);

      } catch (NumberFormatException e) {
        throw new IOException("Invalid number format in vertix: " + line);
      }
    }
  }

  private static void connectVertices(Vertix[] vertices, BufferedReader reader, int numEdges) throws IOException {
    // empty line
    String line = reader.readLine();

    for (int i = 0; i < numEdges; i++) {
      // File format is: vertix_id1 vertix_id2 only. Weight can be calcualted by
      // euclidean distance
      try {
        if (line == null) {
          throw new IOException("Unexpected end of file while reading edges");
        }
        line = reader.readLine();
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 2) {
          throw new IOException("Expected Format: vertix_id1 vertix_id2");
        }

        int vertixId1 = Integer.parseInt(parts[0]);
        int vertixId2 = Integer.parseInt(parts[1]);

        if (vertixId1 < 0 || vertixId1 >= vertices.length || vertixId2 < 0 || vertixId2 >= vertices.length) {
          throw new IOException("Invalid vertex ID in edge: " + line);
        }

        Vertix v1 = vertices[vertixId1];
        Vertix v2 = vertices[vertixId2];

        double weight = calculateEuclideanDistance(v1, v2);
        if (weight < 0) {
          throw new IOException("Negative weight calculated for edge: " + line);
        }
        if (v1 == null || v2 == null) {
          throw new IOException("One of the vertices is null for edge: " + line);
        }
        if (v1.getId() == v2.getId()) {
          throw new IOException("Self-loop detected for vertex ID: " + v1.getId());
        }
        // Assuming undirected graph for Dijkstra's algorithm
        v1.addEdge(v2, weight);
        v2.addEdge(v1, weight);

      } catch (NumberFormatException e) {
        throw new IOException("Invalid number format in edge: " + e.getMessage());
      } catch (IOException e) {
        throw new IOException("Error reading edge: " + e.getMessage());
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new IOException("Vertex ID out of bounds in edge: " + e.getMessage());
      } catch (Exception e) {
        throw new IOException("Unexpected error while processing edge: " + e.getMessage());
      }
    }
  }

  private static double calculateEuclideanDistance(Vertix v1, Vertix v2) {
    return Math.sqrt(Math.pow(v1.getX() - v2.getX(), 2) + Math.pow(v1.getY() - v2.getY(), 2));
  }

}
