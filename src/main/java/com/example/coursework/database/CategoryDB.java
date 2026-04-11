package com.example.coursework.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDB extends dbHelper {

    public CategoryDB(Connection connection) {
        super(connection);
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get categories: " + e.getMessage());
        }
        return categories;
    }

    public boolean addCategory(String name) {
        String sql = "INSERT INTO categories VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(String name) {
        // First update products with this category to 'Uncategorized'
        String updateProducts = "UPDATE products SET category = 'Uncategorized' WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateProducts)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update products: " + e.getMessage());
        }

        // Then delete the category
        String sql = "DELETE FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to delete category: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(String oldName, String newName) {
        // Update category name
        String sql = "UPDATE categories SET name = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            pstmt.executeUpdate();

            // Also update products with this category
            String updateProducts = "UPDATE products SET category = ? WHERE category = ?";
            try (PreparedStatement pstmt2 = connection.prepareStatement(updateProducts)) {
                pstmt2.setString(1, newName);
                pstmt2.setString(2, oldName);
                pstmt2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update category: " + e.getMessage());
            return false;
        }
    }
}