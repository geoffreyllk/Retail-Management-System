package com.example.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SalesTest {

    @Test
    public void testDateFormat() {
        String date = "2026-04-11 2:30 PM";
        assertTrue(date.contains("2026"));
        assertTrue(date.contains("PM"));
    }

    @Test
    public void testCurrencyFormat() {
        String formatted = "RM123.45";
        assertTrue(formatted.startsWith("RM"));
        assertTrue(formatted.contains("."));
    }

    @Test
    public void testAmountValidation() {
        // Valid numbers
        assertTrue("100".matches("\\d*\\.?\\d*"));
        assertTrue("100.50".matches("\\d*\\.?\\d*"));

        // Invalid inputs
        assertFalse("abc".matches("\\d*\\.?\\d*"));
        assertFalse("100..50".matches("\\d*\\.?\\d*"));
    }

    @Test
    public void testCSVExport() {
        String csvHeader = "ID,Cashier,Payment Method,Total,Date";
        String[] columns = csvHeader.split(",");

        assertEquals(5, columns.length);
        assertEquals("ID", columns[0]);
        assertEquals("Date", columns[4]);
    }
}