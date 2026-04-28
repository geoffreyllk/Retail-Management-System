package com.example.coursework.controllers.cashier;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.ProductDB;
import com.example.coursework.database.TransactionsDB;
import com.example.coursework.database.TransactionItemsDB;
import com.example.coursework.models.Product;
import com.example.coursework.models.TransactionItems;
import com.example.coursework.models.Transactions;
import com.example.coursework.models.User;
import com.example.coursework.utils.AlertUtil;
import com.example.coursework.utils.ConfigManager;
import com.example.coursework.utils.ImageHelper;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CashierDashboardController {

    @FXML private VBox mainContent;
    @FXML private Label storeNameLabel;
    @FXML private Label cashierNameLabel;
    @FXML private TextField searchField;
    @FXML private HBox categoryContainer;
    @FXML private GridPane productsGrid;

    @FXML private Label currentDateLabel;
    @FXML private Label currentTimeLabel;
    @FXML private VBox cartContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxText;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private VBox receiptOverlay;
    @FXML private TextArea receiptTextArea;

    private String currentReceiptText = "";

    private final ProductDB productDB = AppCache.getProductDB();
    private final TransactionsDB transactionsDB = AppCache.getTransactionsDB();
    private final TransactionItemsDB transactionItemsDB = AppCache.getTransactionItemsDB();
    private User currentUser;

    private List<Product> allProducts = new ArrayList<>();
    private Map<Product, Integer> cart = new HashMap<>();
    private final String currency = ConfigManager.getCurrency();
    private final double taxRate = ConfigManager.getTaxRate();
    private final boolean taxIncluded = ConfigManager.isTaxIncluded();
    private String selectedCategory = "All Categories";
    private Button activeCategoryButton;

    @FXML
    public void initialize() {
        storeNameLabel.setText(ConfigManager.getStoreName());

        currentUser = AppCache.getCurrentUser();
        cashierNameLabel.setText("Welcome back, " + currentUser.getUsername() + "!");

        setupCategorySlider();
        loadProducts();
        updateCartDisplay();

        // real time search on input
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts());

        searchField.setFocusTraversable(false); // unfocus search bar

        // datetime
        currentDateLabel.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")));
        currentTimeLabel.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));

        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    currentTimeLabel.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();

    }

    private void setupCategorySlider() {
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.addAll(AppCache.getCategoryDB().getAllCategories());

        categoryContainer.getChildren().clear();

        // enhanced loop go through all categories
        for (String category : categories) {
            int productCount;

            if (category.equals("All Categories")) {
                productCount = productDB.getTotalProductCount();
            } else {
                productCount = productDB.getProductCountByCategory(category);
            }

            // create category buttons
            Button categoryBtn = new Button(category + "\n" + productCount + " items");
            categoryBtn.getStyleClass().add("category-btn");
            categoryBtn.setWrapText(true);
            categoryBtn.setAlignment(Pos.CENTER);
            categoryBtn.setPrefWidth(120);

            if (category.equals(selectedCategory)) {
                categoryBtn.getStyleClass().add("category-btn-active");
                activeCategoryButton = categoryBtn;
            }

            categoryBtn.setOnAction(e -> {
                selectedCategory = category;

                // remove active
                if (activeCategoryButton != null) {
                    activeCategoryButton.getStyleClass().remove("category-btn-active");
                }
                // set new active
                categoryBtn.getStyleClass().add("category-btn-active");
                activeCategoryButton = categoryBtn;
                // filter category
                filterProducts();
            });

            categoryContainer.getChildren().add(categoryBtn);
        }
    }

    private void loadProducts() {
        allProducts = productDB.getProducts();
        filterProducts();
    }

    // filter by search and category
    private void filterProducts() {
        String search = searchField.getText().toLowerCase();

        // loop through all products and add to array if match search and category
        List<Product> filteredArray = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchesSearch = search.isEmpty() || p.getName().toLowerCase().contains(search) || p.getProductId().toLowerCase().contains(search);
            boolean matchesCategory = selectedCategory.equals("All Categories") || p.getCategory().equals(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredArray.add(p);
            }
        }

        // display all filtered products in product grid
        displayProducts(filteredArray);
    }

    private void displayProducts(List<Product> products) {
        productsGrid.getChildren().clear();

        int row = 0;
        int col = 0;
        int itemsPerRow = 3;

        for (Product product : products) {
            VBox card = createProductCard(product);
            productsGrid.add(card, col, row);

            col++;
            if (col >= itemsPerRow) {
                col = 0;
                row++;
            }
        }
    }

    // create product cards
    private VBox createProductCard(Product product) {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");

        // Load product image
        ImageView productImage = ImageHelper.loadProductImage(product.getName(), product.getCategory(), 100,  100);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("%s%.2f", currency, product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        card.getChildren().addAll(productImage, nameLabel, priceLabel);
        card.setAlignment(Pos.CENTER);

        // if no stock, disable
        if ((product.getStock()-cart.getOrDefault(product, 0)) <= 0) {
            card.getStyleClass().add("out-of-stock");
            card.setDisable(true);
        } else {
            // else, add to cart event handler
            card.setOnMouseClicked(e -> addToCart(product));
        }

        return card;
    }

    private void addToCart(Product product) {
        // real time validation
        if ((product.getStock()-cart.getOrDefault(product, 0)) <= 0) {
            AlertUtil.showError("Product out of stock!");
            return;
        }

        cart.put(product, cart.getOrDefault(product, 0) + 1);
        updateCartDisplay();
        filterProducts();
    }

    private void updateCartDisplay() {
        cartContainer.getChildren().clear();

        double subtotal = 0;

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double itemSubtotal = product.getPrice() * quantity;
            subtotal += itemSubtotal;

            HBox itemRow = createCartItemRow(product, quantity, itemSubtotal);
            cartContainer.getChildren().add(itemRow);
        }

        // if tax built into prices, hide tax label
        if (taxIncluded) {
            taxLabel.setVisible(false);
            taxText.setText("Tax Included");
            subtotalLabel.setText(String.format("%s%.2f", currency, subtotal));
            totalLabel.setText(String.format("%s%.2f", currency, subtotal));
        } else {
            double tax = subtotal * taxRate;
            double total = subtotal + tax;

            taxLabel.setVisible(true);
            int taxPercentage = (int)(taxRate * 100);
            taxText.setText("Tax (" + taxPercentage + "%)");
            taxLabel.setText(String.format("%s%.2f", currency, tax));
            subtotalLabel.setText(String.format("%s%.2f", currency, subtotal));
            totalLabel.setText(String.format("%s%.2f", currency, total));
        }
    }

    private HBox createCartItemRow(Product product, int quantity, double itemSubtotal) {
        HBox row = new HBox(10);
        row.getStyleClass().add("cart-item");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("cart-item-name");
        Label priceLabel = new Label(String.format("%s%.2f", currency, product.getPrice()));
        priceLabel.getStyleClass().add("cart-item-price");
        infoBox.getChildren().addAll(nameLabel, priceLabel);

        HBox qtyBox = new HBox(5);
        qtyBox.setAlignment(Pos.CENTER);
        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("quantity-btn");
        minusBtn.setOnAction(e -> updateQuantity(product, quantity - 1));

        Label qtyLabel = new Label(String.valueOf(quantity));
        qtyLabel.getStyleClass().add("cart-item-quantity");

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("quantity-btn");
        plusBtn.setOnAction(e -> updateQuantity(product, quantity + 1));

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        Label subtotalLabel = new Label(String.format("%s%.2f", currency, itemSubtotal));
        subtotalLabel.getStyleClass().add("cart-item-subtotal");

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-btn");
        removeBtn.setOnAction(e -> removeFromCart(product));

        row.getChildren().addAll(infoBox, qtyBox, subtotalLabel, removeBtn);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        return row;
    }

    private void updateQuantity(Product product, int newQuantity) {
        if (newQuantity <= 0) {
            cart.remove(product);
        } else if (newQuantity <= product.getStock()) {
            cart.put(product, newQuantity);
        } else {
            AlertUtil.showError("Not enough stock! Available: " + product.getStock());
        }
        updateCartDisplay();
        filterProducts();
    }

    private void removeFromCart(Product product) {
        cart.remove(product);
        updateCartDisplay();
        filterProducts();
    }

    @FXML
    private void clearOrder() {
        cart.clear();
        updateCartDisplay();
        filterProducts();
    }

    @FXML
    private void checkout() {
        if (cart.isEmpty()) {
            AlertUtil.showError("Cart is empty!");
            return;
        }

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (quantity > product.getStock()) {
                AlertUtil.showError("Not enough stock for " + product.getName() + "! Available: " + product.getStock());
                return;
            }
        }

        // payment dialog
        Optional<String> result = AlertUtil.showPaymentDialog(AppCache.getPaymentMethodDB().getAllPaymentMethods());
        if (!result.isPresent()) {
            return; // User cancelled
        }
        String paymentMethod = result.get();

        List<TransactionItems> items = new ArrayList<>();
        double subtotal = 0;

        // Calculate subtotal from cart items
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double itemSubtotal = product.getPrice() * quantity;
            subtotal += itemSubtotal;

            TransactionItems item = new TransactionItems(product.getProductId(), product.getName(), quantity, product.getPrice());
            items.add(item);
        }

        double total;

        if (taxIncluded) {
            total = subtotal;
        } else {
            total = subtotal + subtotal * taxRate;
        }

        Transactions transaction = new Transactions(currentUser.getUsername(), paymentMethod, items, total);

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            product.setStock(product.getStock() - quantity);
            productDB.updateProduct(product);
        }

        // create transaction -> transaction db creates transaction and transaction items
        if (transactionsDB.createTransaction(transaction)) {
            // print receipt
            printReceipt(items, paymentMethod, subtotal, total);
            cart.clear();
            loadProducts();
            updateCartDisplay();
        } else {
            AlertUtil.showError("Transaction failed!");
        }
    }

    private void showReceiptOverlay(String receiptText) {
        currentReceiptText = receiptText;
        receiptTextArea.setText(receiptText);
        receiptOverlay.setVisible(true);
        receiptOverlay.setManaged(true);
        receiptOverlay.toFront();
        mainContent.setStyle("-fx-opacity: 0.3;");
        mainContent.setDisable(true);
    }

    @FXML
    private void closeReceipt() {
        receiptOverlay.setVisible(false);
        receiptOverlay.setManaged(false);
        mainContent.setStyle("");
        mainContent.setDisable(false);
    }

    @FXML
    private void printReceipt() {
        // just show a message
        AlertUtil.showInfo(currentReceiptText);
        closeReceipt();
    }

    private void printReceipt(List<TransactionItems> items, String paymentMethod, double subtotal, double total) {
        double tax = total - subtotal;

        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("           RECEIPT\n");
        receipt.append("================================\n");
        receipt.append("Date: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        receipt.append("Cashier: ").append(currentUser.getUsername()).append("\n");
        receipt.append("--------------------------------\n");

        for (TransactionItems item : items) {
            receipt.append(String.format("%s x%d = %s%.2f\n", item.getProductName(), item.getQuantity(), currency, item.getSubtotal()));
        }

        receipt.append("--------------------------------\n");
        receipt.append(String.format("Subtotal: %s%.2f\n", currency, subtotal));
        receipt.append(String.format("Tax (%d%%): %s%.2f\n", (int)(taxRate * 100), currency, tax));
        receipt.append(String.format("TOTAL: %s%.2f\n", currency, total));
        receipt.append("Payment Method: ").append(paymentMethod).append("\n");

        if (taxIncluded) {
            receipt.append("(Tax included in prices)\n");
        }

        receipt.append("================================\n");
        receipt.append("      Thank you for shopping!\n");
        receipt.append("================================\n");

        // Show receipt in overlay
        showReceiptOverlay(receipt.toString());
    }

    @FXML
    void logout(ActionEvent event) {
        AppCache.clearCurrentUser();

        if (AlertUtil.showConfirmation("Logout","Are you sure you want to logout?", "You will be redirected to the login screen.")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coursework/fxml/LoginScreen.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Login | Retail Management System");
                stage.setMinWidth(400);
                stage.setMinHeight(300);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                AlertUtil.showError("Failed to load login screen");
            }
        }
    }
}