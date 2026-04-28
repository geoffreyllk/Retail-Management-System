package com.example.coursework.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transactions {
    private final String transactionId;
    private final String cashierUsername;
    private final LocalDateTime transactionDate;
    private final double totalAmount;
    private final String paymentMethod;
    private final List<TransactionItems> items;

    // Main constructor for loading from database
    public Transactions(String transactionId, String cashierUsername, LocalDateTime transactionDate, double totalAmount, String paymentMethod, List<TransactionItems> items) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        if (cashierUsername == null || cashierUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Cashier username cannot be empty");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be empty");
        }

        this.transactionId = transactionId;
        this.cashierUsername = cashierUsername;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    // Constructor for new transaction - generates ID and sets it on items
    public Transactions(String cashierUsername, String paymentMethod, List<TransactionItems> items, double totalAmount) {
        this(generateTransactionId(), cashierUsername, LocalDateTime.now(), totalAmount, paymentMethod, items);

        // Set the transactionId on all items
        for (TransactionItems item : this.items) {
            item.setTransactionId(this.transactionId);
        }
    }

    // Generate unique transaction id
    private static String generateTransactionId() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomString = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "T" + dateStr + "-" + randomString;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getCashierUsername() {
        return cashierUsername;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getFormattedTransactionDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return transactionDate.format(formatter);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public List<TransactionItems> getItems() {
        return new ArrayList<>(items);
    }

    public String getFormattedTotalAmount() {
        return String.format("%.2f", totalAmount);
    }
}