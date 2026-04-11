package com.example.coursework;

import com.example.coursework.models.Product;
import com.example.coursework.models.TransactionItems;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class AdminDashboardTest {

    @Test
    public void testPercentageCalculationIncrease() {
        // Simulate setPercentage logic: today > yesterday = positive percentage
        double today = 110.00;
        double yesterday = 100.00;

        double percent = ((today - yesterday) / yesterday) * 100;

        assertEquals(10.0, percent, 0.01);
        assertTrue(percent > 0);
    }

    @Test
    public void testPercentageCalculationDecrease() {
        // today < yesterday = negative percentage
        double today = 90.00;
        double yesterday = 100.00;

        double percent = ((today - yesterday) / yesterday) * 100;

        assertEquals(-10.0, percent, 0.01);
        assertTrue(percent < 0);
    }

    @Test
    public void testPercentageCalculationNoChange() {
        // today == yesterday = 0
        double today = 100.00;
        double yesterday = 100.00;

        double percent = ((today - yesterday) / yesterday) * 100;

        assertEquals(0.0, percent, 0.01);
    }

    @Test
    public void testLowStockDetection() {
        int threshold = 10;

        Product lowStock = new Product("P001", "Coke", "Beverages", 3.50, 5);
        Product normalStock = new Product("P002", "Chips", "Snacks", 4.50, 50);

        assertTrue(lowStock.getStock() < threshold);
        assertFalse(normalStock.getStock() < threshold);
    }

    @Test
    public void testCurrencyFormatting() {
        String currency = "RM";
        double amount = 123.45;

        String formatted = String.format("%s%.2f", currency, amount);

        assertEquals("RM123.45", formatted);
    }

    @Test
    public void testTotalAmountCalculation() {
        List<TransactionItems> items = new ArrayList<>();
        items.add(new TransactionItems("P001", "Coca Cola", 2, 3.50)); // 7.00
        items.add(new TransactionItems("P002", "Pepsi", 1, 3.50));     // 3.50

        double total = 0;
        for (TransactionItems item : items) {
            total += item.getSubtotal();
        }

        assertEquals(10.50, total, 0.01);
    }
}