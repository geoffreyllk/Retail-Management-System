package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.TransactionItemsDB;
import com.example.coursework.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.ArrayList;

public class ReportsController {

    private final String currency = ConfigManager.getCurrency();

    @FXML private VBox mainContent;
    @FXML private ComboBox<String> dateRangeFilter;
    @FXML private ComboBox<String> categoryFilter;

    @FXML private Label totalRevenueLabel;
    @FXML private Label transactionlabel;
    @FXML private Label averageTransactionLabel;
    @FXML private Label itemsSoldLabel;

    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private TableView<TransactionItemsDB.TopProduct> productTable;
    @FXML private TableColumn<TransactionItemsDB.TopProduct, Integer> rowNumberColumn;
    @FXML private TableColumn<TransactionItemsDB.TopProduct, String> nameColumn;
    @FXML private TableColumn<TransactionItemsDB.TopProduct, Integer> qtySoldColumn;
    @FXML private TableColumn<TransactionItemsDB.TopProduct, Double> revenueColumn;

    @FXML private PieChart categoryPieChart;


    private final TransactionItemsDB itemsDB = AppCache.getTransactionItemsDB();

    @FXML
    public void initialize() {
        setupFilters();
        setupTableColumns();
        loadReport();

        // combo box listeners
        dateRangeFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateChartAndStats(newVal);
            updateProductTable(categoryFilter.getValue(), newVal);
        });
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProductTable(newVal, dateRangeFilter.getValue());
        });
    }

    private void setupFilters() {
        dateRangeFilter.setItems(FXCollections.observableArrayList("Today", "Last 7 Days", "Last 30 Days", "This Year", "All Time"));
        dateRangeFilter.setValue("Last 30 Days");

        // Load categories from database
        loadCategoryFilter();
    }

    private void loadCategoryFilter() {
        List<String> categories = AppCache.getCategoryDB().getAllCategories();
        List<String> categoriesWithAll = new ArrayList<>();
        categoriesWithAll.add("All");
        categoriesWithAll.addAll(categories);
        categoryFilter.setItems(FXCollections.observableArrayList(categoriesWithAll));
        categoryFilter.setValue("All");
    }

    private void updateChartAndStats(String dateRange) {
        if (dateRange == null) dateRange = "Last 30 Days";

        // Get data from DB
        double totalRevenue = itemsDB.getTotalRevenue(dateRange);
        int totalTransactions = itemsDB.getTotalTransactions(dateRange);
        int totalItemsSold = itemsDB.getTotalItemsSold(dateRange);

        // Update stat cards with dynamic currency
        totalRevenueLabel.setText(String.format("%s%.2f", currency, totalRevenue));
        transactionlabel.setText(String.valueOf(totalTransactions));
        averageTransactionLabel.setText(String.format("%s%.2f", currency, totalTransactions > 0 ? totalRevenue / totalTransactions : 0));
        itemsSoldLabel.setText(String.valueOf(totalItemsSold));

        List<TransactionItemsDB.ChartData> chartData = itemsDB.getChartData(dateRange);

        revenueChart.getData().clear();

        if (chartData.isEmpty()) {
            XYChart.Series<String, Number> emptySeries = new XYChart.Series<>();
            emptySeries.setName("No Revenue Data");
            revenueChart.getData().add(emptySeries);
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        double maxRevenue = 0;
        for (TransactionItemsDB.ChartData data : chartData) {
            String formattedLabel = formatLabel(data.label, dateRange);
            series.getData().add(new XYChart.Data<>(formattedLabel, data.value));
            if (data.value > maxRevenue) {
                maxRevenue = data.value;
            }
        }

        revenueChart.getData().add(series);

        revenueChart.setAnimated(true);
        revenueChart.setCreateSymbols(true);
        revenueChart.setHorizontalGridLinesVisible(true);
        revenueChart.setVerticalGridLinesVisible(true);
        revenueChart.setAlternativeRowFillVisible(false);
        revenueChart.setHorizontalZeroLineVisible(true);
        revenueChart.setLegendVisible(false);

        // format y axis
        yAxis.setAutoRanging(false);
        double upperBound = maxRevenue * 1.2;
        if (upperBound < 100) upperBound = 100;
        upperBound = Math.ceil(upperBound / 100) * 100;
        yAxis.setUpperBound(upperBound);
        yAxis.setLowerBound(0);
        setTickUnit(upperBound);
        yAxis.setTickLabelGap(5);
    }

    private void updateProductTable(String category, String dateRange) {
        if (category == null) category = "All";
        if (dateRange == null) dateRange = "Last 30 Days";

        List<TransactionItemsDB.TopProduct> topProducts = itemsDB.getTopSellingProducts(dateRange, category);
        updatePieChart(dateRange, category);

        productTable.setItems(FXCollections.observableArrayList(topProducts));
        refreshRowNumbers();
    }

    private String formatLabel(String rawLabel, String dateRange) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        switch (dateRange) {
            case "Today":
                int hour = Integer.parseInt(rawLabel);
                return String.format("%d:00 %s", hour % 12 == 0 ? 12 : hour % 12, hour < 12 ? "AM" : "PM");

            case "Last 7 Days":
                String[] parts7 = rawLabel.split("-");
                int month7 = Integer.parseInt(parts7[1]);
                int day7 = Integer.parseInt(parts7[2]);
                return weekdays[(day7 % 7)] + " " + day7 + " " + months[month7 - 1];

            case "Last 30 Days":
                String[] parts30 = rawLabel.split("-");
                int month30 = Integer.parseInt(parts30[1]);
                int day30 = Integer.parseInt(parts30[2]);
                return day30 + " " + months[month30 - 1];

            case "This Year":
                int month = Integer.parseInt(rawLabel);
                return months[month - 1];

            case "All Time":
                String[] parts = rawLabel.split("-");
                int m = Integer.parseInt(parts[1]);
                String year = parts[0].substring(2);
                return months[m - 1] + " '" + year;

            default:
                return rawLabel;
        }
    }

    private void setTickUnit(double upperBound) {
        double rawTick = upperBound / 5;

        double tickUnit;
        if (rawTick <= 10) {
            tickUnit = Math.ceil(rawTick / 5) * 5;
        } else if (rawTick <= 20) {
            tickUnit = 20;
        } else if (rawTick <= 50) {
            tickUnit = 50;
        } else if (rawTick <= 100) {
            tickUnit = 100;
        } else if (rawTick <= 200) {
            tickUnit = 200;
        } else if (rawTick <= 500) {
            tickUnit = 500;
        } else {
            tickUnit = 1000;
        }

        yAxis.setTickUnit(tickUnit);

        // Format y-axis labels with dynamic currency
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                double val = object.doubleValue();
                if (val >= 1000) {
                    return String.format("%s%.0fk", currency, val / 1000);
                }
                return String.format("%s%.0f", currency, val);
            }
        });
    }

    private void setupTableColumns() {
        rowNumberColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        productTable.getItems().indexOf(cellData.getValue()) + 1
                ).asObject()
        );
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtySoldColumn.setCellValueFactory(new PropertyValueFactory<>("qtySold"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        // Format revenue column with dynamic currency
        revenueColumn.setCellFactory(col -> new TableCell<TransactionItemsDB.TopProduct, Double>() {
            @Override
            protected void updateItem(Double revenue, boolean empty) {
                super.updateItem(revenue, empty);
                setText(empty ? null : String.format("%s%.2f", currency, revenue));
            }
        });
    }

    @FXML
    private void loadReport() {
        String dateRange = dateRangeFilter.getValue();
        String category = categoryFilter.getValue();

        if (dateRange == null) dateRange = "Last 30 Days";
        if (category == null) category = "All";

        double totalRevenue = itemsDB.getTotalRevenue(dateRange);
        int totalTransactions = itemsDB.getTotalTransactions(dateRange);
        int totalItemsSold = itemsDB.getTotalItemsSold(dateRange);
        List<TransactionItemsDB.TopProduct> topProducts = itemsDB.getTopSellingProducts(dateRange, category);

        totalRevenueLabel.setText(String.format("%s%.2f", currency, totalRevenue));
        transactionlabel.setText(String.valueOf(totalTransactions));
        averageTransactionLabel.setText(String.format("%s%.2f", currency, totalTransactions > 0 ? totalRevenue / totalTransactions : 0));
        itemsSoldLabel.setText(String.valueOf(totalItemsSold));

        updateChartAndStats(dateRange);
        updatePieChart(dateRange, category);

        productTable.setItems(FXCollections.observableArrayList(topProducts));
        refreshRowNumbers();
    }

    private void refreshRowNumbers() {
        rowNumberColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        productTable.getItems().indexOf(cellData.getValue()) + 1
                ).asObject()
        );
        productTable.refresh();
    }

    private void updatePieChart(String dateRange, String category) {
        categoryPieChart.getData().clear();

        if (category == null || category.equals("All")) {
            List<TransactionItemsDB.CategoryRevenue> data = itemsDB.getCategoryRevenue(dateRange);
            double totalRevenue = itemsDB.getTotalRevenue(dateRange);

            for (TransactionItemsDB.CategoryRevenue cr : data) {
                double percent = totalRevenue > 0 ? (cr.revenue / totalRevenue) * 100 : 0;
                String label = String.format("%s\n%.1f%%", cr.category, percent);
                categoryPieChart.getData().add(new PieChart.Data(label, cr.revenue));
            }
        } else {
            List<TransactionItemsDB.ProductRevenue> data = itemsDB.getProductRevenueByCategoryWithOthers(dateRange, category);
            double categoryTotal = data.stream().mapToDouble(pr -> pr.revenue).sum();

            for (TransactionItemsDB.ProductRevenue pr : data) {
                double percent = categoryTotal > 0 ? (pr.revenue / categoryTotal) * 100 : 0;
                String label = String.format("%s\n%.1f%%", pr.name, percent);
                categoryPieChart.getData().add(new PieChart.Data(label, pr.revenue));
            }
        }

        categoryPieChart.setAnimated(false);
        categoryPieChart.setLabelsVisible(true);
        categoryPieChart.setClockwise(true);
        categoryPieChart.setLegendVisible(false);
        categoryPieChart.setStartAngle(90);
    }
}