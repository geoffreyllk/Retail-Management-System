package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.UserDB;
import com.example.coursework.models.User;
import com.example.coursework.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;

public class UsersController {

    @FXML private VBox mainContent;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @FXML private Label itemCountLabel;
    @FXML private Label roleCountLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;

    // Overlay fields
    @FXML private VBox userOverlay;
    @FXML private Label overlayTitle;
    @FXML private Button deleteButton;
    @FXML private Button resetPasswordButton;
    @FXML private TextField editUsername;
    @FXML private PasswordField editUserPassword;
    @FXML private PasswordField editConfirmPassword;
    @FXML private VBox passwordFieldsContainer;
    @FXML private ComboBox<String> editUserRole;

    private final UserDB userDB = AppCache.getUserDB();
    private User selectedUser;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupValidation();
        setupRealTimeFilters();  // ADD THIS - enables real-time filtering
        loadUsers();

        userTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 1) {
                selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    if (selectedUser.getId() == AppCache.getCurrentUser().getId()) {
                        AlertUtil.showInfo("To edit your own account, please go to the settings page.");
                        userTable.getSelectionModel().clearSelection();
                        return;
                    }
                    showEditOverlay(selectedUser);
                }
            }
        });
    }

    private void setupTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void setupFilters() {
        roleFilter.setItems(FXCollections.observableArrayList("All", "admin", "cashier"));
        roleFilter.setValue("All");
        editUserRole.setItems(FXCollections.observableArrayList("admin", "cashier"));
    }

    // ADD THIS METHOD - enables real-time filtering
    private void setupRealTimeFilters() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadUsers());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadUsers());
    }

    private void setupValidation() {
        editUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            if (newValue.length() > 20) {
                editUsername.setText(oldValue);
                return;
            }
            if (!newValue.matches("[a-zA-Z0-9_]*")) {
                editUsername.setText(oldValue);
            }
        });

        editUserPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 20) {
                editUserPassword.setText(oldValue);
            }
        });
        editConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 20) {
                editConfirmPassword.setText(oldValue);
            }
        });
    }

    private String getValidatedPassword() {
        String password = editUserPassword.getText();
        String confirmPassword = editConfirmPassword.getText();

        if (password == null || password.isEmpty()) {
            AlertUtil.showError("Password is required.");
            return null;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.showError("Passwords do not match!");
            return null;
        }

        if (password.length() > 20) {
            AlertUtil.showError("Password cannot exceed 20 characters!");
            return null;
        }

        return password;
    }

    private void showEditOverlay(User user) {
        isEditMode = true;
        overlayTitle.setText("Edit User");
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        resetPasswordButton.setVisible(true);
        resetPasswordButton.setManaged(true);

        passwordFieldsContainer.setVisible(false);
        passwordFieldsContainer.setManaged(false);

        editUsername.setText(user.getUsername());
        editUsername.setDisable(true);
        editUserRole.setValue(user.getRole());

        selectedUser = user;
        showOverlay();
    }

    @FXML
    private void showAddOverlay() {
        isEditMode = false;
        overlayTitle.setText("Add User");
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        resetPasswordButton.setVisible(false);
        resetPasswordButton.setManaged(false);

        passwordFieldsContainer.setVisible(true);
        passwordFieldsContainer.setManaged(true);

        editUsername.clear();
        editUserPassword.clear();
        editConfirmPassword.clear();
        editUserRole.getSelectionModel().clearSelection();
        editUsername.setDisable(false);

        selectedUser = null;
        showOverlay();
    }

    @FXML
    private void resetPassword() {
        passwordFieldsContainer.setVisible(true);
        passwordFieldsContainer.setManaged(true);
        editUserPassword.clear();
        editConfirmPassword.clear();
        resetPasswordButton.setVisible(false);
        resetPasswordButton.setManaged(false);
    }

    private void showOverlay() {
        mainContent.setStyle("-fx-opacity: 0.3;");
        mainContent.setDisable(true);
        userOverlay.setVisible(true);
        userOverlay.setManaged(true);
        userOverlay.toFront();
    }

    @FXML
    private void hideOverlay() {
        mainContent.setStyle("");
        mainContent.setDisable(false);
        userOverlay.setVisible(false);
        userOverlay.setManaged(false);
        userTable.getSelectionModel().clearSelection();
        selectedUser = null;
    }

    @FXML
    private void saveUser() {
        String role = editUserRole.getValue();
        if (role == null || role.isEmpty()) {
            AlertUtil.showError("Role is required.");
            return;
        }

        try {
            if (isEditMode) {
                if (!userDB.updateUserRole(selectedUser.getId(), role)) {
                    AlertUtil.showError("Failed to update role!");
                    return;
                }

                if (passwordFieldsContainer.isVisible()) {
                    String password = getValidatedPassword();
                    if (password == null) return;

                    if (!userDB.updateUserPassword(selectedUser.getId(), password)) {
                        AlertUtil.showError("Failed to update password!");
                        return;
                    }
                }

                AlertUtil.showSuccess("User updated successfully!");
                hideOverlay();
                loadUsers();
            } else {
                String username = editUsername.getText();
                if (username == null || username.isEmpty()) {
                    AlertUtil.showError("Username is required.");
                    return;
                }

                String password = getValidatedPassword();
                if (password == null) return;

                User newUser = new User(0, username, password, role);
                if (userDB.createUser(newUser)) {
                    AlertUtil.showSuccess("User added successfully!");
                    hideOverlay();
                    loadUsers();
                } else {
                    AlertUtil.showError("Failed to add user.");
                }
            }
        } catch (Exception e) {
            AlertUtil.showError("An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void deleteUser() {
        if (AlertUtil.showConfirmation("Confirm Delete", "Delete " + selectedUser.getUsername() + "?", "This action cannot be undone.")) {
            if (userDB.deleteUser(selectedUser.getId())) {
                AlertUtil.showSuccess("User deleted successfully!");
                hideOverlay();
                loadUsers();
            } else {
                AlertUtil.showError("Failed to delete user.");
            }
        }
    }

    @FXML
    private void searchUsers() {
        loadUsers();
    }

    private void loadUsers() {
        String searchText = searchField.getText();
        String role = roleFilter.getValue();
        String roleParam = "All".equals(role) ? null : role;

        List<User> users = userDB.searchUsers(searchText, roleParam);
        userTable.setItems(FXCollections.observableArrayList(users));

        int totalCount = userDB.getTotalUserCount();
        int totalAdminCount = userDB.getTotalAdminCount();
        int totalCashierCount = totalCount - totalAdminCount;
        itemCountLabel.setText("Showing: " + users.size() + " of " + totalCount + " users");
        roleCountLabel.setText("Total Admins: " + totalAdminCount + " - Total Cashiers: " + totalCashierCount);
    }
}