package com.example.coursework.database;

import com.example.coursework.models.Product;
import com.example.coursework.models.User;
import com.example.coursework.utils.ConfigManager;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDB extends dbHelper {
    public ProductDB(Connection connection) {
        super(connection);  // super dbHelper constructor and pass connection to store
    }
    // CRUD Operations

    // CREATE a single product
    public boolean createProduct(Product product) {
        String sqlQuery = "INSERT INTO products (product_id, name, category, price, stock) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(sqlQuery);){
            // get object values and insert into prepared statement index 1 to 5
            pstatement.setString(1, product.getProductId());
            pstatement.setString(2, product.getName());
            pstatement.setString(3, product.getCategory());
            pstatement.setDouble(4, product.getPrice());
            pstatement.setInt(5, product.getStock());

            // returns no. of rows affected
            int inserted = pstatement.executeUpdate();
            return inserted > 0; // false if nothing was inserted
        } catch (SQLException e) {
            System.out.println("Failed to create product: " + e.getMessage());
            return false;
        }
    }

    // READ products
    private List<Product> getProducts(String query, String ... condition) {
        List<Product> products = new ArrayList<>();
        // try with resource to close()
        try (PreparedStatement statement = connection.prepareStatement(query)){
            // only add parameters condition if condition not null
            if (condition != null) {
                for (int i = 0; i < condition.length; i++) {
                    statement.setString(i + 1, condition[i]);
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(new Product(
                            resultSet.getString("product_id"),
                            resultSet.getString("name"),
                            resultSet.getString("category"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("stock")
                    ));
                }
            }
        }  catch (SQLException e) {
            System.out.println("Failed to display product(s): " + e.getMessage());
        }
        return products; // return list of product objects
    }

    // return all products
    public List<Product> getProducts() {
        String query = "SELECT * FROM products ORDER BY name";
        return getProducts(query, null);
    }

    // get product count
    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getProductCountByCategory(String category) {
        String sql = "SELECT COUNT(*) FROM products WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get product count for category: " + e.getMessage());
        }
        return 0;
    }

    // get low stock (limit 3 for dashboard)
    public List<Product> getLowStockProducts() {
        int threshold = ConfigManager.getLowStockThreshold();
        String sql = "SELECT * FROM products WHERE stock >= 0 AND stock < ? ORDER BY stock ASC LIMIT 3";
        return getProducts(sql, String.valueOf(threshold));
    }

    // Search products with filters
    public List<Product> searchProducts(String searchText, String category, String stockStatus) {
        StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (searchText != null && !searchText.trim().isEmpty()) {
            query.append(" AND (product_id LIKE ? OR name LIKE ?)");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }

        if (category != null && !category.equals("All")) {
            query.append(" AND category = ?");
            params.add(category);
        }

        if (stockStatus != null && !stockStatus.equals("All")) {
            switch (stockStatus) {
                case "Low Stock":
                    query.append(" AND stock > 0 AND stock < 50");
                    break;
                case "Out of Stock":
                    query.append(" AND stock = 0");
                    break;
                case "In Stock":
                    query.append(" AND stock > 0");
                    break;
            }
        }

        query.append(" ORDER BY name");

        return getProducts(query.toString(), params.toArray(new String[0]));
    }

    // Update product
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.setString(5, product.getProductId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update product: " + e.getMessage());
            return false;
        }
    }

    // Update products category when category is deleted
    public void updateProductsCategory(String oldCategory, String newCategory) {
        String sql = "UPDATE products SET category = ? WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newCategory);
            pstmt.setString(2, oldCategory);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update products category: " + e.getMessage());
        }
    }

    // Delete product
    public boolean deleteProduct(String productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete product: " + e.getMessage());
            return false;
        }
    }
}