package com.example.coursework.database;

import com.example.coursework.models.TransactionItems;
import com.example.coursework.models.Transactions;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionsDB extends dbHelper {

    private TransactionItemsDB transactionItemsDB;

    public TransactionsDB(Connection connection) {
        super(connection);
        this.transactionItemsDB = new TransactionItemsDB(connection);
    }

    // CREATE a transaction AND all its items (passes transaction model with items inside)
    public boolean createTransaction(Transactions transaction) {
        String sqlQuery = "INSERT INTO transactions (transaction_id, cashier_username, transaction_date, total_amount, payment_method) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(sqlQuery)) {
            // Insert the transaction
            pstatement.setString(1, transaction.getTransactionId());
            pstatement.setString(2, transaction.getCashierUsername());
            pstatement.setString(3, transaction.getFormattedTransactionDate());
            pstatement.setDouble(4, transaction.getTotalAmount());
            pstatement.setString(5, transaction.getPaymentMethod());

            int inserted = pstatement.executeUpdate();

            if (inserted > 0) {
                // create transaction's items
                boolean itemsSaved = transactionItemsDB.createTransactionItems(transaction.getItems());
                if (!itemsSaved) {
                    System.out.println("Warning: Failed to save some transaction items");
                }
                return itemsSaved;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Failed to create transaction: " + e.getMessage());
            return false;
        }
    }

    // READ transactions
    private List<Transactions> getTransactions(String query, String ... condition) {
        List<Transactions> transactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)){
            if (condition != null) {
                for (int i = 0; i < condition.length; i++) {
                    statement.setString(i + 1, condition[i]);
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp("transaction_date");
                    LocalDateTime dateTime = timestamp.toLocalDateTime();

                    String transactionId = resultSet.getString("transaction_id");

                    // Get transaction items using TransactionItemsDB
                    List<TransactionItems> items = transactionItemsDB.getTransactionItems(transactionId);

                    transactions.add(new Transactions(
                            resultSet.getString("transaction_id"),
                            resultSet.getString("cashier_username"),
                            dateTime,
                            resultSet.getDouble("total_amount"),
                            resultSet.getString("payment_method"),
                            items
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to display transactions(s): " + e.getMessage());
        }

        return transactions;
    }

    public List<Transactions> getAllTransactions() {
        String query = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        return getTransactions(query, null);
    }

    public List<Transactions> getAllTransactionsLimit() {
        String query = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT 30";
        return getTransactions(query, null);
    }

    public List<Transactions> getTransaction(String condition) {
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";
        return getTransactions(query, condition);
    }

    public int getTotalTransactionsCount() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getTodaySales(int daysOffset) {
        String sql;
        if (daysOffset == 0) {
            sql = "SELECT SUM(total_amount) AS total_amount FROM transactions WHERE DATE(transaction_date) = DATE('now', 'localtime')";
        } else {
            sql = "SELECT SUM(total_amount) AS total_amount FROM transactions WHERE DATE(transaction_date) = DATE('now', '" + daysOffset + " days', 'localtime')";
        }

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getDouble("total_amount");
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getTodayTransactions(int daysOffset) {
        String sql;
        if (daysOffset == 0) {
            sql = "SELECT COUNT(*) AS today_transactions FROM transactions WHERE DATE(transaction_date) = DATE('now', 'localtime')";
        } else {
            sql = "SELECT COUNT(*) AS today_transactions FROM transactions WHERE DATE(transaction_date) = DATE('now', '" + daysOffset + " days', 'localtime')";
        }
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt("today_transactions");
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getAverageSpend(int daysOffset) {
        String sql;
        if (daysOffset == 0) {
            sql = "SELECT AVG(total_amount) AS average_spend FROM transactions WHERE DATE(transaction_date) = DATE('now', 'localtime')";
        } else {
            sql = "SELECT AVG(total_amount) AS average_spend FROM transactions WHERE DATE(transaction_date) = DATE('now', '" + daysOffset + " days', 'localtime')";
        }
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getDouble("average_spend");
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getSalesForMonth(int monthsOffset) {
        String sql;
        if (monthsOffset == 0) {
            sql = "SELECT SUM(total_amount) AS monthly_sales FROM transactions WHERE strftime('%m', transaction_date) = strftime('%m', 'now') AND strftime('%Y', transaction_date) = strftime('%Y', 'now')";
        } else {
            sql = "SELECT SUM(total_amount) AS monthly_sales FROM transactions WHERE strftime('%m', transaction_date) = strftime('%m', 'now', '" + monthsOffset + " months') AND strftime('%Y', transaction_date) = strftime('%Y', 'now', '" + monthsOffset + " months')";
        }
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getDouble("monthly_sales");
        } catch (SQLException e) {
            return 0;
        }
    }

    public double[] getLast7DaysSales() {
        double[] sales = new double[7];
        String sql = "SELECT DATE(transaction_date) as sale_date, SUM(total_amount) as daily_sales FROM transactions WHERE DATE(transaction_date) >= DATE('now', '-6 days', 'localtime') GROUP BY DATE(transaction_date) ORDER BY sale_date DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            int index = 0;
            while (rs.next() && index < 7) {
                sales[index] = rs.getDouble("daily_sales");
                index++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get last 7 days sales: " + e.getMessage());
        }
        return sales;
    }

    public List<Transactions> searchTransactions(String searchText, String paymentMethod, String dateRange, String minAmount, String maxAmount) {
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (searchText != null && !searchText.trim().isEmpty()) {
            query.append(" AND (transaction_id LIKE ? OR cashier_username LIKE ?)");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }

        if (paymentMethod != null && !paymentMethod.equals("All") && !paymentMethod.isEmpty()) {
            query.append(" AND payment_method = ?");
            params.add(paymentMethod);
        }

        if (dateRange != null && !dateRange.equals("All Time")) {
            switch (dateRange) {
                case "Today":
                    query.append(" AND DATE(transaction_date) = DATE('now', 'localtime')");
                    break;
                case "Last 15 Days":
                    query.append(" AND DATE(transaction_date) >= DATE('now', '-15 days', 'localtime')");
                    break;
                case "Last 30 Days":
                    query.append(" AND DATE(transaction_date) >= DATE('now', '-30 days', 'localtime')");
                    break;
                case "Last 90 Days":
                    query.append(" AND DATE(transaction_date) >= DATE('now', '-90 days', 'localtime')");
                    break;
                case "This Year":
                    query.append(" AND strftime('%Y', transaction_date) = strftime('%Y', 'now')");
                    break;
            }
        }

        if (minAmount != null && !minAmount.trim().isEmpty()) {
            query.append(" AND total_amount >= ?");
            params.add(minAmount);
        }

        if (maxAmount != null && !maxAmount.trim().isEmpty()) {
            query.append(" AND total_amount <= ?");
            params.add(maxAmount);
        }

        query.append(" ORDER BY transaction_date DESC");

        return getTransactions(query.toString(), params.toArray(new String[0]));
    }
}