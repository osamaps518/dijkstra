package university.dijkstra;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import university.dijkstra.io.DataProccessor;
import university.dijkstra.model.Vertex;
import university.dijkstra.model.Edge;
import university.dijkstra.data_structures.List;
import university.dijkstra.algorithm.Dijkstra;

public class DijkstraVisualization extends Application {
  private static final int INITIAL_WIDTH = 1200;
  private static final int INITIAL_HEIGHT = 800;
  private static final double MIN_ZOOM = 0.1;
  private static final double MAX_ZOOM = 10.0;
  private static final double EDGE_VISIBILITY_ZOOM = 3.0;
  private static final double VERTEX_LABEL_ZOOM = 6.0;

  private Vertex[] graph;
  private double minX, maxX, minY, maxY;
  private double currentZoom = 1.0;
  private Canvas canvas;
  private ScrollPane scrollPane;
  private HBox infoPanel; // Reference to the info panel
  Button showDetailsButton;
  private TextField sourceSearchField;
  private ListView<String> sourceListView;
  private FilteredList<String> filteredSourceList;
  private TextField destSearchField;
  private ListView<String> destListView;
  private FilteredList<String> filteredDestList;
  private ObservableList<String> allVerticesList;

  // For vertex selection and pathfinding
  private Vertex selectedSource = null;
  private Vertex selectedDestination = null;
  private List<Integer> currentPath = null;
  private Dijkstra dijkstra;

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Dijkstra's Algorithm Visualization");

