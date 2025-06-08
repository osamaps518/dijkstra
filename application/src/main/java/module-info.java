module university.dijkstra {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens university.dijkstra to javafx.fxml;

    exports university.dijkstra;
}
