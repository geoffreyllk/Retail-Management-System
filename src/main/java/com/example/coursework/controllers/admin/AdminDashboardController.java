package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.ProductDB;
import com.example.coursework.database.TransactionItemsDB;
import com.example.coursework.database.TransactionsDB;
import com.example.coursework.models.Product;
import com.example.coursework.models.Transactions;
import com.example.coursework.utils.ConfigManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {
    @FXML
    private Label todaySales;

    @FXML
    private Label todaySalesPercentage;

    @FXML
    private Label todayTransactions;

    @FXML
    private Label todayTransactionsPercentage;

    @FXML
    private Label averageSpend;

    @FXML
    private Label averageSpendPercentage;

    @FXML
    private Label thisMonthSales;

    @FXML
    private Label thisMonthSalesPercentage;

    @FXML
    private VBox lowStockContainer;

    @FXML
    private BarChart<String, Number> weeklySalesChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private TableView<Transactions> transactionTable;

    @FXML
    private TableColumn<Transactions, String> transactionIdColumn;

    @FXML
    private TableColumn<Transactions, String> cashierUsernameColumn;

    @FXML
    private TableColumn<Transactions, String> transactionDateColumn;

    @FXML
    private TableColumn<Transactions, Double> totalAmountColumn;

    @FXML private Label lowStockName1, lowStockName2, lowStockName3;
    @FXML private Label lowStockQty1, lowStockQty2, lowStockQty3;
    @FXML private Label topSellingName1, topSellingName2, topSellingName3;
    @FXML private Label topSellingQty1, topSellingQty2, topSellingQty3;

    private final TransactionsDB transactionDB = AppCache.getTransactionsDB();
    private final ProductDB productDB = AppCache.getProductDB();
    private final TransactionItemsDB transactionItemsDB = AppCache.getTransactionItemsDB();

    @FXML
    public void initialize() {
        loadDashboardData();
        loadTransactions();
        loadWeeklySales();
        loadLowStockItems();
        loadTopSellingItems();
    }

    private void loadDashboardData() {
        String currency = ConfigManager.getCurrency();

        // Get today's values
        double todaySalesValue = transactionDB.getTodaySales(0);
        int todayTransactionsValue = transactionDB.getTodayTransactions(0);
        double todayAvgSpendValue = transactionDB.getAverageSpend(0);
        double thisMonthSalesValue = transactionDB.getSalesForMonth(0);

        // Get yesterday's values
        double yesterdaySalesValue = transactionDB.getTodaySales(-1);
        int yesterdayTransactionsValue = transactionDB.getTodayTransactions(-1);
        double yesterdayAvgSpendValue = transactionDB.getAverageSpend(-1);
        double lastMonthSalesValue = transactionDB.getSalesForMonth(-1);

        // Display today's values with dynamic currency
        todaySales.setText(String.format("%s%.2f", currency, todaySalesValue));
        todayTransactions.setText(String.valueOf(todayTransactionsValue));
        averageSpend.setText(String.format("%s%.2f", currency, todayAvgSpendValue));
        thisMonthSales.setText(String.format("%s%.2f", currency, thisMonthSalesValue));

        // Calculate and display percentages
        setPercentage(todaySalesPercentage, todaySalesValue, yesterdaySalesValue);
        setPercentage(todayTransactionsPercentage, todayTransactionsValue, yesterdayTransactionsValue);
        setPercentage(averageSpendPercentage, todayAvgSpendValue, yesterdayAvgSpendValue);
        setPercentage(thisMonthSalesPercentage, thisMonthSalesValue, lastMonthSalesValue);
    }

    private void setPercentage(Label label, double today, double yesterday) {
        double percent = 0;
        if (yesterday != 0) {
            percent = ((today - yesterday) / yesterday) * 100;
        }

        if (percent > 0) {
            label.setText(String.format("+%.0f%%", percent));
            label.getStyleClass().removeAll("percentage-increase", "percentage-decrease");
            label.getStyleClass().add("percentage-increase");
        } else if (percent < 0) {
            label.setText(String.format("%.0f%%", percent));
            label.getStyleClass().removeAll("percentage-increase", "percentage-decrease");
            label.getStyleClass().add("percentage-decrease");
        } else {
            label.setText("-%");
        }
    }

    private void loadTransactions() {
        String currency = ConfigManager.getCurrency();

        // Set up table columns
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        cashierUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("cashierUsername"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Format total amount as RM
        totalAmountColumn.setCellFactory(col -> new TableCell<Transactions, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%s%.2f", currency, amount));
                }
            }
        });

        // Format date for display
        transactionDateColumn.setCellValueFactory(cellData -> {
            Transactions transaction = cellData.getValue();
            String formatted = transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("HH:mm, d MMM"));
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        // Get all transactions from database
        List<Transactions> transactions = transactionDB.getAllTransactionsLimit();

        // Add to table
        transactionTable.getItems().setAll(transactions);
    }

    private void loadLowStockItems() {
        List<Product> lowStockProducts = productDB.getLowStockProducts();

        Label[] nameLabels = {lowStockName1, lowStockName2, lowStockName3};
        Label[] qtyLabels = {lowStockQty1, lowStockQty2, lowStockQty3};

        if (lowStockProducts.isEmpty()) {
            // Hide the entire container
            lowStockContainer.setVisible(false);
            lowStockContainer.setManaged(false);
            return;
        }

        // Show container
        lowStockContainer.setVisible(true);
        lowStockContainer.setManaged(true);

        // Hide all rows first
        for (int i = 0; i < 3; i++) {
            nameLabels[i].getParent().setVisible(false);
            nameLabels[i].getParent().setManaged(false);
        }

        // load data
        for (int i = 0; i < lowStockProducts.size() && i < 3; i++) {
            Product product = lowStockProducts.get(i);
            nameLabels[i].setText(product.getName());
            qtyLabels[i].setText(String.valueOf(product.getStock()));
            nameLabels[i].getParent().setVisible(true);
            nameLabels[i].getParent().setManaged(true);
        }
    }

    private void loadTopSellingItems() {
        List<TransactionItemsDB.TopProduct> topProducts = transactionItemsDB.getTopSellingForDashboard();

        Label[] nameLabels = {topSellingName1, topSellingName2, topSellingName3};
        Label[] qtyLabels = {topSellingQty1, topSellingQty2, topSellingQty3};

        // Hide all first
        for (int i = 0; i < 3; i++) {
            nameLabels[i].setText("");
            qtyLabels[i].setText("");
            nameLabels[i].getParent().setVisible(false);
        }

        // Show actual data
        for (int i = 0; i < topProducts.size() && i < 3; i++) {
            TransactionItemsDB.TopProduct product = topProducts.get(i);
            nameLabels[i].setText(product.getName());
            qtyLabels[i].setText(product.getQtySold() + " pcs");
            nameLabels[i].getParent().setVisible(true);
        }
    }


    // bar chart
    private void loadWeeklySales() {
        // Get sales for last 7 days (index 0 = today, index 6 = 7 days ago)
        double[] weeklySales = transactionDB.getLast7DaysSales();

        // Create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        // date labels for last 7 days
        LocalDate today = LocalDate.now();
        // oldest to newest (left-to-right)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayLabel = date.format(DateTimeFormatter.ofPattern("EEE")).toUpperCase();
            series.getData().add(new XYChart.Data<>(dayLabel, weeklySales[i]));
        }

        weeklySalesChart.getData().clear();
        weeklySalesChart.getData().add(series);

        // Style the chart
        weeklySalesChart.setAnimated(true);
        weeklySalesChart.setLegendVisible(false);
        weeklySalesChart.setCategoryGap(18);

        yAxis.setVisible(false);
        yAxis.setTickLabelsVisible(false);
    }

    public void viewTransactions(ActionEvent actionEvent) {
        AdminLayoutController.goToSales();
    }

    public void viewReport(ActionEvent actionEvent) {
        AdminLayoutController.goToReports();
    }
}