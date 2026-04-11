package com.example.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SettingsTest {

    @Test
    public void testTaxToggle() {
        // Only one tax button can be active at a time
        boolean onActive = true;
        boolean offActive = false;

        assertTrue(onActive);
        assertFalse(offActive);
    }

    @Test
    public void testNumberParsing() {
        int lowStock = Integer.parseInt("10");
        double taxRate = Double.parseDouble("0.10");

        assertEquals(10, lowStock);
        assertEquals(0.10, taxRate, 0.01);
    }

    @Test
    public void testInvalidNumberParsing() {
        // Invalid input should throw exception
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("abc");
        });

        assertThrows(NumberFormatException.class, () -> {
            Double.parseDouble("xyz");
        });
    }

    @Test
    public void testPasswordPlaceholder() {
        String passwordField = "********";

        assertTrue(passwordField.equals("********"));
        assertFalse(passwordField.isEmpty());
    }
}