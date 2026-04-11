package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminLayoutController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button inventoryBtn;

    @FXML
    private Button salesBtn;

    @FXML
    private Button userBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button logoutBtn;

    private static AdminLayoutController instance;
    public void initialize() {
        // single static instance
        instance = this;

        // Set welcome message
        if (AppCache.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + AppCache.getCurrentUser().getUsername() + "!");
        }

        // load svg icons
        setIcon(dashboardBtn, "M478.08-335.35q29.15-1.65 42.54-20.96l210.15-292.92-294.46 208.61q-20.08 13.39-22.35 41.54-2.27 28.16 16.35 46.77 18.61 18.62 47.77 16.96ZM480-760q55.15 0 100.42 13.42 45.27 13.43 90.2 41.04l-36.77 24.92q-35.31-19.3-73.12-29.34T480-720q-133 0-226.5 93.5T160-400q0 42 11.5 83t32.5 77h552q23-38 33.5-79t10.5-85q0-36-9.27-75.77-9.27-39.77-29.35-74.08l24.93-36.77q29.23 50.08 41.34 94.24 12.12 44.15 12.35 91.92.23 51.61-11.46 96.31-11.69 44.69-36.39 90.15-4.84 8-14.61 13T756-200H204q-11 0-20.38-5.38-9.39-5.39-15.77-15.7-19.85-35-33.85-78.96Q120-344 120-400q0-73.77 28.04-139.35 28.04-65.57 76.38-114.69 48.35-49.11 114.43-77.54Q404.92-760 480-760Zm1.62 278.38Z");
        setIcon(inventoryBtn, "M460-171.46v-297.08L200-619.08v283.23q0 6.16 3.08 11.54 3.07 5.39 9.23 9.23L460-171.46Zm40 0 247.69-143.62q6.16-3.84 9.23-9.23 3.08-5.38 3.08-11.54v-283.23L500-468.54v297.08Zm-52.31 38.92L192.31-279.69q-15.16-8.69-23.73-23.62-8.58-14.92-8.58-32.31v-288.76q0-17.39 8.58-32.31 8.57-14.93 23.73-23.62l255.38-147.15q15.16-8.69 32.31-8.69 17.15 0 32.31 8.69l255.38 147.15q15.16 8.69 23.73 23.62 8.58 14.92 8.58 32.31v288.76q0 17.39-8.58 32.31-8.57 14.93-23.73 23.62L512.31-132.54q-15.16 8.69-32.31 8.69-17.15 0-32.31-8.69ZM628.46-589 737-651.46 492.31-793.08q-6.16-3.84-12.31-3.84t-12.31 3.84l-95.69 55L628.46-589ZM480-502.92l108-62.7-257-148.53-108 62.69 257 148.54Z");
        setIcon(salesBtn, "M249.65-132.73q-17.34-17.35-17.34-42.65 0-25.31 17.34-42.66 17.35-17.34 42.66-17.34 25.31 0 42.65 17.34 17.35 17.35 17.35 42.66 0 25.3-17.35 42.65-17.34 17.35-42.65 17.35t-42.66-17.35Zm375.39 0q-17.35-17.35-17.35-42.65 0-25.31 17.35-42.66 17.34-17.34 42.65-17.34t42.66 17.34q17.34 17.35 17.34 42.66 0 25.3-17.34 42.65-17.35 17.35-42.66 17.35-25.31 0-42.65-17.35ZM235.23-740 342-515.38h265.38q6.93 0 12.31-3.47 5.39-3.46 9.23-9.61l104.62-190q4.61-8.46.77-15-3.85-6.54-13.08-6.54h-486Zm-19.54-40h520.77q26.08 0 39.23 21.27 13.16 21.27 1.39 43.81l-114.31 208.3q-8.69 14.62-22.58 22.93-13.88 8.31-30.5 8.31H324l-48.62 89.23q-6.15 9.23-.38 20 5.77 10.77 17.31 10.77h435.38v40H292.31q-35 0-52.23-29.5-17.23-29.5-.85-59.27l60.15-107.23L152.31-820H80v-40h97.69l38 80ZM342-515.38h280-280Z");
        setIcon(userBtn, "M103.85-215.38v-65.85q0-27.85 14.42-47.89 14.42-20.03 38.76-32.02 52.05-24.78 103.35-39.51 51.31-14.73 123.47-14.73 72.15 0 123.46 14.73 51.31 14.73 103.35 39.51 24.34 11.99 38.76 32.02 14.43 20.04 14.43 47.89v65.85h-560Zm640 0v-67.7q0-34.77-14.08-65.64-14.07-30.87-39.92-52.97 29.46 6 56.77 16.65 27.3 10.66 54 23.96 26 13.08 40.77 33.47 14.76 20.4 14.76 44.53v67.7h-112.3ZM298.92-539.69q-35.07-35.08-35.07-84.93 0-49.84 35.07-84.92 35.08-35.08 84.93-35.08 49.84 0 84.92 35.08t35.08 84.92q0 49.85-35.08 84.93-35.08 35.07-84.92 35.07-49.85 0-84.93-35.07Zm340.45 0q-35.25 35.07-84.75 35.07-2.54 0-6.47-.57-3.92-.58-6.46-1.27 20.33-24.9 31.24-55.24 10.92-30.34 10.92-63.01t-11.43-62.44q-11.42-29.77-30.73-55.62 3.23-1.15 6.46-1.5 3.23-.35 6.47-.35 49.5 0 84.75 35.08t35.25 84.92q0 49.85-35.25 84.93ZM143.85-255.38h480v-25.85q0-14.08-7.04-24.62-7.04-10.53-25.27-20.15-44.77-23.92-94.39-36.65-49.61-12.73-113.3-12.73-63.7 0-113.31 12.73-49.62 12.73-94.39 36.65-18.23 9.62-25.27 20.15-7.03 10.54-7.03 24.62v25.85Zm296.5-312.74q23.5-23.5 23.5-56.5t-23.5-56.5q-23.5-23.5-56.5-23.5t-56.5 23.5q-23.5 23.5-23.5 56.5t23.5 56.5q23.5 23.5 56.5 23.5t56.5-23.5Zm-56.5 312.74Zm0-369.24Z");
        setIcon(reportsBtn, "M224.62-160q-26.85 0-45.74-18.88Q160-197.77 160-224.62v-510.76q0-26.85 18.88-45.74Q197.77-800 224.62-800h188q-5.47-30.62 14.65-55.31Q447.38-880 480-880q33.38 0 53.5 24.69 20.12 24.69 13.88 55.31h188q26.85 0 45.74 18.88Q800-762.23 800-735.38v510.76q0 26.85-18.88 45.74Q762.23-160 735.38-160H224.62Zm0-40h510.76q9.24 0 16.93-7.69 7.69-7.69 7.69-16.93v-510.76q0-9.24-7.69-16.93-7.69-7.69-16.93-7.69H224.62q-9.24 0-16.93 7.69-7.69 7.69-7.69 16.93v510.76q0 9.24 7.69 16.93 7.69 7.69 16.93 7.69ZM300-309.23h240v-40H300v40ZM300-460h360v-40H300v40Zm0-150.77h360v-40H300v40Zm201.5-180.04q8.5-8.5 8.5-21.5t-8.5-21.5q-8.5-8.5-21.5-8.5t-21.5 8.5q-8.5 8.5-8.5 21.5t8.5 21.5q8.5 8.5 21.5 8.5t21.5-8.5ZM200-200v-560 560Z");
        setIcon(settingsBtn, "m405.38-120-14.46-115.69q-19.15-5.77-41.42-18.16-22.27-12.38-37.88-26.53L204.92-235l-74.61-130 92.23-69.54q-1.77-10.84-2.92-22.34-1.16-11.5-1.16-22.35 0-10.08 1.16-21.19 1.15-11.12 2.92-25.04L130.31-595l74.61-128.46 105.93 44.61q17.92-14.92 38.77-26.92 20.84-12 40.53-18.54L405.38-840h149.24l14.46 116.46q23 8.08 40.65 18.54 17.65 10.46 36.35 26.15l109-44.61L829.69-595l-95.31 71.85q3.31 12.38 3.7 22.73.38 10.34.38 20.42 0 9.31-.77 19.65-.77 10.35-3.54 25.04L827.92-365l-74.61 130-107.23-46.15q-18.7 15.69-37.62 26.92-18.92 11.23-39.38 17.77L554.62-120H405.38ZM440-160h78.23L533-268.31q30.23-8 54.42-21.96 24.2-13.96 49.27-38.27L736.46-286l39.77-68-87.54-65.77q5-17.08 6.62-31.42 1.61-14.35 1.61-28.81 0-15.23-1.61-28.81-1.62-13.57-6.62-29.88L777.77-606 738-674l-102.08 42.77q-18.15-19.92-47.73-37.35-29.57-17.42-55.96-23.11L520-800h-79.77l-12.46 107.54q-30.23 6.46-55.58 20.81-25.34 14.34-50.42 39.42L222-674l-39.77 68L269-541.23q-5 13.46-7 29.23t-2 32.77q0 15.23 2 30.23t6.23 29.23l-86 65.77L222-286l99-42q23.54 23.77 48.88 38.12 25.35 14.34 57.12 22.34L440-160Zm38.92-220q41.85 0 70.93-29.08 29.07-29.07 29.07-70.92t-29.07-70.92Q520.77-580 478.92-580q-42.07 0-71.04 29.08-28.96 29.07-28.96 70.92t28.96 70.92Q436.85-380 478.92-380ZM480-480Z");
        setIcon(logoutBtn, "M224.62-160q-27.62 0-46.12-18.5Q160-197 160-224.62v-510.76q0-27.62 18.5-46.12Q197-800 224.62-800h256.15v40H224.62q-9.24 0-16.93 7.69-7.69 7.69-7.69 16.93v510.76q0 9.24 7.69 16.93 7.69 7.69 16.93 7.69h256.15v40H224.62Zm433.84-178.46-28.08-28.77L723.15-460H367.69v-40h355.46l-92.77-92.77 28.08-28.77L800-480 658.46-338.46Z");
        // Load default view when dashboard opens
        loadView("admin/AdminDashboard.fxml");
        setActiveButton(dashboardBtn); // default active is dashboard page
    }

    private void setIcon(Button button, String pathData) {
        SVGPath svg = new SVGPath();
        svg.setContent(pathData);
        svg.getStyleClass().add("icon"); // add css styling

        svg.setScaleX(0.02);
        svg.setScaleY(0.02);

        // wrap in stackpane to control size
        StackPane wrapper = new StackPane(svg);
        wrapper.setPrefSize(20, 20);
        wrapper.setMinSize(20, 20);
        wrapper.setMaxSize(20, 20);

        button.setGraphic(wrapper);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(10);
    }

    @FXML
    private BorderPane bp;

    @FXML
    private AnchorPane ap;  // content area

    // load different fxml files in content area on click
    @FXML
    void dashboard(MouseEvent event) {
        goToDashboard();
    }

    @FXML
    void inventory_management(MouseEvent event) {
        goToInventory();
    }

    @FXML
    void sale_management(MouseEvent event) {
        goToSales();
    }

    @FXML
    void user_management(MouseEvent event) {
        goToUsers();
    }

    @FXML
    void reports(MouseEvent event) {
        goToReports();
    }

    @FXML
    void settings(MouseEvent event) {
        goToSettings();
    }

    // public methods so other controllers can access for links
    public static void goToDashboard() {
        if (instance.loadView("admin/AdminDashboard.fxml")) {
            instance.setActiveButton(instance.dashboardBtn);
            instance.getStage().setTitle("Dashboard | Admin Panel");
        }
    }

    public static void goToInventory() {
        if (instance.loadView("admin/Inventory.fxml")) {
            instance.setActiveButton(instance.inventoryBtn);
            instance.getStage().setTitle("Inventory Management | Admin Panel");
        }
    }

    public static  void goToSales() {
        if (instance.loadView("admin/Sales.fxml")) {
            instance.setActiveButton(instance.salesBtn);
            instance.getStage().setTitle("Sales Management | Admin Panel");
        }
    }

    public static  void goToUsers() {
        if (instance.loadView("admin/Users.fxml")) {
            instance.setActiveButton(instance.userBtn);
            instance.getStage().setTitle("User Management | Admin Panel");
        }
    }

    public static  void goToReports() {
        if (instance.loadView("admin/Reports.fxml")) {
            instance.setActiveButton(instance.reportsBtn);
            instance.getStage().setTitle("Report | Admin Panel");
        }
    }

    public static  void goToSettings() {
        if (instance.loadView("admin/Settings.fxml")) {
            instance.setActiveButton(instance.settingsBtn);
            instance.getStage().setTitle("Settings | Admin Panel");
        }
    }

    private Stage getStage() {
        return (Stage) bp.getScene().getWindow();
    }

    private boolean loadView(String fxmlPath) {
        try {
            // load fxml dynamically into anchor pane (ap)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/" + fxmlPath));
            Node content = loader.load();

            ap.getChildren().clear(); // clear content
            ap.getChildren().add(content); // add new content

            // centering
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Could not load view: " + fxmlPath);
            return false; // if error dont change title and active button
        }
    }

    @FXML
    void logout(MouseEvent event) {
        // clear user info from cache
        AppCache.clearCurrentUser();

        if (AlertUtil.showConfirmation("Logout","Are you sure you want to logout?", "You will be redirected to the login screen.")) {
            try {
                // Load login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/LoginScreen.fxml"));
                Parent root = loader.load();

                // Get current stage
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Login | Retail Management System");
                stage.setMinWidth(400);
                stage.setMinHeight(300);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                AlertUtil.showError("Failed to load login screen");
            }
        }
    }

    private void setActiveButton(Button activeButton) {
        // remove all active classes
        dashboardBtn.getStyleClass().remove("active");
        inventoryBtn.getStyleClass().remove("active");
        salesBtn.getStyleClass().remove("active");
        userBtn.getStyleClass().remove("active");
        reportsBtn.getStyleClass().remove("active");
        settingsBtn.getStyleClass().remove("active");

        // add active class to button
        activeButton.getStyleClass().add("active");
    }
}