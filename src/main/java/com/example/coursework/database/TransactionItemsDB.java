package com.example.coursework.database;

import com.example.coursework.models.TransactionItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionItemsDB extends dbHelper {

    public TransactionItemsDB(Connection connection) {
        super(connection);
    }

    // CREATE
    public boolean createTransactionItem(TransactionItems item) {
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, product_name, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getTransactionId());
            pstmt.setString(2, item.getProductId());
            pstmt.setString(3, item.getProductName());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setDouble(5, item.getPrice());
            pstmt.setDouble(6, item.getSubtotal());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to create transaction item: " + e.getMessage());
            return false;
        }
    }

    // Save all items for a transaction
    public boolean createTransactionItems(List<TransactionItems> items) {
        try {
            for (TransactionItems item : items) {
                if (!createTransactionItem(item)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Failed to create transaction items: " + e.getMessage());
            return false;
        }
    }

    // READ transactions items
    private List<TransactionItems> getTransactionsItems(String query, String... condition) {
        List<TransactionItems> items = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (condition != null) {
                for (int i = 0; i < condition.length; i++) {
                    statement.setString(i + 1, condition[i]);
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new TransactionItems(
                            resultSet.getInt("id"),
                            resultSet.getString("transaction_id"),
                            resultSet.getString("product_id"),
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("price"),
                            resultSet.getDouble("subtotal")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to display items: " + e.getMessage());
        }

        return items;
    }

    // Get transaction items for receipt
    public List<TransactionItems> getTransactionItems(String transaction_id) {
        String query = "SELECT * FROM transaction_items WHERE transaction_id = ? ORDER BY product_id";
        return getTransactionsItems(query, transaction_id);
    }

    /* report methods */

    // get total revenue for date range
    public double getTotalRevenue(String dateRange) {
        String sql = "SELECT SUM(total_amount) as total FROM transactions WHERE 1=1" + getDateFilter(dateRange);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error getting total revenue: " + e.getMessage());
            return 0;
        }
    }

    // get transactions count (filtered by date)
    public int getTotalTransactions(String dateRange) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE 1=1" + getDateFilter(dateRange);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error getting total transactions: " + e.getMessage());
            return 0;
        }
    }

    // Get total items sold
    public int getTotalItemsSold(String dateRange) {
        String sql = "SELECT SUM(ti.quantity) as total FROM transaction_items ti JOIN transactions t ON ti.transaction_id = t.transaction_id WHERE 1=1" + getDateFilter(dateRange);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error getting total items sold: " + e.getMessage());
            return 0;
        }
    }

    // chart data
    public List<ChartData> getChartData(String dateRange) {
        List<ChartData> chartData = new ArrayList<>();
        String sql = getChartSQL(dateRange);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ChartData data = new ChartData();
                data.label = rs.getString("label");
                data.value = rs.getDouble("value");
                chartData.add(data);
            }
        } catch (SQLException e) {
            System.out.println("Error getting chart data: " + e.getMessage());
        }

        return chartData;
    }

    // query sql based on daterange
    private String getChartSQL(String dateRange) {
        switch (dateRange) {
            case "Today":
                return "SELECT strftime('%H', transaction_date) as label, COALESCE(SUM(total_amount), 0) as value FROM transactions WHERE DATE(transaction_date) = DATE('now') GROUP BY label ORDER BY label";
            case "Last 7 Days":
                return "SELECT DATE(transaction_date) as label, COALESCE(SUM(total_amount), 0) as value FROM transactions WHERE DATE(transaction_date) >= DATE('now', '-7 days') GROUP BY DATE(transaction_date) ORDER BY transaction_date";
            case "Last 30 Days":
                return "SELECT DATE(transaction_date) as label, COALESCE(SUM(total_amount), 0) as value FROM transactions WHERE DATE(transaction_date) >= DATE('now', '-30 days') GROUP BY DATE(transaction_date) ORDER BY transaction_date";
            case "This Year":
                return "SELECT strftime('%m', transaction_date) as label, COALESCE(SUM(total_amount), 0) as value FROM transactions WHERE strftime('%Y', transaction_date) = strftime('%Y', 'now') GROUP BY label ORDER BY label";
            case "All Time":
                return "SELECT strftime('%Y-%m', transaction_date) as label, COALESCE(SUM(total_amount), 0) as value FROM transactions GROUP BY label ORDER BY label";
            default:
                return "";
        }
    }

    // Get top-selling products
    public List<TopProduct> getTopSellingProducts(String dateRange, String category) {
        StringBuilder sql = new StringBuilder();
        List<String> params = new ArrayList<>();

        sql.append("SELECT p.product_id, p.name, p.category, COALESCE(SUM(ti.quantity), 0) as total_qty, COALESCE(SUM(ti.subtotal), 0) as total_revenue FROM transaction_items ti JOIN products p ON ti.product_id = p.product_id JOIN transactions t ON ti.transaction_id = t.transaction_id WHERE 1=1");
        sql.append(getDateFilter(dateRange));

        if (category != null && !category.equals("All") && !category.isEmpty()) {
            sql.append(" AND p.category = ?");
            params.add(category);
        }

        sql.append(" GROUP BY p.product_id, p.name, p.category ");
        sql.append(" ORDER BY total_revenue DESC LIMIT 10");

        return getTopProducts(sql.toString(), params);
    }

    // date range helper
    private String getDateFilter(String dateRange) {
        if (dateRange == null || dateRange.equals("All Time")) {
            return "";
        }

        switch (dateRange) {
            case "Today":
                return " AND DATE(transaction_date) = DATE('now')";
            case "Last 7 Days":
                return " AND DATE(transaction_date) >= DATE('now', '-7 days')";
            case "Last 30 Days":
                return " AND DATE(transaction_date) >= DATE('now', '-30 days')";
            case "This Year":
                return " AND strftime('%Y', transaction_date) = strftime('%Y', 'now')";
            default:
                return "";
        }
    }

    // Get top products list
    private List<TopProduct> getTopProducts(String query, List<String> params) {
        List<TopProduct> products = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TopProduct tp = new TopProduct();
                tp.productId = rs.getString("product_id");
                tp.name = rs.getString("name");
                tp.category = rs.getString("category");
                tp.qtySold = rs.getInt("total_qty");
                tp.revenue = rs.getDouble("total_revenue");
                products.add(tp);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get top products: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    // top product model
    public static class TopProduct {
        public String productId;
        public String name;
        public String category;
        public int qtySold;
        public double revenue;

        // getters for PropertyValueFactory
        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getQtySold() { return qtySold; }
        public double getRevenue() { return revenue; }
    }

    // inline chart object class
    public static class ChartData {
        public String label;
        public double value;
        public String getLabel() { return label; }
        public double getValue() { return value; }
    }

    // pie chart
    // Get category revenue for pie chart
    public List<CategoryRevenue> getCategoryRevenue(String dateRange) {
        List<CategoryRevenue> list = new ArrayList<>();
        String sql = "SELECT p.category, COALESCE(SUM(ti.subtotal), 0) as revenue FROM transaction_items ti " +
                "JOIN products p ON ti.product_id = p.product_id " +
                "JOIN transactions t ON ti.transaction_id = t.transaction_id " +
                "WHERE 1=1" + getDateFilter(dateRange) +
                " GROUP BY p.category ORDER BY revenue DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                CategoryRevenue cr = new CategoryRevenue();
                cr.category = rs.getString("category");
                cr.revenue = rs.getDouble("revenue");
                list.add(cr);
            }
        } catch (SQLException e) {
            System.out.println("Error getting category revenue: " + e.getMessage());
        }
        return list;
    }

    // Get product revenue within a category for pie chart
    public List<ProductRevenue> getProductRevenueByCategoryWithOthers(String dateRange, String category) {
        List<ProductRevenue> list = new ArrayList<>();

        // Get top 5 products
        String topSql = "SELECT p.name, COALESCE(SUM(ti.subtotal), 0) as revenue FROM transaction_items ti JOIN products p ON ti.product_id = p.product_id JOIN transactions t ON ti.transaction_id = t.transaction_id WHERE 1=1"
                + getDateFilter(dateRange) + " AND p.category = '" + category + "' GROUP BY p.product_id ORDER BY revenue DESC LIMIT 5";

        // Get total revenue for the category
        String totalSql = "SELECT COALESCE(SUM(ti.subtotal), 0) as total FROM transaction_items ti " +
                "JOIN products p ON ti.product_id = p.product_id " +
                "JOIN transactions t ON ti.transaction_id = t.transaction_id " +
                "WHERE 1=1" + getDateFilter(dateRange) +
                " AND p.category = '" + category + "'";

        try {
            // Get top 5 products
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(topSql);
                while (rs.next()) {
                    ProductRevenue pr = new ProductRevenue();
                    pr.name = rs.getString("name");
                    pr.revenue = rs.getDouble("revenue");
                    list.add(pr);
                }
            }

            // Get total category revenue
            double totalRevenue = 0;
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(totalSql);
                if (rs.next()) {
                    totalRevenue = rs.getDouble("total");
                }
            }

            // Calculate top 5 total
            double top5Revenue = 0;
            for (ProductRevenue pr : list) {
                top5Revenue += pr.revenue;
            }

            // Add "Others" if there's remaining revenue
            double othersRevenue = totalRevenue - top5Revenue;
            if (othersRevenue > 0) {
                ProductRevenue others = new ProductRevenue();
                others.name = "Others";
                others.revenue = othersRevenue;
                list.add(others);
            }

        } catch (SQLException e) {
            System.out.println("Error getting product revenue: " + e.getMessage());
        }

        return list;
    }

    // Get trending items for dashboard (by quantity sold)
    public List<TopProduct> getTopSellingForDashboard() {
        String sql = "SELECT p.name, COALESCE(SUM(ti.quantity), 0) as total_qty FROM transaction_items ti JOIN products p ON ti.product_id = p.product_id JOIN transactions t ON ti.transaction_id = t.transaction_id WHERE DATE(t.transaction_date) >= DATE('now', '-7 days') GROUP BY p.name ORDER BY total_qty DESC LIMIT 3";

        List<TopProduct> products = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                TopProduct tp = new TopProduct();
                tp.name = rs.getString("name");
                tp.qtySold = rs.getInt("total_qty");
                products.add(tp);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get top selling for dashboard: " + e.getMessage());
        }
        return products;
    }

    // Inner classes for pie chart data
    public static class CategoryRevenue {
        public String category;
        public double revenue;
    }

    public static class ProductRevenue {
        public String name;
        public double revenue;
    }
}