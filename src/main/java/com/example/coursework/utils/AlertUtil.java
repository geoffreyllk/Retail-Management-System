package com.example.coursework.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Optional;

public class AlertUtil {

    private static final String ALERT_CSS = "/com/example/coursework/styles/Alert.css";

    // info
    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", null, message);
    }

    // error
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", null, message);
    }

    // Success
    public static void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", null, message);
    }

    // Confirmation
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        setupAlert(alert, title, header, content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // select combo
    public static Optional<String> showPaymentDialog(List<String> paymentMethods) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(paymentMethods.get(0), paymentMethods);
        dialog.setTitle("Payment");
        dialog.setHeaderText("Select Payment Method");
        dialog.setContentText("Payment Method:");
        dialog.getDialogPane().getStylesheets().add(AlertUtil.class.getResource(ALERT_CSS).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        return dialog.showAndWait();
    }

    // base method
    private static void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        setupAlert(alert, title, header, message);
        alert.showAndWait();
    }

    // helper
    private static void setupAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setGraphic(null); // hide graphic
        alert.getDialogPane().setMinHeight(100);

        // apply css
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtil.class.getResource(ALERT_CSS).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        // center dialog paned
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOnShown(event -> {
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.centerOnScreen();
        });
    }
}