package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.models.User;
import com.example.coursework.utils.AlertUtil;
import com.example.coursework.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;

public class SettingsController {

    @FXML private VBox mainContent;

    @FXML private Label userIdLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField passwordField;
    @FXML private Button applyChangesButton;

    @FXML private TextField editStoreName;
    @FXML private TextField editCurrency;
    @FXML private TextField editLowStock;
    @FXML private Button onTaxButton;
    @FXML private Button offTaxButton;
    @FXML private TextField editTaxRate;

    @FXML private TableView<String> categoryTable;
    @FXML private TableColumn<String, String> categoryName;
    @FXML private TableColumn<String, Void> categoryAction;

    @FXML private TableView<String> paymentTable;
    @FXML private TableColumn<String, String> paymentName;
    @FXML private TableColumn<String, Void> paymentAction;

    @FXML private VBox overlay;
    @FXML private Label overlayTitle;
    @FXML private Label overlayLabel;
    @FXML private TextField overlayTextField;
    @FXML private Button deleteButton;

    private User currentUser = AppCache.getCurrentUser();
    private String selectedCategory;
    private String selectedPayment;
    private boolean isEditingCategory = false;
    private boolean isEditingPayment = false;

    @FXML
    public void initialize() {
        setupAdminProfile();
        setupSystemPreferences();
        setupCategoryTable();
        setupPaymentTable();
        loadCategories();
        loadPaymentMethods();
    }

    private void setupAdminProfile() {
        userIdLabel.setText(String.valueOf(currentUser.getId()));
        usernameLabel.setText(currentUser.getUsername());
        passwordField.setText("********");
        passwordField.setPromptText("Enter new password to change");
    }

    private void setupSystemPreferences() {
        editStoreName.setText(ConfigManager.getStoreName());
        editCurrency.setText(ConfigManager.getCurrency());
        editLowStock.setText(String.valueOf(ConfigManager.getLowStockThreshold()));
        editTaxRate.setText(String.valueOf(ConfigManager.getTaxRate()));

        boolean taxIncluded = ConfigManager.isTaxIncluded();
        if (taxIncluded) {
            onTaxButton.getStyleClass().add("active");
            offTaxButton.getStyleClass().remove("active");
        } else {
            offTaxButton.getStyleClass().add("active");
            onTaxButton.getStyleClass().remove("active");
        }
    }

    @FXML
    private void onTax() {
        onTaxButton.getStyleClass().add("active");
        offTaxButton.getStyleClass().remove("active");
    }

    @FXML
    private void offTax() {
        offTaxButton.getStyleClass().add("active");
        onTaxButton.getStyleClass().remove("active");
    }

    @FXML
    private void applyChanges() {
        ConfigManager.setStoreName(editStoreName.getText());
        ConfigManager.setCurrency(editCurrency.getText());
        try {
            ConfigManager.setLowStockThreshold(Integer.parseInt(editLowStock.getText()));
        } catch (NumberFormatException e) {}

        try {
            ConfigManager.setTaxRate(Double.parseDouble(editTaxRate.getText()));
        } catch (NumberFormatException e) {}

        boolean taxIncluded = onTaxButton.getStyleClass().contains("active");
        ConfigManager.setTaxIncluded(taxIncluded);

        String newPassword = passwordField.getText();
        if (newPassword != null && !newPassword.equals("********") && !newPassword.isEmpty()) {
            if (AppCache.getUserDB().updateUserPassword(currentUser.getId(), newPassword)) {
                AlertUtil.showSuccess("Settings saved successfully!");
                passwordField.setText("********");
            } else {
                AlertUtil.showError("Failed to update password!");
            }
        } else {
            AlertUtil.showSuccess("Settings saved successfully!");
        }
    }

