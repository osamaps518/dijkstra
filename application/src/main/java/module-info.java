module university.dijkstra {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens university.dijkstra to javafx.fxml;

    exports university.dijkstra;
}
