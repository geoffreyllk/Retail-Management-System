package com.example.coursework.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private Connection connection;

    // constructor of connectDB, whenever connectDB is called, a connection is made
    public ConnectDB() {
        // url of sqlite db
        String url = "jdbc:sqlite:retailstore.db";
        // try connection
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection Succesful");
        } catch (SQLException e) {
            System.out.println("Error Connecting to Database");
            e.printStackTrace();
        }
    }

    // returns connection
    public Connection getConnection() {
        return connection;
    }

    // close connection
    public void closeConnection() {
        // check if it can close connection (not null)
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection Closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection");
            e.printStackTrace();
        }
    }
}
