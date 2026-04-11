package com.example.coursework.controllers.admin;

import com.example.coursework.app.AppCache;
import com.example.coursework.database.ProductDB;
import com.example.coursework.models.Product;
import com.example.coursework.utils.AlertUtil;
import com.example.coursework.utils.ConfigManager;
import com.example.coursework.utils.ImageHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class InventoryController {

    @FXML private VBox mainContent;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productIdColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, String> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private Label itemCountLabel;

    @FXML private Label totalStockLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label outOfStockLabel;
    @FXML private Label totalValueLabel;

    @FXML private Label priceLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> stockFilter;

    // Overlay fields
    @FXML private VBox productOverlay;
    @FXML private Label overlayTitle;
    @FXML private Button deleteButton;
    @FXML private TextField editProductId;
    @FXML private TextField editName;
    @FXML private ComboBox<String> editCategory;
    @FXML private TextField editPrice;
    @FXML private TextField editStock;

    @FXML private ImageView productImageView;
    @FXML private Button selectImageButton;

    private File selectedImageFile;
    private String oldProductName;
    private String oldCategory;

    private final ProductDB productDB = AppCache.getProductDB();
    private Product selectedProduct;
    private boolean isEditMode = false;
    private final String currency = ConfigManager.getCurrency();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        loadCategoryDropdowns();
        searchProducts();
        priceValidation();
        stockValidation();

        productTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 1) {
                selectedProduct = productTable.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    showEditOverlay(selectedProduct);
                }
            }
        });
    }

    private void loadCategoryDropdowns() {
        // Get categories from db
        List<String> categories = AppCache.getCategoryDB().getAllCategories();

        List<String> filterCategories = new ArrayList<>();
        filterCategories.add("All");
        filterCategories.addAll(categories);
        categoryFilter.setItems(FXCollections.observableArrayList(filterCategories));
        categoryFilter.setValue("All");

        // overlay edit category dropdown
        editCategory.setItems(FXCollections.observableArrayList(categories));
    }


    private void showOverlay() {
        mainContent.setStyle("-fx-opacity: 0.3;");
        mainContent.setDisable(true);
        productOverlay.setVisible(true);
        productOverlay.setManaged(true);
        productOverlay.toFront();
    }

    @FXML
    private void hideOverlay() {
        mainContent.setStyle("");
        mainContent.setDisable(false);
        productOverlay.setVisible(false);
        productOverlay.setManaged(false);
        productTable.getSelectionModel().clearSelection();
        selectedProduct = null;
    }

    @FXML
    private void showAddOverlay() {
        String currency = ConfigManager.getCurrency();
        isEditMode = false;

        overlayTitle.setText("Add Product");
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        priceLabel.setText("Price (" + currency + "):");

        editProductId.clear();
        editName.clear();
        editCategory.setValue(null);
        editPrice.clear();
        editStock.clear();
        editProductId.setDisable(false);
        selectedProduct = null;

        // Clear image
        selectedImageFile = null;
        productImageView.setImage(null);

        showOverlay();
    }

    private void showEditOverlay(Product product) {
        isEditMode = true;

        overlayTitle.setText("Edit Product");
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        priceLabel.setText("Price (" + currency + "):");

        editProductId.setText(product.getProductId());
        editProductId.setDisable(true);
        editName.setText(product.getName());
        editCategory.setValue(product.getCategory());
        editPrice.setText(product.getFormattedPrice());
        editStock.setText(String.valueOf(product.getStock()));

        // Store old values for image update
        oldProductName = product.getName();
        oldCategory = product.getCategory();

        // Load existing image
        selectedImageFile = null;
        ImageView existingImage = ImageHelper.loadProductImage(product.getName(), product.getCategory(), 80, 80);
        productImageView.setImage(existingImage.getImage());
        if (existingImage.getImage() != null) {
            productImageView.setManaged(true);
        } else {
            productImageView.setManaged(false);
        }

        selectedProduct = product;
        showOverlay();
    }

    @FXML
    private void selectImage() {
        Stage stage = (Stage) mainContent.getScene().getWindow();
        File imageFile = ImageHelper.chooseImage(stage);

        if (imageFile != null) {
            selectedImageFile = imageFile;
            try {
                Image image = new Image(imageFile.toURI().toString());
                productImageView.setImage(image);
                productImageView.setManaged(true);
            } catch (Exception e) {
                selectedImageFile = null;
                productImageView.setManaged(false);
            }
        }
    }

    @FXML
    private void saveProduct() {
        if (editProductId.getText().isEmpty() || editName.getText().isEmpty() ||
                editCategory.getValue() == null || editCategory.getValue().isEmpty() ||
                editPrice.getText().isEmpty() || editStock.getText().isEmpty()) {
            AlertUtil.showError("All fields are required.");
            return;
        }

        try {
            String id = editProductId.getText();
            String name = editName.getText();
            String category = editCategory.getValue();
            double price = Double.parseDouble(editPrice.getText());
            int stock = Integer.parseInt(editStock.getText());

            if (price < 0) {
                AlertUtil.showError("Price cannot be negative!");
                return;
            }

            if (stock < 0) {
                AlertUtil.showError("Stock cannot be negative!");
                return;
            }

            if (isEditMode) {
                if (!name.equals(oldProductName) || !category.equals(oldCategory)) {
                    ImageHelper.updateProductImage(oldProductName, name, oldCategory, category);
                }

                if (selectedImageFile != null) {
                    ImageHelper.saveProductImage(name, category, selectedImageFile);
                }

                Product updatedProduct = new Product(id, name, category, price, stock);
                if (productDB.updateProduct(updatedProduct)) {
                    hideOverlay();
                    searchProducts();
                } else {
                    AlertUtil.showError("Failed to update product!");
                }
            } else {
                List<Product> existing = productDB.searchProducts(id, "All", "All");
                if (!existing.isEmpty()) {
                    AlertUtil.showError("Product ID '" + id + "' already exists!");
                    return;
                }

                Product newProduct = new Product(id, name, category, price, stock);
                if (productDB.createProduct(newProduct)) {
                    // Save image if selected
                    if (selectedImageFile != null) {
                        ImageHelper.saveProductImage(name, category, selectedImageFile);
                    }

                    hideOverlay();
                    searchProducts();
                } else {
                    AlertUtil.showError("Failed to add product!");
                }
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Price must be a number and stock must be an integer!");
        }
    }

    @FXML
    private void deleteProduct() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + selectedProduct.getName() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (productDB.deleteProduct(selectedProduct.getProductId())) {
                // Delete associated image
                ImageHelper.deleteProductImage(selectedProduct.getName(), selectedProduct.getCategory());
                AlertUtil.showSuccess("Product deleted successfully!");
                hideOverlay();
                searchProducts();
            } else {
                AlertUtil.showError("Failed to delete product!");
            }
        }
    }

    private void setupTableColumns() {
        priceColumn.setText("Price (" + currency + ")");

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedPrice())
        );
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void setupFilters() {
        stockFilter.setItems(FXCollections.observableArrayList("All", "Low Stock", "Out of Stock", "In Stock"));
        stockFilter.setValue("All");
    }

    private void priceValidation() {
        editPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            if (newValue.length() > 10) {
                editPrice.setText(oldValue);
                return;
            }

            if (!newValue.matches("\\d*\\.?\\d{0,2}")) {
                editPrice.setText(oldValue);
                return;
            }

            if (newValue.chars().filter(ch -> ch == '.').count() > 1) {
                editPrice.setText(oldValue);
            }
        });
    }

    private void stockValidation() {
        editStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            if (!newValue.matches("\\d{0,10}")) {
                editStock.setText(oldValue);
            }
        });
    }

    @FXML
    private void searchProducts() {
        String searchText = searchField.getText();
        String category = categoryFilter.getValue();
        String stockStatus = stockFilter.getValue();

        if (category != null && category.equals("All")) {
            category = "All";
        }

        List<Product> products = productDB.searchProducts(searchText, category, stockStatus);
        productTable.setItems(FXCollections.observableArrayList(products));
        int totalCount = productDB.getTotalProductCount();
        itemCountLabel.setText(products.size() + " of " + totalCount + " items");
        updateSummaryCards(products);
    }

    private void updateSummaryCards(List<Product> products) {
        int totalStock = products.stream().mapToInt(Product::getStock).sum();
        long lowStock = products.stream().filter(p -> p.getStock() > 0 && p.getStock() < ConfigManager.getLowStockThreshold()).count();
        long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();
        double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();

        totalStockLabel.setText(String.valueOf(totalStock));
        lowStockLabel.setText(String.valueOf(lowStock));
        outOfStockLabel.setText(String.valueOf(outOfStock));
        totalValueLabel.setText(String.format("%s%.2f", currency, totalValue));
    }
}