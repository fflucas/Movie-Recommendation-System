package com.recsys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class StartApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/BuscaFilmes.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        Image appIcon = new Image( "/claquete.png");
        primaryStage.getIcons().add(appIcon);
        primaryStage.setTitle("Sistema de recomendação de filmes");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}