    // File chooser
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Graph Data File");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Text Files", "*.txt"));

    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile == null) {
      System.exit(0);
      return;
    }

    // Load the graph
    try {
      long startTime = System.currentTimeMillis();
      graph = DataProccessor.parseFile(selectedFile.getAbsolutePath());
      long loadTime = System.currentTimeMillis() - startTime;
      // Initialize Dijkstra
      dijkstra = new Dijkstra(graph.length);

      calculateBounds();

      // Create UI
      BorderPane root = new BorderPane();

      // Create controls
      HBox controls = createControls();
      root.setTop(controls);

      infoPanel = createInfoPanel();
      root.setBottom(infoPanel);
      root.setBottom(infoPanel);

      // Create scrollable canvas
      canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
      scrollPane = new ScrollPane(canvas);
      scrollPane.setPannable(true);
      root.setCenter(scrollPane);

      // Add mouse interaction
      setupMouseInteraction();

      // Initial draw
      drawGraph();

      // Add mouse wheel zoom
      scrollPane.setOnScroll(event -> {
        if (event.isControlDown()) {
          double zoomFactor = 1.05;
          double deltaY = event.getDeltaY();

          if (deltaY < 0) {
            currentZoom /= zoomFactor;
          } else {
            currentZoom *= zoomFactor;
          }

          currentZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, currentZoom));
          updateCanvasSize();
          drawGraph();

          event.consume();
        }
      });

      Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
      primaryStage.setScene(scene);
      primaryStage.show();

      // Count edges
      int edgeCount = 0;
      for (Vertex v : graph) {
        if (v != null) {
          edgeCount += v.getEdges().size();
        }
      }
      edgeCount /= 2; // Undirected graph

      System.out.println("=== Map Statistics ===");
      System.out.println("Vertices: " + graph.length);
      System.out.println("Edges: " + edgeCount);
      System.out.println("Load time: " + loadTime + " ms");
      System.out.println("\n=== Controls ===");
      System.out.println("Zoom: Ctrl+Mouse Wheel or use slider");
      System.out.println("Pan: Drag with mouse");
      System.out.println("Select vertices: Click on map");
      System.out.println("First click: Source (green)");
      System.out.println("Second click: Destination (red) - path will be calculated");

    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error loading file: " + e.getMessage());
      System.exit(1);
    }
  }

  private HBox createControls() {
    HBox controls = new HBox(10);
    controls.setPadding(new Insets(10));
    controls.setStyle("-fx-background-color: #f0f0f0;");

    Label zoomLabel = new Label("Zoom: ");
    Slider zoomSlider = new Slider(MIN_ZOOM, MAX_ZOOM, 1.0);
    zoomSlider.setShowTickLabels(true);
    zoomSlider.setShowTickMarks(true);
    zoomSlider.setMajorTickUnit(1.0);
    zoomSlider.setPrefWidth(300);

    Label zoomValue = new Label(String.format("%.1fx", currentZoom));
    Label edgeInfo = new Label(" (Edges hidden)");
    edgeInfo.setTextFill(Color.GRAY);
    controls.getChildren().add(new Label("  |  "));

    zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      currentZoom = newVal.doubleValue();
      zoomValue.setText(String.format("%.1fx", currentZoom));
      updateCanvasSize();
      drawGraph();

      if (currentZoom >= EDGE_VISIBILITY_ZOOM) {
        edgeInfo.setText(" (Edges visible)");
        edgeInfo.setTextFill(Color.GREEN);
      } else {
        edgeInfo.setText(" (Edges hidden - zoom to " + EDGE_VISIBILITY_ZOOM + "x)");
        edgeInfo.setTextFill(Color.GRAY);
      }
    });

    // Create searchable dropdowns instead of ComboBoxes
    VBox sourceBox = createSearchableDropdown("Source:", true);
    VBox destBox = createSearchableDropdown("Destination:", false);

    Button calculateButton = new Button("Calculate Path");
    calculateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    calculateButton.setOnAction(e -> handleSearchFieldSelection());

    controls.getChildren().addAll(
        zoomLabel, zoomSlider, zoomValue, edgeInfo,
        sourceBox, destBox,
        calculateButton);
    return controls;
  }

  private void handleSearchFieldSelection() {
    String sourceSelection = sourceSearchField.getText();
    String destSelection = destSearchField.getText();

    if (sourceSelection.isEmpty() || destSelection.isEmpty() ||
        !sourceSelection.startsWith("Vertex") || !destSelection.startsWith("Vertex")) {
      // Show error or do nothing
      System.out.println("Please select valid vertices from the search results");
      return;
    }

    // Extract vertex ID from selection string
    int sourceId = extractVertexId(sourceSelection);
    int destId = extractVertexId(destSelection);

    selectedSource = graph[sourceId];
    selectedDestination = graph[destId];

    calculatePath();
    updateInfoPanel();
    drawGraph();
  }

  private VBox createSearchableDropdown(String label, boolean isSource) {
    VBox container = new VBox(2);
    container.setPrefWidth(200);

    TextField searchField = new TextField();
    searchField.setPromptText("Type to search...");
    searchField.setPrefWidth(180);

    ListView<String> listView = new ListView<>();
    listView.setPrefHeight(150);
    listView.setPrefWidth(180);
    listView.setVisible(false); // starts hidden until the user types
    listView.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");

    // Create filtered list using the shared vertex list
    // This happens during UI creation, not while the user types
    if (allVerticesList == null) {
      allVerticesList = FXCollections.observableArrayList();
      for (Vertex v : graph) {
        if (v != null) {
          allVerticesList.add("Vertex " + v.getId() + " (" + v.getX() + ", " + v.getY() + ")");
        }
      }
    }

    // The listview shows this filtered list, not all items
    FilteredList<String> filteredList = new FilteredList<>(allVerticesList);
    listView.setItems(filteredList);

    // Store references for later use
    // check whether to update source or destination
    if (isSource) {
      sourceSearchField = searchField;
      sourceListView = listView;
      filteredSourceList = filteredList;
    } else {
      destSearchField = searchField;
      destListView = listView;
      filteredDestList = filteredList;
    }

    // Filter as user types
    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.isEmpty()) {
        listView.setVisible(false);
      } else {
        filteredList.setPredicate(item -> item.toLowerCase().contains(newVal.toLowerCase()));
        listView.setVisible(true);
        // Limit height based on results
        int itemCount = Math.min(filteredList.size(), 8);
        listView.setPrefHeight(itemCount * 24 + 2);
      }
    });

    // Handle selection
    // if an item is selected, fill the field and hide the menu
    listView.setOnMouseClicked(event -> {
      String selected = listView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        searchField.setText(selected);
        listView.setVisible(false);
      }
    });

    // Hide list when focus is lost
    // Hides dropdown when clicking elsewhere
    searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        // Small delay to allow click on list
        javafx.application.Platform.runLater(() -> {
          if (!listView.isFocused()) {
            listView.setVisible(false);
          }
        });
      }
    });

    container.getChildren().addAll(new Label(label), searchField, listView);
    return container;
  }

  private int extractVertexId(String selection) {
    // Extract ID from "Vertex 12345 (x, y)" format
    String[] parts = selection.split(" ");
    return Integer.parseInt(parts[1]);
  }

  private HBox createInfoPanel() {
    HBox info = new HBox(20);
    info.setPadding(new Insets(10));
    info.setStyle("-fx-background-color: #f8f8f8;");

    Label sourceLabel = new Label("Source: None");
    sourceLabel.setTextFill(Color.DARKGREEN);

    Label destLabel = new Label("Destination: None");
    destLabel.setTextFill(Color.DARKRED);

    Label pathLabel = new Label("Path: Not calculated");

    Label distanceLabel = new Label("Distance: N/A");
    showDetailsButton = new Button("Show Path Details");
    showDetailsButton.setVisible(false);
    showDetailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

    showDetailsButton.setOnAction(e -> showPathDetailsScene());

    // Store labels for updates
    info.setUserData(new Label[] { sourceLabel, destLabel, pathLabel, distanceLabel });

    info.getChildren().addAll(sourceLabel, destLabel, pathLabel, distanceLabel, showDetailsButton);
    return info;
  }

  private void updateInfoPanel() {
    if (infoPanel != null && infoPanel.getUserData() instanceof Label[]) {
      Label[] labels = (Label[]) infoPanel.getUserData();
      labels[0].setText("Source: " + (selectedSource != null ? "Vertex " + selectedSource.getId() : "None"));
      labels[1]
          .setText("Destination: " + (selectedDestination != null ? "Vertex " + selectedDestination.getId() : "None"));

      if (currentPath != null && currentPath.size() > 0) {
        labels[2].setText("Path: Found (" + currentPath.size() + " vertices)");

        // Calculate and display distance
        if (selectedDestination != null) {
          double distance = dijkstra.getDistance(selectedDestination.getId());
          labels[3].setText(String.format("Distance: %.2f", distance));
        }
      } else if (currentPath != null && currentPath.size() == 0) {
        labels[2].setText("Path: No path exists!");
        labels[3].setText("Distance: ∞");
      } else {
        labels[2].setText("Path: Not calculated");
        labels[3].setText("Distance: N/A");
      }
    }
    showDetailsButton.setVisible(selectedSource != null && selectedDestination != null && currentPath != null);

  }

  private void showPathDetailsScene() {
    Stage detailsStage = new Stage();
    detailsStage.setTitle("Path Details");

    VBox detailsBox = new VBox(10);
    detailsBox.setPadding(new Insets(20));

    Text title = new Text("Path from Vertex " + selectedSource.getId() +
        " to Vertex " + selectedDestination.getId());
    title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

    TextArea pathDetails = new TextArea();
    pathDetails.setEditable(false);
    pathDetails.setPrefRowCount(20);
    pathDetails.setFont(Font.font("Arial", 20));

    StringBuilder details = new StringBuilder();
    details.append("Total Distance: ").append(String.format("%.2f", dijkstra.getDistance(selectedDestination.getId())))
        .append("\n\n");
    details.append("Path vertices: ").append(currentPath.size()).append("\n\n");
    details.append("Detailed Route:\n");

    // Show each step with distances from edges
    List.Node<Integer> node = currentPath.getHead();
    int step = 1;
    Integer prevId = null;

    while (node != null) {
      Integer currentId = node.getData();
      Vertex current = graph[currentId];

      details.append(String.format("%d. Vertex %d (x=%d, y=%d)",
          step++, current.getId(), current.getX(), current.getY()));

      if (prevId != null) {
        // Find the edge weight between prev and current
        Vertex prev = graph[prevId];
        double edgeWeight = findEdgeWeight(prev, current);
        if (edgeWeight > 0) {
          details.append(String.format(" - Distance from previous: %.2f", edgeWeight));
        }
      }
      details.append("\n");

      prevId = currentId;
      node = node.getNext();
    }

    pathDetails.setText(details.toString());

    Button closeButton = new Button("Close");
    closeButton.setOnAction(ev -> detailsStage.close());
    closeButton.setPrefWidth(100);
    closeButton.setStyle("-fx-font-size: 14px;");

    detailsBox.getChildren().addAll(title, pathDetails, closeButton);

    Scene scene = new Scene(detailsBox, 800, 600);
    detailsStage.setScene(scene);
    detailsStage.show();
  }

  // Helper method to find edge weight between two vertices
  private double findEdgeWeight(Vertex from, Vertex to) {
    List<Edge> edges = from.getEdges();
    List.Node<Edge> node = edges.getHead();

    while (node != null) {
      Edge edge = node.getData();
      if (edge.getDestination().getId() == to.getId()) {
        return edge.getWeight();
      }
      node = node.getNext();
    }
    return -1; // Edge not found (shouldn't happen in a valid path)
  }

  private void setupMouseInteraction() {
    canvas.setOnMouseClicked(event -> {
      double canvasX = event.getX();
      double canvasY = event.getY();

      // Convert canvas coordinates to data coordinates
      double dataX = minX + (canvasX / canvas.getWidth()) * (maxX - minX);
      double dataY = maxY - (canvasY / canvas.getHeight()) * (maxY - minY);

      // Find nearest vertex
      Vertex nearest = findNearestVertex(dataX, dataY);

      if (nearest != null) {
        if (selectedSource == null) {
          selectedSource = nearest;
          selectedDestination = null;
          currentPath = null;
        } else if (selectedDestination == null) {
          selectedDestination = nearest;
          calculatePath();
        } else {
          // Reset and start new selection
          selectedSource = nearest;
          selectedDestination = null;
          currentPath = null;
        }

        updateInfoPanel();
        drawGraph();
      }
    });
  }

  private Vertex findNearestVertex(double x, double y) {
    Vertex nearest = null;
    double minDistance = Double.MAX_VALUE;

    // Adjust search radius based on zoom
    // If the user clicked within 50 pixels of a vertex, select it
    double searchRadius = 50 / currentZoom;

    for (Vertex v : graph) {
      if (v != null &&
          Math.abs(v.getX() - x) < searchRadius &&
          Math.abs(v.getY() - y) < searchRadius) {

        double dist = Math.sqrt(Math.pow(v.getX() - x, 2) + Math.pow(v.getY() - y, 2));
        if (dist < minDistance) {
          minDistance = dist;
          nearest = v;
        }
      }
    }

    return (minDistance < searchRadius) ? nearest : null;
  }

  private void calculatePath() {
    if (selectedSource == null || selectedDestination == null)
      return;

    // Run Dijkstra's algorithm
    dijkstra.findShortestPath(graph, selectedSource, selectedDestination);
    // Reconstruct the path
    currentPath = dijkstra.reconstructPath(selectedSource.getId(), selectedDestination.getId());
  }

  private void calculateBounds() {
    if (graph.length == 0)
      return;

    minX = maxX = graph[0].getX();
    minY = maxY = graph[0].getY();

    for (Vertex v : graph) {
      if (v != null) {
        minX = Math.min(minX, v.getX());
        maxX = Math.max(maxX, v.getX());
        minY = Math.min(minY, v.getY());
        maxY = Math.max(maxY, v.getY());
      }
    }

    // Add padding
    double paddingX = (maxX - minX) * 0.05;
    double paddingY = (maxY - minY) * 0.05;
    minX -= paddingX;
    maxX += paddingX;
    minY -= paddingY;
    maxY += paddingY;
  }

  private void updateCanvasSize() {
    canvas.setWidth(INITIAL_WIDTH * currentZoom);
    canvas.setHeight(INITIAL_HEIGHT * currentZoom);
  }

  private void drawGraph() {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    // Clear canvas
    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    // Draw the shortest path (before vertices so vertices appear on top)
    if (currentPath != null && currentPath.size() > 1) {
      drawPath(gc);
    }

    // Draw vertices
    drawVisibleVertices(gc);
  }

  private void drawVisibleVertices(GraphicsContext gc) {
    // Adjust point size based on zoom
    // double pointSize = Math.max(2, Math.min(8, 3 * currentZoom));
    double pointSize = Math.max(3, Math.min(10, 4 * currentZoom));

    // Check if vertex is on the path for highlighting
    Set<Integer> pathVertices = new HashSet<>();
    if (currentPath != null) {
      List.Node<Integer> node = currentPath.getHead();
      while (node != null) {
        pathVertices.add(node.getData());
        node = node.getNext();
      }
    }

    for (Vertex v : graph) {
      if (v != null) {
        double x = mapX(v.getX());
        double y = mapY(v.getY());

        // Choose color based on selection and path
        if (v == selectedSource) {
          gc.setFill(Color.LIGHTGREEN);
          gc.fillOval(x - pointSize, y - pointSize, pointSize * 2, pointSize * 2);
          gc.setFill(Color.DARKGREEN);
          gc.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        } else if (v == selectedDestination) {
          gc.setFill(Color.PINK);
          gc.fillOval(x - pointSize, y - pointSize, pointSize * 2, pointSize * 2);
          gc.setFill(Color.DARKRED);
          gc.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        } else if (pathVertices.contains(v.getId())) {
          // Highlight vertices on the path
          gc.setFill(Color.ORANGE);
          gc.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        } else {
          gc.setFill(Color.DARKBLUE);
          gc.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        }

        // Show vertex IDs if zoomed in
        if (currentZoom >= VERTEX_LABEL_ZOOM) {
          gc.setFill(Color.BLACK);
          gc.fillText(String.valueOf(v.getId()), x + pointSize, y - pointSize);
        }
      }
    }
  }

  private void drawPath(GraphicsContext gc) {
    if (currentPath == null || currentPath.size() < 2)
      return;

    gc.setStroke(Color.RED);
    gc.setLineWidth(3);

    // Draw the path by connecting consecutive vertices
    List.Node<Integer> node = currentPath.getHead();
    Vertex prev = null;

    while (node != null) {
      Vertex current = graph[node.getData()];

      if (prev != null && current != null) {
        double x1 = mapX(prev.getX());
        double y1 = mapY(prev.getY());
        double x2 = mapX(current.getX());
        double y2 = mapY(current.getY());

        gc.strokeLine(x1, y1, x2, y2);
      }

      prev = current;
      node = node.getNext();
    }
  }

  private double mapX(double x) {
    return (x - minX) / (maxX - minX) * canvas.getWidth();
  }

  private double mapY(double y) {
    return canvas.getHeight() - ((y - minY) / (maxY - minY) * canvas.getHeight());
  }

  public static void main(String[] args) {
    launch(args);
  }
}