package com.example.coursework.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDB extends dbHelper {

    public PaymentMethodDB(Connection connection) {
        super(connection);
    }

    public List<String> getAllPaymentMethods() {
        List<String> methods = new ArrayList<>();
        String sql = "SELECT name FROM payment_methods ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                methods.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get payment methods: " + e.getMessage());
        }
        return methods;
    }

    public boolean addPaymentMethod(String name) {
        String sql = "INSERT INTO payment_methods VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add payment method: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePaymentMethod(String name) {
        String sql = "DELETE FROM payment_methods WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to delete payment method: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePaymentMethod(String oldName, String newName) {
        String sql = "UPDATE payment_methods SET name = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update payment method: " + e.getMessage());
            return false;
        }
    }
}