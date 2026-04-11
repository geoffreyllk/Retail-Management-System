package com.example.coursework;

import com.example.coursework.models.Product;
import com.example.coursework.models.TransactionItems;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class CashierDashboardTest {
    // test if subtotal, tax and product price calculations work properly

    @Test
    public void testSubtotalCalculation() {
        List<TransactionItems> items = new ArrayList<>();
        items.add(new TransactionItems("P001", "Coca Cola", 2, 3.50));
        items.add(new TransactionItems("P002", "Pepsi", 1, 3.50));

        double subtotal = 0;
        // add both items to subtotal
        for (TransactionItems item : items) {
            subtotal += item.getSubtotal();
        }

        assertEquals(10.50, subtotal, 0.01);
    }

    @Test
    public void testTaxCalculation() {
        double subtotal = 100.00;
        double taxRate = 0.10;

        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        assertEquals(10.00, tax, 0.01);
        assertEquals(110.00, total, 0.01);
    }

    @Test
    public void testProductPrice() {
        Product product = new Product("P001", "Coca Cola", "Beverages", 3.50, 100);

        assertEquals(3.50, product.getPrice(), 0.01);
        assertEquals(100, product.getStock());
    }
}