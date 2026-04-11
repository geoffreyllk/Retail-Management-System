package com.example.coursework.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;

    // constructor
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // getters
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }

    // setters (return bool to allow caller to decide, loop until validated)
    public boolean setPassword(String password) {
        if (password.length() >= 8) { // minimum 8 char
            this.password = password;
            return true;
        }
        return false;
    }

    public boolean setRole(String role) {
        if (!role.trim().isEmpty()) {
            // compare uppercase strings
            String text = role.trim().toUpperCase();
            if (text.equals("ADMIN") || text.equals("CASHIER")) {
                this.role = text;
                return true;
            }
        }
        return false;
    }
}