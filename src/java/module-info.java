module KnnApp {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.json;
    requires jackson.databind;
    requires jackson.dataformat.csv;
    opens com.recsys.controller to javafx.fxml;
    opens com.recsys.model to javafx.base;
    opens com.recsys;
}