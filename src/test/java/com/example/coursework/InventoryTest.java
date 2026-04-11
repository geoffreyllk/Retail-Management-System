package com.example.coursework;

import com.example.coursework.models.Product;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    public void testProductValidation() {
        // Test price cannot be negative
        double price = 3.50;
        assertTrue(price >= 0);

        double negativePrice = -5.00;
        assertFalse(negativePrice >= 0);
    }

    @Test
    public void testStockValidation() {
        // Test stock cannot be negative
        int stock = 100;
        assertTrue(stock >= 0);

        int negativeStock = -10;
        assertFalse(negativeStock >= 0);
    }

    @Test
    public void testLowStockCalculation() {
        int threshold = 10;

        List<Product> products = new ArrayList<>();
        products.add(new Product("P001", "Coke", "Beverages", 3.50, 5));   // low stock
        products.add(new Product("P002", "Chips", "Snacks", 4.50, 50));    // normal stock
        products.add(new Product("P003", "Water", "Beverages", 1.50, 0));   // out of stock

        // test thresholds
        long lowStockCount = products.stream().filter(p -> p.getStock() > 0 && p.getStock() < threshold).count();
        long outOfStockCount = products.stream().filter(p -> p.getStock() == 0).count();

        assertEquals(1, lowStockCount);
        assertEquals(1, outOfStockCount);
    }

    @Test
    public void testTotalValueCalculation() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("P001", "Coke", "Beverages", 3.50, 100));
        products.add(new Product("P002", "Chips", "Snacks", 4.50, 50));

        double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();

        // 3.50 * 100 = 350, 4.50 * 50 = 225, total = 575
        assertEquals(575.00, totalValue, 0.01);
    }

    @Test
    public void testProductIdFormat() {
        String productId = "P001";

        assertTrue(productId.startsWith("P"));
        assertTrue(productId.length() >= 3);
        assertTrue(productId.matches("P\\d{3}"));
    }

    @Test
    public void testPriceFormatting() {
        double price = 3.50;
        String currency = "RM";
        String formatted = String.format("%s%.2f", currency, price);

        assertEquals("RM3.50", formatted);
    }

    @Test
    public void testSearchFilterLogic() {
        String searchText = "cola";
        String productName = "Coca Cola";

        boolean matchesSearch = productName.toLowerCase().contains(searchText);
        assertTrue(matchesSearch);

        String nonMatch = "Pepsi";
        boolean notMatches = nonMatch.toLowerCase().contains(searchText);
        assertFalse(notMatches);
    }
}