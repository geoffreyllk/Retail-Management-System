package com.example.coursework.controllers;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.dbHelper;
import com.example.coursework.models.User;
import com.example.coursework.utils.AlertUtil;
import com.example.coursework.utils.ConfigManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginController {
    private static final String ROLE_CASHIER = "cashier";
    private static final String ROLE_ADMIN = "admin";

    @FXML private Label storeNameLabel;
    @FXML private Label errorLabel;
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    @FXML private Button insertSampleDataBtn;

    @FXML
    public void initialize() {
        // get and set store name
        String storeName = ConfigManager.getStoreName();
        storeNameLabel.setText(storeName);

        // Check if database already has products
        boolean hasData = checkIfDatabaseHasData();

        // Hide button if data already exists
        insertSampleDataBtn.setVisible(!hasData);
        insertSampleDataBtn.setManaged(!hasData);

    }

    private boolean checkIfDatabaseHasData() {
        try {
            Connection conn = AppCache.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            int count = rs.getInt(1);
            rs.close();
            stmt.close();
            return count > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @FXML
    private void insertSampleData() {
        try {
            dbHelper db = new dbHelper(AppCache.getConnection());
            db.insertSampleData();

            AlertUtil.showSuccess("Sample data inserted successfully!");

            // Hide button after inserting
            insertSampleDataBtn.setVisible(false);
            insertSampleDataBtn.setManaged(false);
        } catch (Exception e) {
            AlertUtil.showError("Failed to insert sample data: " + e.getMessage());
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        User user = AppCache.getUserDB().getUser(username);

        if (user != null && user.getPassword().equals(password)) {
            String role = user.getRole();

            if (role == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error !!");
                alert.setHeaderText("User not found.");
                alert.showAndWait();
                usernameField.clear();
                passwordField.clear();
                usernameField.requestFocus();
                return;
            }

            AppCache.setCurrentUser(user);
            openDashboard(event, role);
        } else {
            showError("Invalid username or password");
            passwordField.clear();
        }
    }

    private void openDashboard(ActionEvent event, String role) {
        if (role.equals(ROLE_CASHIER) || role.equals(ROLE_ADMIN)) {
            try {
                FXMLLoader loader;
                if (role.equals(ROLE_CASHIER)) {
                    loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/cashier/CashierDashboard.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/admin/AdminLayout.fxml"));
                }

                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/example/coursework/styles/Root.css").toExternalForm());
                stage.setScene(scene);

                if (role.equals(ROLE_CASHIER)) {
                    stage.setTitle("Dashboard | Cashier Panel");
                } else {
                    stage.setTitle("Dashboard | Admin Panel");
                }
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                showError("Failed to open dashboard");
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error !!");
            alert.setHeaderText("User has invalid User Role. Please try again or contact IT support.");
            alert.showAndWait();
            usernameField.clear();
            passwordField.clear();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}