package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.TransactionsDB;
import com.example.coursework.models.Transactions;
import com.example.coursework.utils.AlertUtil;
import com.example.coursework.utils.ConfigManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class SalesController {
    @FXML private TableView<Transactions> transactionTable;
    @FXML private TableColumn<Transactions, String> transactionIdColumn;
    @FXML private TableColumn<Transactions, String> cashierUsernameColumn;
    @FXML private TableColumn<Transactions, String> transactionDateColumn;
    @FXML private TableColumn<Transactions, String> totalAmountColumn;
    @FXML private TableColumn<Transactions, String> paymentMethodColumn;
    @FXML private Label itemCountLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> paymentMethodFilter;
    @FXML private ComboBox<String> dateRangeFilter;
    @FXML private TextField minAmount;
    @FXML private TextField maxAmount;

    private final TransactionsDB transactionDB = AppCache.getTransactionsDB();

    @FXML
    public void initialize() {
        loadPaymentMethodFilter();

        dateRangeFilter.setItems(FXCollections.observableArrayList(
                "Today", "Last 15 Days", "Last 30 Days", "Last 90 Days", "This Year", "All Time"
        ));
        dateRangeFilter.setValue("All Time");

        setupAmountField(minAmount);
        setupAmountField(maxAmount);
        setupTableColumns();

        loadRecentTransactions();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchTransactions());
        paymentMethodFilter.valueProperty().addListener((obs, oldVal, newVal) -> searchTransactions());
        dateRangeFilter.valueProperty().addListener((obs, oldVal, newVal) -> searchTransactions());
        minAmount.textProperty().addListener((obs, oldVal, newVal) -> searchTransactions());
        maxAmount.textProperty().addListener((obs, oldVal, newVal) -> searchTransactions());
    }

    private void loadPaymentMethodFilter() {
        List<String> paymentMethods = AppCache.getPaymentMethodDB().getAllPaymentMethods();
        List<String> paymentMethodsWithAll = new ArrayList<>();
        paymentMethodsWithAll.add("All");
        paymentMethodsWithAll.addAll(paymentMethods);
        paymentMethodFilter.setItems(FXCollections.observableArrayList(paymentMethodsWithAll));
        paymentMethodFilter.setValue("All");
    }

    private void setupAmountField(TextField field) {
        field.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                field.setText(old);
            }
        });
    }

    private void setupTableColumns() {
        String currency = ConfigManager.getCurrency();
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        cashierUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("cashierUsername"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalAmountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.format("%s%.2f", currency, cellData.getValue().getTotalAmount()))
        );

        transactionDateColumn.setCellValueFactory(cellData -> {
            Transactions transaction = cellData.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
            String formattedTime = transaction.getTransactionDate().format(formatter).toUpperCase();
            return new javafx.beans.property.SimpleStringProperty(formattedTime);
        });
    }

    private void loadRecentTransactions() {
        List<Transactions> transactions = transactionDB.getAllTransactionsLimit();
        transactionTable.setItems(FXCollections.observableArrayList(transactions));
        transactionTable.refresh();
        int totalCount = transactionDB.getTotalTransactionsCount();
        itemCountLabel.setText(transactions.size() + " of " + totalCount + " items");
    }

    // fxml on action buttons
    @FXML
    private void searchTransactions() {
        String searchText = searchField.getText();
        String paymentMethod = paymentMethodFilter.getValue();
        String dateRange = dateRangeFilter.getValue();
        String min = minAmount.getText().isEmpty() ? null : minAmount.getText();
        String max = maxAmount.getText().isEmpty() ? null : maxAmount.getText();

        // Convert "All" to null for database query
        if (paymentMethod != null && paymentMethod.equals("All")) {
            paymentMethod = null;
        }

        List<Transactions> results = transactionDB.searchTransactions(searchText, paymentMethod, dateRange, min, max);
        transactionTable.setItems(FXCollections.observableArrayList(results));

        int totalCount = transactionDB.getTotalTransactionsCount();
        itemCountLabel.setText(results.size() + " of " + totalCount + " items");
    }

    @FXML
    private void downloadCSV() {
        List<Transactions> transactions = transactionTable.getItems();

        if (transactions.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("transactions.csv");
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("ID,Cashier,Payment Method,Total,Date\n");

                for (Transactions t : transactions) {
                    writer.write(t.getTransactionId() + ",");
                    writer.write(t.getCashierUsername() + ",");
                    writer.write(t.getPaymentMethod() + ",");
                    writer.write(t.getTotalAmount() + ",");
                    writer.write(t.getTransactionDate() + "\n");
                }
                AlertUtil.showSuccess("Export success.");
            } catch (IOException e) {
                AlertUtil.showError("Export failed.");
            }
        }
    }

    public void viewReport(ActionEvent actionEvent) {
        AdminLayoutController.goToReports();
    }
}