package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    Controller controller;


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent)loader.load();

        primaryStage.getIcons().add(new Image("server_icon.png"));
        primaryStage.setTitle("SQL : JAVA GUI");
        primaryStage.setScene(new Scene(root, 1200, 470));
        primaryStage.show();
        primaryStage.setResizable(false);

        controller = (Controller)loader.getController();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