    private void setupCategoryTable() {
        categoryName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));

        categoryAction.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            {
                editBtn.getStyleClass().add("edit-btn");
                editBtn.setOnAction(e -> {
                    String category = getTableView().getItems().get(getIndex());
                    showCategoryOverlay(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editBtn);
                }
            }
        });
    }

    private void setupPaymentTable() {
        paymentName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));

        paymentAction.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            {
                editBtn.getStyleClass().add("edit-btn");
                editBtn.setOnAction(e -> {
                    String payment = getTableView().getItems().get(getIndex());
                    showPaymentOverlay(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editBtn);
                }
            }
        });
    }

    private void loadCategories() {
        categoryTable.setItems(FXCollections.observableArrayList(AppCache.getCategoryDB().getAllCategories()));
    }

    private void loadPaymentMethods() {
        paymentTable.setItems(FXCollections.observableArrayList(AppCache.getPaymentMethodDB().getAllPaymentMethods()));
    }

    @FXML
    private void showAddCategoryOverlay() {
        isEditingCategory = true;
        isEditingPayment = false;
        overlayTitle.setText("Add Category");
        overlayLabel.setText("Category:");
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        overlayTextField.clear();
        overlayTextField.setPromptText("Enter category name");
        selectedCategory = null;
        showOverlay();
    }

    @FXML
    private void showAddPaymentOverlay() {
        isEditingCategory = false;
        isEditingPayment = true;
        overlayTitle.setText("Add Payment Method");
        overlayLabel.setText("Payment Method:");
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        overlayTextField.clear();
        overlayTextField.setPromptText("Enter payment method");
        selectedPayment = null;
        showOverlay();
    }

    private void showCategoryOverlay(String category) {
        isEditingCategory = true;
        isEditingPayment = false;
        overlayTitle.setText("Edit Category");
        overlayLabel.setText("Category:");
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        overlayTextField.setText(category);
        overlayTextField.setPromptText("Enter category name");
        selectedCategory = category;
        showOverlay();
    }

    private void showPaymentOverlay(String payment) {
        isEditingCategory = false;
        isEditingPayment = true;
        overlayTitle.setText("Edit Payment Method");
        overlayLabel.setText("Payment Method:");
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        overlayTextField.setText(payment);
        overlayTextField.setPromptText("Enter payment method");
        selectedPayment = payment;
        showOverlay();
    }

    private void showOverlay() {
        mainContent.setStyle("-fx-opacity: 0.3;");
        mainContent.setDisable(true);
        overlay.setVisible(true);
        overlay.setManaged(true);
        overlay.toFront();
    }

    @FXML
    private void hideOverlay() {
        mainContent.setStyle("");
        mainContent.setDisable(false);
        overlay.setVisible(false);
        overlay.setManaged(false);
        categoryTable.getSelectionModel().clearSelection();
        paymentTable.getSelectionModel().clearSelection();
        selectedCategory = null;
        selectedPayment = null;
    }

    @FXML
    private void saveOverlay() {
        String name = overlayTextField.getText();
        if (name == null || name.trim().isEmpty()) {
            AlertUtil.showError("Name cannot be empty!");
            return;
        }

        if (isEditingCategory) {
            if (selectedCategory == null) {
                AppCache.getCategoryDB().addCategory(name);
                AlertUtil.showSuccess("Category added successfully!");
            } else {
                AppCache.getCategoryDB().updateCategory(selectedCategory, name);
                AlertUtil.showSuccess("Category updated successfully!");
            }
            hideOverlay();
            loadCategories();
        } else if (isEditingPayment) {
            if (selectedPayment == null) {
                AppCache.getPaymentMethodDB().addPaymentMethod(name);
                AlertUtil.showSuccess("Payment method added successfully!");
            } else {
                AppCache.getPaymentMethodDB().updatePaymentMethod(selectedPayment, name);
                AlertUtil.showSuccess("Payment method updated successfully!");
            }
            hideOverlay();
            loadPaymentMethods();
        }
    }

    @FXML
    private void deleteOverlayItem() {
        if (isEditingCategory && selectedCategory != null) {
            deleteCategory(selectedCategory);
        } else if (isEditingPayment && selectedPayment != null) {
            deletePayment(selectedPayment);
        }
    }

    private void deleteCategory(String category) {
        if (AlertUtil.showConfirmation("Confirm Delete", "Delete category: " + category + "?", "Products will be moved to 'Uncategorized'.")) {
            AppCache.getProductDB().updateProductsCategory(category, "Uncategorized");
            AppCache.getCategoryDB().deleteCategory(category);
            AlertUtil.showSuccess("Category deleted successfully! Products moved to 'Uncategorized'.");
            hideOverlay();
            loadCategories();
        }
    }

    private void deletePayment(String payment) {
        if (AlertUtil.showConfirmation("Confirm Delete", "Delete payment method: " + payment + "?", "This action cannot be undone.")) {
            AppCache.getPaymentMethodDB().deletePaymentMethod(payment);
            AlertUtil.showSuccess("Payment method deleted successfully!");
            hideOverlay();
            loadPaymentMethods();
        }
    }
}