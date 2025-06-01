package university.dijkstra.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import university.dijkstra.data_structures.List;

public class DataProccessor {
  public static void parseFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      int[] graphInfo = parseGraphInfo(reader);
      int numVertices = graphInfo[0];
      int numEdges = graphInfo[1];
      List[] graph = new List[numVertices];
    }
  }

  // This function was AI generated, check it out
  private void fillGraph(List[] graph, BufferedReader reader, int numEdges) throws IOException {
    for (int i = 0; i < numEdges; i++) {
      String line = reader.readLine();
      if (line == null) {
        throw new IOException("Unexpected end of file while reading edges");
      }
      String[] parts = line.trim().split("\\s+");
      if (parts.length != 3) {
        throw new IOException("Each edge must have exactly 3 numbers: source, destination, weight");
      }

      try {
        int source = Integer.parseInt(parts[0]);
        int destination = Integer.parseInt(parts[1]);
        int weight = Integer.parseInt(parts[2]);

        if (source < 0 || source >= graph.length || destination < 0 || destination >= graph.length) {
          throw new IOException("Vertex index out of bounds: " + source + " or " + destination);
        }

        if (graph[source] == null) {
          graph[source] = new List<>();
        }
        graph[source].add(new Edge(destination, weight));
      } catch (NumberFormatException e) {
        throw new IOException("Invalid number format in edge: " + line);
      }
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
}
