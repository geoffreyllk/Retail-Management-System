package com.example.coursework;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.ConnectDB;
import com.example.coursework.database.dbHelper;
import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    // javafx entry point
    @Override
    public void start(Stage primaryStage) {
        try {
            // create connection (to pass it to database DAO classes, so all use the same connection)
            ConnectDB connectDB = new ConnectDB();
            // get connection from connectDB helper class
            Connection connection = connectDB.getConnection();

            // --- INIT DB ----
            dbHelper db = new dbHelper(connection);
            db.initDatabase();

            // initialise cache (blueprints + session data)
            AppCache.initialize(connection);

            // load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/LoginScreen.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Login | Retail Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // call javafx fallback
    }
}