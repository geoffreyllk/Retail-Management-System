package com.example.coursework.database;

import com.example.coursework.models.TransactionItems;
import com.example.coursework.models.Transactions;
import com.example.coursework.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDB extends dbHelper{
    public UserDB(Connection connection) {
        super(connection);  // super dbHelper constructor and pass connection to store
    }

    // CRUD Operations

    // CREATE a single user (insert)
    public boolean createUser(User user) {
        String sqlQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(sqlQuery)){
            // get object values and insert into prepared statement index 1 to 3
            pstatement.setString(1, user.getUsername());
            pstatement.setString(2, user.getPassword());
            pstatement.setString(3, user.getRole());

            // returns no. of rows affected
            int inserted = pstatement.executeUpdate();
            return inserted > 0; // false if nothing was inserted
        } catch (SQLException e) {
            System.out.println("Failed to create user: " + e.getMessage());
            return false;
        }
    }

    // READ users
    private List<User> getUsers(String query, String... condition) {
        List<User> users = new ArrayList<>();
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
                    users.add(new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    ));
                }
            }
        }  catch (SQLException e) {
            System.out.println("Failed to display user(s): " + e.getMessage());
        }

        return users; // return list of user objects
    }

    // return all users
    public List<User> getAllUsers() {
        String query = ("SELECT * FROM users ORDER BY username");
        return getUsers(query, null);
    }

    // return a user by username
    public User getUser(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        List<User> users = getUsers(query, username); // getUsers returns list of users with username

        if (!users.isEmpty()) {
            return users.getFirst(); // return first user found in list
        }
        return null;
    }


    // Get total user count
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    // Get total admin count
    public int getTotalAdminCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role='admin'";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    // Search users with filters (for table)
    public List<User> searchUsers(String searchText, String role) {
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (searchText != null && !searchText.trim().isEmpty()) {
            query.append(" AND username LIKE ?");
            params.add("%" + searchText + "%");
        }

        if (role != null && !role.equals("All")) {
            query.append(" AND role = ?");
            params.add(role);
        }

        query.append(" ORDER BY id");

        return getUsers(query.toString(), params.toArray(new String[0]));
    }

    // Update user
    // Update only role
    public boolean updateUserRole(int id, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update role: " + e.getMessage());
            return false;
        }
    }

    // Update only password
    public boolean updateUserPassword(int id, String password) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, password);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update password: " + e.getMessage());
            return false;
        }
    }

    // Delete user
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
            return false;
        }
    }
}
