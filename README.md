# Retail Management System

A complete desktop application for managing retail store operations, including sales processing, inventory management, transaction tracking, and reporting.

## How to Run

1. Open project in IDE
2. Run `Main.java`
3. Click **"Insert Sample Data"** button on login screen (first time only)
4. Login with credentials below

## Sample Login Credentials

**Admin**
- Username: admin
- Password: admin123

**Cashier**
- Username: cashier
- Password: cashier123

## Configuration

Edit `config.properties` in project root 
or, login as admin and go to settings page.

## Project Structure

### Admin
- **Dashboard** - Overview of daily performance metrics and low stock alerts
- **Inventory** - View and manage all products (Create, Read, Update, Delete with images)
- **Sales** - Search and filter transaction history
- **Users** - View and manage all cashier accounts (CRUD)
- **Reports** - Analytical graphs and charts on store performance
- **Settings** - Configure store details (name, currency, tax rate, categories, payment methods)

### Cashier
- **Dashboard** - Process customer sales, manage cart, accept payments, and print receipts

> **Note:** To edit or delete items (e.g., products, categories), click on the table row of the item to open  the edit overlay.