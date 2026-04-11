package com.example.coursework.models;

public class Product {
    private String productId;
    private String name;
    private String category;
    private double price;
    private int stock;

    // constructor
    public Product(String productId, String name, String category, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // getters
    public String getProductId() {
        return productId;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public double getPrice() {
        return price;
    }
    public int getStock() {
        return stock;
    }

    // currency 2 d.p. formatters
    public String getFormattedPrice() {
        return String.format("%.2f", price);
    }

    // setters
    public boolean setName(String name) {
        if (!name.trim().isEmpty()) { // if string empty
            this.name = name;
            return true;
        }
        return false;
    }
    public boolean setPrice(double price) {
        if (price >= 0) { // min 0
            this.price = price;
            return true;
        }
        return false;
    }
    public boolean setCategory(String category) {
        if (!category.trim().isEmpty()) { // if string empty
            this.category = category;
            return true;
        }
        return false;
    }
    public boolean setStock(int stock) {
        if (stock >= 0) { // min 0
            this.stock = stock;
            return true;
        }
        return false;
    }
}