package com.example.coursework.database;

import java.sql.*;

public class dbHelper {
    protected Connection connection;

    // dbHelper constructor receives and stores connection from child classes
    public dbHelper(Connection connection) {
        this.connection = connection;
    }

    // for development drop db
    public void dropAndRedo() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS transaction_items");
            statement.executeUpdate("DROP TABLE IF EXISTS transactions");
            statement.executeUpdate("DROP TABLE IF EXISTS products");
            statement.executeUpdate("DROP TABLE IF EXISTS users");

            initDatabase();
            insertSampleTransactionsApril();
            // insertSampleData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -- init --
    public void initDatabase() {
        try (Statement statement = connection.createStatement()) {
            // all users for login, either admin or cashier role
            String createUsers = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, role TEXT NOT NULL)";
            statement.executeUpdate(createUsers);

            // all products in stock
            String createProducts = "CREATE TABLE IF NOT EXISTS products (product_id TEXT PRIMARY KEY, name TEXT NOT NULL, category TEXT NOT NULL, price REAL NOT NULL, stock INTEGER NOT NULL)";
            statement.executeUpdate(createProducts);

            // transaction details
            String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (transaction_id TEXT PRIMARY KEY, cashier_username TEXT NOT NULL, transaction_date TEXT, total_amount REAL NOT NULL, payment_method TEXT)";
            statement.executeUpdate(createTransactions);

            // one to many - transactions -> transactionItems , one transaction may have many items bought
            String createTransactionItems = "CREATE TABLE IF NOT EXISTS transaction_items (id INTEGER PRIMARY KEY AUTOINCREMENT, transaction_id TEXT NOT NULL, product_id TEXT NOT NULL, product_name TEXT NOT NULL, quantity INTEGER NOT NULL, price REAL NOT NULL, subtotal REAL NOT NULL, FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id), FOREIGN KEY (product_id) REFERENCES products(product_id))";
            statement.executeUpdate(createTransactionItems);

            // category table
            String createCategories = "CREATE TABLE IF NOT EXISTS categories (name TEXT PRIMARY KEY)";
            statement.executeUpdate(createCategories);

            // payment method table
            String createPaymentMethods = "CREATE TABLE IF NOT EXISTS payment_methods (name TEXT PRIMARY KEY)";
            statement.executeUpdate(createPaymentMethods);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // sample data
    public void insertSampleData() {
        try {
            insertSampleCategories();
            insertSamplePaymentMethods();
            insertSampleUsers();
            insertSampleProducts();
            insertSampleTransactions();
            insertSampleTransactionItems();

            System.out.println("Sample data inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting sample data: " + e.getMessage());
        }
    }

    private void insertSampleCategories() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT OR IGNORE INTO categories VALUES ('Beverages'), ('Snacks'), ('Candy'), ('Food'), ('Uncategorized')");
            System.out.println("Categories inserted.");
        }
    }

    private void insertSamplePaymentMethods() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT OR IGNORE INTO payment_methods VALUES ('Cash'), ('Card'), ('QR Pay')");
            System.out.println("Payment methods inserted.");
        }
    }

    private void insertSampleUsers() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES " +
                    "('admin', 'admin123', 'admin'), " +
                    "('cashier', 'cashier123', 'cashier'), " +
                    "('john', 'cashier123', 'cashier'), " +
                    "('jane', 'cashier123', 'cashier'), " +
                    "('bob', 'cashier123', 'cashier'), " +
                    "('alice', 'cashier123', 'cashier')");
            System.out.println("Users inserted.");
        }
    }

    private void insertSampleProducts() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO products VALUES " +
                    "('P001', 'Coca Cola', 'Beverages', 3.50, 80), " +
                    "('P002', 'Pepsi', 'Beverages', 3.50, 85), " +
                    "('P003', 'Mineral Water', 'Beverages', 1.50, 100), " +
                    "('P004', 'Orange Juice', 'Beverages', 4.00, 4), " +
                    "('P005', \"Lay's Chips\", 'Snacks', 4.50, 5), " +
                    "('P006', 'Cheetos', 'Snacks', 3.80, 90), " +
                    "('P007', 'Snickers', 'Candy', 2.50, 9), " +
                    "('P008', 'Kit Kat', 'Candy', 2.50, 30), " +
                    "('P009', 'Instant Noodles', 'Food', 2.20, 35), " +
                    "('P010', 'Canned Tuna', 'Food', 5.50, 50)");
            System.out.println("Products Inserted.");
        }
    }

    private void insertSampleTransactions() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // transactions (2024-2026)
            statement.executeUpdate("INSERT INTO transactions (transaction_id, cashier_username, transaction_date, total_amount, payment_method) VALUES " +
                    "('TXN001', 'john', '2024-05-15 10:30:00', 15.50, 'Cash'), " + "('TXN002', 'jane', '2024-06-20 14:15:00', 23.00, 'Card'), " + "('TXN003', 'bob', '2024-07-10 09:45:00', 67.80, 'QR Pay'), " + "('TXN004', 'alice', '2024-08-05 16:20:00', 34.50, 'Cash'), " +
                    "('TXN005', 'john', '2024-09-12 11:00:00', 42.00, 'Card'), " + "('TXN006', 'jane', '2024-10-18 13:30:00', 28.50, 'QR Pay'), " + "('TXN007', 'bob', '2024-11-22 15:45:00', 56.00, 'Cash'), " +
                    "('TXN008', 'alice', '2024-12-03 10:15:00', 19.50, 'Card'), " + "('TXN009', 'john', '2025-01-14 12:00:00', 73.50, 'QR Pay'), " + "('TXN010', 'jane', '2025-02-20 14:30:00', 31.00, 'Cash'), " +
                    "('TXN011', 'bob', '2025-03-05 09:30:00', 88.00, 'Card'), " + "('TXN012', 'alice', '2025-04-10 16:00:00', 45.00, 'QR Pay'), " + "('TXN013', 'john', '2025-05-25 11:30:00', 52.50, 'Cash'), " +
                    "('TXN014', 'jane', '2025-06-30 13:00:00', 67.00, 'Card'), " + "('TXN015', 'bob', '2025-07-08 15:30:00', 24.50, 'QR Pay'), " + "('TXN016', 'alice', '2025-08-12 10:45:00', 39.00, 'Cash'), " +
                    "('TXN017', 'john', '2025-09-18 12:15:00', 58.50, 'Card'), " + "('TXN018', 'jane', '2025-10-22 14:00:00', 43.00, 'QR Pay'), " + "('TXN019', 'bob', '2025-11-28 16:30:00', 71.50, 'Cash'), " +
                    "('TXN020', 'alice', '2025-12-05 11:00:00', 36.00, 'Card'), " + "('TXN021', 'john', '2026-01-10 13:45:00', 62.00, 'QR Pay'), " +
                    "('TXN022', 'jane', '2026-01-15 10:00:00', 29.50, 'Cash'), " + "('TXN023', 'bob', '2026-01-20 14:30:00', 47.00, 'Card'), " + "('TXN024', 'alice', '2026-01-25 16:15:00', 84.00, 'QR Pay'), " +
                    "('TXN025', 'john', '2026-02-01 09:30:00', 33.50, 'Cash'), " + "('TXN026', 'jane', '2026-02-05 11:45:00', 55.00, 'Card'), " + "('TXN027', 'bob', '2026-02-10 13:00:00', 41.50, 'QR Pay'), " + "('TXN028', 'alice', '2026-02-15 15:30:00', 76.00, 'Cash'), " +
                    "('TXN029', 'john', '2026-02-20 10:15:00', 22.50, 'Card'), " + "('TXN030', 'jane', '2026-02-25 12:00:00', 68.00, 'QR Pay'), " + "('TXN031', 'bob', '2026-03-01 14:45:00', 49.50, 'Cash'), " +
                    "('TXN032', 'alice', '2026-03-03 09:00:00', 37.00, 'Card'), " + "('TXN033', 'john', '2026-03-05 11:30:00', 81.50, 'QR Pay'), " + "('TXN034', 'jane', '2026-03-07 13:15:00', 26.00, 'Cash'), " +
                    "('TXN035', 'bob', '2026-03-09 15:00:00', 59.50, 'Card'), " + "('TXN036', 'alice', '2026-03-11 10:30:00', 44.00, 'QR Pay'), " + "('TXN037', 'john', '2026-03-13 12:45:00', 72.50, 'Cash'), " +
                    "('TXN038', 'jane', '2026-03-15 14:00:00', 38.00, 'Card'), " + "('TXN039', 'bob', '2026-03-17 16:30:00', 65.50, 'QR Pay'), " + "('TXN040', 'alice', '2026-03-19 09:45:00', 51.00, 'Cash'), " +
                    "('TXN041', 'john', '2026-03-21 11:15:00', 28.50, 'Card'), " + "('TXN042', 'jane', '2026-03-23 13:30:00', 87.00, 'QR Pay'), " + "('TXN043', 'bob', '2026-03-25 15:45:00', 42.50, 'Cash'), " +
                    "('TXN044', 'alice', '2026-03-27 10:00:00', 34.00, 'Card'), " + "('TXN045', 'john', '2026-03-29 12:30:00', 77.50, 'QR Pay'), " + "('TXN046', 'jane', '2026-03-31 14:15:00', 53.00, 'Cash'), " +
                    "('TXN047', 'bob', '2026-04-01 09:30:00', 61.50, 'Card'), " + "('TXN048', 'alice', '2026-04-01 13:00:00', 35.00, 'QR Pay'), " + "('TXN049', 'john', '2026-04-02 10:45:00', 48.50, 'Cash'), " +
                    "('TXN050', 'jane', '2026-04-02 15:30:00', 92.00, 'Card'), " + "('TXN051', 'bob', '2026-04-03 11:00:00', 39.50, 'QR Pay'), " + "('TXN052', 'alice', '2026-04-03 16:15:00', 56.00, 'Cash'), " +
                    "('TXN053', 'john', '2026-04-04 09:00:00', 73.00, 'Card'), " + "('TXN054', 'jane', '2026-04-04 12:30:00', 27.50, 'QR Pay'), " + "('TXN055', 'bob', '2026-04-05 14:45:00', 64.00, 'Cash'), " +
                    "('TXN056', 'alice', '2026-04-05 10:15:00', 41.50, 'Card'), " + "('TXN057', 'john', '2026-04-06 13:30:00', 58.00, 'QR Pay'), " + "('TXN058', 'jane', '2026-04-06 15:00:00', 33.50, 'Cash'), " +
                    "('TXN059', 'bob', '2026-04-07 11:45:00', 79.00, 'Card'), " + "('TXN060', 'alice', '2026-04-07 16:30:00', 46.50, 'QR Pay'), " + "('TXN061', 'john', '2026-04-08 09:15:00', 52.00, 'Cash'), " +
                    "('TXN062', 'jane', '2026-04-08 12:00:00', 68.50, 'Card'), " + "('TXN063', 'bob', '2026-04-08 14:30:00', 37.00, 'QR Pay'), " + "('TXN064', 'alice', '2026-04-08 17:00:00', 83.50, 'Cash')");
            System.out.println("Transactions inserted.");
        }
    }

    private void insertSampleTransactionItems() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // transaction items
            statement.executeUpdate("INSERT INTO transaction_items (transaction_id, product_id, product_name, quantity, price, subtotal) VALUES " +
                    "('TXN001', 'P003', 'Mineral Water', 2, 1.50, 3.00), ('TXN001', 'P007', 'Snickers', 5, 2.50, 12.50), " +
                    "('TXN002', 'P009', 'Instant Noodles', 3, 2.20, 6.60), ('TXN002', 'P005', \"Lay's Chips\", 1, 4.50, 4.50), ('TXN002', 'P003', 'Mineral Water', 1, 1.50, 1.50), " + "('TXN003', 'P001', 'Coca Cola', 4, 3.50, 14.00), ('TXN003', 'P008', 'Kit Kat', 6, 2.50, 15.00), ('TXN003', 'P010', 'Canned Tuna', 3, 5.50, 16.50), " + "('TXN004', 'P002', 'Pepsi', 2, 3.50, 7.00), ('TXN004', 'P004', 'Orange Juice', 1, 4.00, 4.00), ('TXN004', 'P006', 'Cheetos', 2, 3.80, 7.60), " +
                    "('TXN005', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), ('TXN005', 'P009', 'Instant Noodles', 3, 2.20, 6.60), ('TXN005', 'P001', 'Coca Cola', 1, 3.50, 3.50), " + "('TXN006', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN006', 'P003', 'Mineral Water', 3, 1.50, 4.50), ('TXN006', 'P010', 'Canned Tuna', 1, 5.50, 5.50), " + "('TXN007', 'P008', 'Kit Kat', 8, 2.50, 20.00), ('TXN007', 'P002', 'Pepsi', 3, 3.50, 10.50), ('TXN007', 'P004', 'Orange Juice', 2, 4.00, 8.00), " +
                    "('TXN008', 'P001', 'Coca Cola', 1, 3.50, 3.50), ('TXN008', 'P006', 'Cheetos', 2, 3.80, 7.60), ('TXN008', 'P003', 'Mineral Water', 1, 1.50, 1.50), " + "('TXN009', 'P009', 'Instant Noodles', 5, 2.20, 11.00), ('TXN009', 'P007', 'Snickers', 7, 2.50, 17.50), ('TXN009', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), " + "('TXN010', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN010', 'P002', 'Pepsi', 2, 3.50, 7.00), ('TXN010', 'P008', 'Kit Kat', 2, 2.50, 5.00), " +
                    "('TXN011', 'P002', 'Pepsi', 4, 3.50, 14.00), ('TXN011', 'P007', 'Snickers', 6, 2.50, 15.00), ('TXN011', 'P009', 'Instant Noodles', 8, 2.20, 17.60), " + "('TXN012', 'P001', 'Coca Cola', 3, 3.50, 10.50), ('TXN012', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), ('TXN012', 'P008', 'Kit Kat', 5, 2.50, 12.50), " + "('TXN013', 'P003', 'Mineral Water', 5, 1.50, 7.50), ('TXN013', 'P010', 'Canned Tuna', 2, 5.50, 11.00), ('TXN013', 'P004', 'Orange Juice', 3, 4.00, 12.00), " +
                    "('TXN014', 'P006', 'Cheetos', 4, 3.80, 15.20), ('TXN014', 'P007', 'Snickers', 8, 2.50, 20.00), ('TXN014', 'P002', 'Pepsi', 2, 3.50, 7.00), " + "('TXN015', 'P009', 'Instant Noodles', 3, 2.20, 6.60), ('TXN015', 'P001', 'Coca Cola', 2, 3.50, 7.00), ('TXN015', 'P003', 'Mineral Water', 2, 1.50, 3.00), " + "('TXN016', 'P008', 'Kit Kat', 4, 2.50, 10.00), ('TXN016', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), ('TXN016', 'P004', 'Orange Juice', 1, 4.00, 4.00), " + "('TXN017', 'P007', 'Snickers', 5, 2.50, 12.50), ('TXN017', 'P010', 'Canned Tuna', 3, 5.50, 16.50), ('TXN017', 'P002', 'Pepsi', 3, 3.50, 10.50), " +
                    "('TXN018', 'P003', 'Mineral Water', 6, 1.50, 9.00), ('TXN018', 'P006', 'Cheetos', 2, 3.80, 7.60), ('TXN018', 'P009', 'Instant Noodles', 4, 2.20, 8.80), " + "('TXN019', 'P001', 'Coca Cola', 5, 3.50, 17.50), ('TXN019', 'P008', 'Kit Kat', 7, 2.50, 17.50), ('TXN019', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), " +
                    "('TXN020', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN020', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN020', 'P003', 'Mineral Water', 3, 1.50, 4.50), " + "('TXN021', 'P002', 'Pepsi', 4, 3.50, 14.00), ('TXN021', 'P009', 'Instant Noodles', 6, 2.20, 13.20), ('TXN021', 'P010', 'Canned Tuna', 2, 5.50, 11.00), " + "('TXN022', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), ('TXN022', 'P001', 'Coca Cola', 2, 3.50, 7.00), ('TXN022', 'P006', 'Cheetos', 1, 3.80, 3.80), " + "('TXN023', 'P008', 'Kit Kat', 5, 2.50, 12.50), ('TXN023', 'P007', 'Snickers', 3, 2.50, 7.50), ('TXN023', 'P003', 'Mineral Water', 4, 1.50, 6.00), " +
                    "('TXN024', 'P004', 'Orange Juice', 4, 4.00, 16.00), ('TXN024', 'P002', 'Pepsi', 3, 3.50, 10.50), ('TXN024', 'P009', 'Instant Noodles', 5, 2.20, 11.00), " + "('TXN025', 'P010', 'Canned Tuna', 1, 5.50, 5.50), ('TXN025', 'P001', 'Coca Cola', 3, 3.50, 10.50), ('TXN025', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), " + "('TXN026', 'P003', 'Mineral Water', 5, 1.50, 7.50), ('TXN026', 'P007', 'Snickers', 6, 2.50, 15.00), ('TXN026', 'P008', 'Kit Kat', 4, 2.50, 10.00), " + "('TXN027', 'P006', 'Cheetos', 3, 3.80, 11.40), ('TXN027', 'P002', 'Pepsi', 2, 3.50, 7.00), ('TXN027', 'P004', 'Orange Juice', 2, 4.00, 8.00), " +
                    "('TXN028', 'P009', 'Instant Noodles', 4, 2.20, 8.80), ('TXN028', 'P001', 'Coca Cola', 3, 3.50, 10.50), ('TXN028', 'P010', 'Canned Tuna', 3, 5.50, 16.50), " + "('TXN029', 'P005', \"Lay's Chips\", 1, 4.50, 4.50), ('TXN029', 'P003', 'Mineral Water', 2, 1.50, 3.00), ('TXN029', 'P007', 'Snickers', 3, 2.50, 7.50), " +
                    "('TXN030', 'P008', 'Kit Kat', 6, 2.50, 15.00), ('TXN030', 'P002', 'Pepsi', 3, 3.50, 10.50), ('TXN030', 'P006', 'Cheetos', 2, 3.80, 7.60), " + "('TXN031', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN031', 'P009', 'Instant Noodles', 3, 2.20, 6.60), ('TXN031', 'P001', 'Coca Cola', 4, 3.50, 14.00), " + "('TXN032', 'P010', 'Canned Tuna', 2, 5.50, 11.00), ('TXN032', 'P003', 'Mineral Water', 3, 1.50, 4.50), ('TXN032', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), " + "('TXN033', 'P007', 'Snickers', 8, 2.50, 20.00), ('TXN033', 'P002', 'Pepsi', 5, 3.50, 17.50), ('TXN033', 'P008', 'Kit Kat', 6, 2.50, 15.00), " +
                    "('TXN034', 'P001', 'Coca Cola', 2, 3.50, 7.00), ('TXN034', 'P006', 'Cheetos', 2, 3.80, 7.60), ('TXN034', 'P004', 'Orange Juice', 1, 4.00, 4.00), " + "('TXN035', 'P009', 'Instant Noodles', 5, 2.20, 11.00), ('TXN035', 'P003', 'Mineral Water', 4, 1.50, 6.00), ('TXN035', 'P010', 'Canned Tuna', 2, 5.50, 11.00), " + "('TXN036', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), ('TXN036', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN036', 'P002', 'Pepsi', 2, 3.50, 7.00), " + "('TXN037', 'P008', 'Kit Kat', 5, 2.50, 12.50), ('TXN037', 'P001', 'Coca Cola', 4, 3.50, 14.00), ('TXN037', 'P006', 'Cheetos', 3, 3.80, 11.40), " +
                    "('TXN038', 'P003', 'Mineral Water', 6, 1.50, 9.00), ('TXN038', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN038', 'P009', 'Instant Noodles', 3, 2.20, 6.60), " + "('TXN039', 'P010', 'Canned Tuna', 3, 5.50, 16.50), ('TXN039', 'P007', 'Snickers', 5, 2.50, 12.50), ('TXN039', 'P002', 'Pepsi', 3, 3.50, 10.50), " + "('TXN040', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), ('TXN040', 'P001', 'Coca Cola', 3, 3.50, 10.50), ('TXN040', 'P008', 'Kit Kat', 4, 2.50, 10.00), " +
                    "('TXN041', 'P003', 'Mineral Water', 3, 1.50, 4.50), ('TXN041', 'P006', 'Cheetos', 1, 3.80, 3.80), ('TXN041', 'P009', 'Instant Noodles', 4, 2.20, 8.80), " + "('TXN042', 'P004', 'Orange Juice', 3, 4.00, 12.00), ('TXN042', 'P007', 'Snickers', 6, 2.50, 15.00), ('TXN042', 'P010', 'Canned Tuna', 3, 5.50, 16.50), " + "('TXN043', 'P002', 'Pepsi', 2, 3.50, 7.00), ('TXN043', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), ('TXN043', 'P001', 'Coca Cola', 3, 3.50, 10.50), " + "('TXN044', 'P008', 'Kit Kat', 4, 2.50, 10.00), ('TXN044', 'P003', 'Mineral Water', 4, 1.50, 6.00), ('TXN044', 'P006', 'Cheetos', 2, 3.80, 7.60), " +
                    "('TXN045', 'P007', 'Snickers', 7, 2.50, 17.50), ('TXN045', 'P009', 'Instant Noodles', 5, 2.20, 11.00), ('TXN045', 'P004', 'Orange Juice', 2, 4.00, 8.00), " + "('TXN046', 'P001', 'Coca Cola', 4, 3.50, 14.00), ('TXN046', 'P010', 'Canned Tuna', 2, 5.50, 11.00), ('TXN046', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), " + "('TXN047', 'P003', 'Mineral Water', 5, 1.50, 7.50), ('TXN047', 'P002', 'Pepsi', 3, 3.50, 10.50), ('TXN047', 'P008', 'Kit Kat', 6, 2.50, 15.00), " + "('TXN048', 'P006', 'Cheetos', 3, 3.80, 11.40), ('TXN048', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN048', 'P009', 'Instant Noodles', 3, 2.20, 6.60), " +
                    "('TXN049', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN049', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), ('TXN049', 'P001', 'Coca Cola', 3, 3.50, 10.50), " + "('TXN050', 'P010', 'Canned Tuna', 4, 5.50, 22.00), ('TXN050', 'P003', 'Mineral Water', 6, 1.50, 9.00), ('TXN050', 'P002', 'Pepsi', 4, 3.50, 14.00), " + "('TXN051', 'P008', 'Kit Kat', 5, 2.50, 12.50), ('TXN051', 'P007', 'Snickers', 3, 2.50, 7.50), ('TXN051', 'P006', 'Cheetos', 2, 3.80, 7.60), " + "('TXN052', 'P001', 'Coca Cola', 4, 3.50, 14.00), ('TXN052', 'P009', 'Instant Noodles', 5, 2.20, 11.00), ('TXN052', 'P004', 'Orange Juice', 3, 4.00, 12.00), " +
                    "('TXN053', 'P005', \"Lay's Chips\", 4, 4.50, 18.00), ('TXN053', 'P003', 'Mineral Water', 5, 1.50, 7.50), ('TXN053', 'P010', 'Canned Tuna', 3, 5.50, 16.50), " + "('TXN054', 'P002', 'Pepsi', 2, 3.50, 7.00), ('TXN054', 'P008', 'Kit Kat', 3, 2.50, 7.50), ('TXN054', 'P007', 'Snickers', 2, 2.50, 5.00), " + "('TXN055', 'P006', 'Cheetos', 4, 3.80, 15.20), ('TXN055', 'P001', 'Coca Cola', 5, 3.50, 17.50), ('TXN055', 'P009', 'Instant Noodles', 4, 2.20, 8.80), " +
                    "('TXN056', 'P003', 'Mineral Water', 4, 1.50, 6.00), ('TXN056', 'P004', 'Orange Juice', 3, 4.00, 12.00), ('TXN056', 'P005', \"Lay's Chips\", 2, 4.50, 9.00), " + "('TXN057', 'P010', 'Canned Tuna', 3, 5.50, 16.50), ('TXN057', 'P007', 'Snickers', 5, 2.50, 12.50), ('TXN057', 'P002', 'Pepsi', 4, 3.50, 14.00), " +
                    "('TXN058', 'P008', 'Kit Kat', 4, 2.50, 10.00), ('TXN058', 'P001', 'Coca Cola', 3, 3.50, 10.50), ('TXN058', 'P006', 'Cheetos', 2, 3.80, 7.60), " + "('TXN059', 'P003', 'Mineral Water', 6, 1.50, 9.00), ('TXN059', 'P009', 'Instant Noodles', 7, 2.20, 15.40), ('TXN059', 'P004', 'Orange Juice', 4, 4.00, 16.00), " + "('TXN060', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), ('TXN060', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN060', 'P010', 'Canned Tuna', 2, 5.50, 11.00), " + "('TXN061', 'P002', 'Pepsi', 3, 3.50, 10.50), ('TXN061', 'P008', 'Kit Kat', 5, 2.50, 12.50), ('TXN061', 'P001', 'Coca Cola', 4, 3.50, 14.00), " +
                    "('TXN062', 'P006', 'Cheetos', 3, 3.80, 11.40), ('TXN062', 'P003', 'Mineral Water', 5, 1.50, 7.50), ('TXN062', 'P009', 'Instant Noodles', 6, 2.20, 13.20), " + "('TXN063', 'P007', 'Snickers', 4, 2.50, 10.00), ('TXN063', 'P004', 'Orange Juice', 2, 4.00, 8.00), ('TXN063', 'P005', \"Lay's Chips\", 3, 4.50, 13.50), " + "('TXN064', 'P010', 'Canned Tuna', 5, 5.50, 27.50), ('TXN064', 'P001', 'Coca Cola', 6, 3.50, 21.00), ('TXN064', 'P002', 'Pepsi', 4, 3.50, 14.00)");
            System.out.println("Transaction Items inserted.");
        }
    }

    private void insertSampleTransactionsApril() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Transactions for April 22-27, 2026 (total ~RM950)
            statement.executeUpdate("INSERT INTO transactions (transaction_id, cashier_username, transaction_date, total_amount, payment_method) VALUES \n" +
                    "('TXN065', 'john', '2026-04-22 10:30:00', 87.50, 'Cash'),\n" +
                    "('TXN066', 'jane', '2026-04-22 14:15:00', 124.80, 'Card'),\n" +
                    "('TXN067', 'bob', '2026-04-22 17:45:00', 62.30, 'QR Pay'),\n" +
                    "('TXN068', 'alice', '2026-04-23 09:30:00', 198.40, 'Cash'),\n" +
                    "('TXN069', 'john', '2026-04-23 13:20:00', 55.90, 'Card'),\n" +
                    "('TXN070', 'jane', '2026-04-23 16:50:00', 143.20, 'QR Pay'),\n" +
                    "('TXN071', 'bob', '2026-04-24 11:00:00', 76.50, 'Cash'),\n" +
                    "('TXN072', 'alice', '2026-04-24 15:30:00', 212.00, 'Card'),\n" +
                    "('TXN073', 'john', '2026-04-24 18:15:00', 49.80, 'QR Pay'),\n" +
                    "('TXN074', 'jane', '2026-04-25 08:45:00', 167.30, 'Cash'),\n" +
                    "('TXN075', 'bob', '2026-04-25 12:30:00', 91.20, 'Card'),\n" +
                    "('TXN076', 'alice', '2026-04-25 16:00:00', 58.40, 'QR Pay'),\n" +
                    "('TXN077', 'john', '2026-04-26 10:15:00', 185.60, 'Cash'),\n" +
                    "('TXN078', 'jane', '2026-04-26 14:45:00', 73.90, 'Card'),\n" +
                    "('TXN079', 'bob', '2026-04-26 17:30:00', 136.50, 'QR Pay'),\n" +
                    "('TXN080', 'alice', '2026-04-27 09:00:00', 94.20, 'Cash'),\n" +
                    "('TXN081', 'john', '2026-04-27 13:15:00', 158.80, 'Card'),\n" +
                    "('TXN082', 'jane', '2026-04-27 16:40:00', 67.50, 'QR Pay');");

            // Transaction items
            statement.executeUpdate("INSERT INTO transaction_items (transaction_id, product_id, product_name, quantity, price, subtotal) VALUES \n" +
                    "('TXN065', 'P001', 'Coca Cola', 5, 3.50, 17.50),\n" +
                    "('TXN065', 'P007', 'Snickers', 8, 2.50, 20.00),\n" +
                    "('TXN065', 'P005', 'Lay\\'s Chips', 4, 4.50, 18.00),\n" +
                    "('TXN065', 'P003', 'Mineral Water', 8, 1.50, 12.00),\n" +
                    "('TXN065', 'P010', 'Canned Tuna', 2, 5.50, 11.00),\n" +
                    "('TXN066', 'P002', 'Pepsi', 6, 3.50, 21.00),\n" +
                    "('TXN066', 'P009', 'Instant Noodles', 12, 2.20, 26.40),\n" +
                    "('TXN066', 'P008', 'Kit Kat', 10, 2.50, 25.00),\n" +
                    "('TXN066', 'P004', 'Orange Juice', 5, 4.00, 20.00),\n" +
                    "('TXN066', 'P003', 'Mineral Water', 15, 1.50, 22.50),\n" +
                    "('TXN067', 'P006', 'Cheetos', 7, 3.80, 26.60),\n" +
                    "('TXN067', 'P007', 'Snickers', 6, 2.50, 15.00),\n" +
                    "('TXN067', 'P001', 'Coca Cola', 4, 3.50, 14.00),\n" +
                    "('TXN067', 'P003', 'Mineral Water', 3, 1.50, 4.50),\n" +
                    "('TXN068', 'P005', 'Lay\\'s Chips', 12, 4.50, 54.00),\n" +
                    "('TXN068', 'P010', 'Canned Tuna', 8, 5.50, 44.00),\n" +
                    "('TXN068', 'P007', 'Snickers', 15, 2.50, 37.50),\n" +
                    "('TXN068', 'P002', 'Pepsi', 10, 3.50, 35.00),\n" +
                    "('TXN068', 'P004', 'Orange Juice', 7, 4.00, 28.00),\n" +
                    "('TXN069', 'P003', 'Mineral Water', 12, 1.50, 18.00),\n" +
                    "('TXN069', 'P008', 'Kit Kat', 8, 2.50, 20.00),\n" +
                    "('TXN069', 'P009', 'Instant Noodles', 6, 2.20, 13.20),\n" +
                    "('TXN069', 'P001', 'Coca Cola', 1, 3.50, 3.50),\n" +
                    "('TXN070', 'P007', 'Snickers', 14, 2.50, 35.00),\n" +
                    "('TXN070', 'P005', 'Lay\\'s Chips', 9, 4.50, 40.50),\n" +
                    "('TXN070', 'P010', 'Canned Tuna', 6, 5.50, 33.00),\n" +
                    "('TXN070', 'P006', 'Cheetos', 5, 3.80, 19.00),\n" +
                    "('TXN070', 'P003', 'Mineral Water', 10, 1.50, 15.00),\n" +
                    "('TXN071', 'P002', 'Pepsi', 6, 3.50, 21.00),\n" +
                    "('TXN071', 'P004', 'Orange Juice', 4, 4.00, 16.00),\n" +
                    "('TXN071', 'P009', 'Instant Noodles', 10, 2.20, 22.00),\n" +
                    "('TXN071', 'P008', 'Kit Kat', 5, 2.50, 12.50),\n" +
                    "('TXN072', 'P001', 'Coca Cola', 12, 3.50, 42.00),\n" +
                    "('TXN072', 'P005', 'Lay\\'s Chips', 14, 4.50, 63.00),\n" +
                    "('TXN072', 'P007', 'Snickers', 18, 2.50, 45.00),\n" +
                    "('TXN072', 'P010', 'Canned Tuna', 9, 5.50, 49.50),\n" +
                    "('TXN072', 'P003', 'Mineral Water', 8, 1.50, 12.00),\n" +
                    "('TXN073', 'P006', 'Cheetos', 6, 3.80, 22.80),\n" +
                    "('TXN073', 'P009', 'Instant Noodles', 8, 2.20, 17.60),\n" +
                    "('TXN073', 'P003', 'Mineral Water', 5, 1.50, 7.50),\n" +
                    "('TXN074', 'P008', 'Kit Kat', 16, 2.50, 40.00),\n" +
                    "('TXN074', 'P005', 'Lay\\'s Chips', 10, 4.50, 45.00),\n" +
                    "('TXN074', 'P002', 'Pepsi', 12, 3.50, 42.00),\n" +
                    "('TXN074', 'P004', 'Orange Juice', 6, 4.00, 24.00),\n" +
                    "('TXN074', 'P003', 'Mineral Water', 10, 1.50, 15.00),\n" +
                    "('TXN075', 'P001', 'Coca Cola', 7, 3.50, 24.50),\n" +
                    "('TXN075', 'P007', 'Snickers', 9, 2.50, 22.50),\n" +
                    "('TXN075', 'P009', 'Instant Noodles', 14, 2.20, 30.80),\n" +
                    "('TXN075', 'P010', 'Canned Tuna', 2, 5.50, 11.00),\n" +
                    "('TXN076', 'P003', 'Mineral Water', 15, 1.50, 22.50),\n" +
                    "('TXN076', 'P006', 'Cheetos', 5, 3.80, 19.00),\n" +
                    "('TXN076', 'P008', 'Kit Kat', 6, 2.50, 15.00),\n" +
                    "('TXN077', 'P005', 'Lay\\'s Chips', 13, 4.50, 58.50),\n" +
                    "('TXN077', 'P002', 'Pepsi', 15, 3.50, 52.50),\n" +
                    "('TXN077', 'P007', 'Snickers', 12, 2.50, 30.00),\n" +
                    "('TXN077', 'P004', 'Orange Juice', 8, 4.00, 32.00),\n" +
                    "('TXN077', 'P003', 'Mineral Water', 8, 1.50, 12.00),\n" +
                    "('TXN078', 'P009', 'Instant Noodles', 10, 2.20, 22.00),\n" +
                    "('TXN078', 'P001', 'Coca Cola', 5, 3.50, 17.50),\n" +
                    "('TXN078', 'P006', 'Cheetos', 6, 3.80, 22.80),\n" +
                    "('TXN078', 'P008', 'Kit Kat', 4, 2.50, 10.00),\n" +
                    "('TXN079', 'P010', 'Canned Tuna', 7, 5.50, 38.50),\n" +
                    "('TXN079', 'P007', 'Snickers', 10, 2.50, 25.00),\n" +
                    "('TXN079', 'P005', 'Lay\\'s Chips', 8, 4.50, 36.00),\n" +
                    "('TXN079', 'P002', 'Pepsi', 6, 3.50, 21.00),\n" +
                    "('TXN079', 'P003', 'Mineral Water', 10, 1.50, 15.00),\n" +
                    "('TXN080', 'P001', 'Coca Cola', 8, 3.50, 28.00),\n" +
                    "('TXN080', 'P008', 'Kit Kat', 10, 2.50, 25.00),\n" +
                    "('TXN080', 'P009', 'Instant Noodles', 12, 2.20, 26.40),\n" +
                    "('TXN080', 'P004', 'Orange Juice', 3, 4.00, 12.00),\n" +
                    "('TXN081', 'P005', 'Lay\\'s Chips', 11, 4.50, 49.50),\n" +
                    "('TXN081', 'P010', 'Canned Tuna', 9, 5.50, 49.50),\n" +
                    "('TXN081', 'P007', 'Snickers', 13, 2.50, 32.50),\n" +
                    "('TXN081', 'P003', 'Mineral Water', 12, 1.50, 18.00),\n" +
                    "('TXN082', 'P002', 'Pepsi', 6, 3.50, 21.00),\n" +
                    "('TXN082', 'P006', 'Cheetos', 5, 3.80, 19.00),\n" +
                    "('TXN082', 'P009', 'Instant Noodles', 8, 2.20, 17.60),\n" +
                    "('TXN082', 'P003', 'Mineral Water', 6, 1.50, 9.00);");

            System.out.println("Sample transactions and items inserted (April 22-27, 2026)");
        }
    }
}
