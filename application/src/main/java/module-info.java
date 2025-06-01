module university.dijkstra {
    requires javafx.controls;
    requires javafx.fxml;

    opens university.dijkstra to javafx.fxml;
    exports university.dijkstra;
}
