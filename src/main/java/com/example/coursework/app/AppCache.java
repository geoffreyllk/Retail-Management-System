package com.example.coursework.app;

import com.example.coursework.database.*;
import com.example.coursework.models.User;
import java.sql.Connection;

public class AppCache {
    private static Connection connection;
    private static UserDB userDB;
    private static ProductDB productDB;
    private static TransactionsDB transactionsDB;
    private static TransactionItemsDB itemsDB;
    private static CategoryDB categoryDB;
    private static PaymentMethodDB paymentMethodDB;
    private static User currentUser;

    // initialise blueprints with a single connection
    public static void initialize(Connection conn) {
        connection = conn;
        userDB = new UserDB(connection);
        productDB = new ProductDB(connection);
        transactionsDB = new TransactionsDB(connection);
        itemsDB = new TransactionItemsDB(connection);
        categoryDB = new CategoryDB(connection);
        paymentMethodDB = new PaymentMethodDB(connection);
    }

    public static Connection getConnection() {
        return connection;
    }

    // Getters for blueprints (so controllers can access them)
    public static UserDB getUserDB() { return userDB; }
    public static ProductDB getProductDB() { return productDB; }
    public static TransactionsDB getTransactionsDB() { return transactionsDB; }
    public static TransactionItemsDB getTransactionItemsDB() { return itemsDB; }
    public static CategoryDB getCategoryDB() { return categoryDB; }
    public static PaymentMethodDB getPaymentMethodDB() { return paymentMethodDB; }

    // User session data
    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void clearCurrentUser() { currentUser = null; }
}