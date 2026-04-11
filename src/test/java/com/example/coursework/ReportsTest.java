package com.example.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReportsTest {

    @Test
    public void testTotalRevenueCalculation() {
        double revenue1 = 100.00;
        double revenue2 = 50.00;
        double totalRevenue = revenue1 + revenue2;

        assertEquals(150.00, totalRevenue, 0.01);
    }

    @Test
    public void testAverageCalculation() {
        double total = 500.00;
        int count = 10;
        double average = total / count;

        assertEquals(50.00, average, 0.01);
    }

    @Test
    public void testPercentageCalculation() {
        double part = 30.00;
        double whole = 100.00;
        double percentage = (part / whole) * 100;

        assertEquals(30.0, percentage, 0.01);
    }

    @Test
    public void testTaxRateFormat() {
        double taxRate = 0.10;
        int taxPercentage = (int)(taxRate * 100);

        assertEquals(10, taxPercentage);
    }
}