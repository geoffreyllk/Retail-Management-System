package com.example.coursework.models;

public class TransactionItems {
    private int id;
    private String transactionId;
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    // Constructor for loading from database (all fields known)
    public TransactionItems(int id, String transactionId, String productId, String productName, int quantity, double price, double subtotal) {
        // Only validate transactionId for database records (not empty)
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Constructor for creating new cart item (without transactionId yet)
    public TransactionItems(String productId, String productName, int quantity, double price) {
        this.id = 0;
        this.transactionId = "";  // Empty temporarily
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = quantity * price;
    }

    // Setter for transactionId (called by Transactions after generating ID)
    public void setTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        this.transactionId = transactionId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Formatters
    public String getFormattedPrice() {
        return String.format("%.2f", price);
    }

    public String getFormattedSubtotal() {
        return String.format("%.2f", subtotal);
    }
}