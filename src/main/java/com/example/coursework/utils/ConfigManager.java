package com.example.coursework.utils;

import java.io.*;
import java.util.Properties;

// config manager for low_stock_threshold, currency, tax_rate, tax included
public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties props = new Properties();

    // Load config on startup
    static {
        loadConfig();
    }

    public static void loadConfig() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, create default
            setDefaultValues();
            saveConfig();
        } catch (IOException e) {
            System.out.println("Error loading config: " + e.getMessage());
        }
    }

    public static void saveConfig() {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "Application Settings");
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    private static void setDefaultValues() {
        props.setProperty("low_stock_threshold", "10");
        props.setProperty("currency", "RM");
        props.setProperty("tax_rate", "0");
        props.setProperty("tax_included", "false");
        props.setProperty("store_name", "My Retail Store");
    }

    // Getters
    public static int getLowStockThreshold() {
        return Integer.parseInt(props.getProperty("low_stock_threshold", "10"));
    }

    public static String getCurrency() {
        return props.getProperty("currency", "RM");
    }

    public static double getTaxRate() {
        return Double.parseDouble(props.getProperty("tax_rate", "0"));
    }

    public static boolean isTaxIncluded() {
        return Boolean.parseBoolean(props.getProperty("tax_included", "false"));
    }

    public static String getStoreName() {
        return props.getProperty("store_name", "My Retail Store");
    }

    // Setters
    public static void setLowStockThreshold(int value) {
        props.setProperty("low_stock_threshold", String.valueOf(value));
        saveConfig();
    }

    public static void setCurrency(String value) {
        props.setProperty("currency", value);
        saveConfig();
    }

    public static void setTaxRate(double value) {
        props.setProperty("tax_rate", String.valueOf(value));
        saveConfig();
    }

    public static void setTaxIncluded(boolean value) {
        props.setProperty("tax_included", String.valueOf(value));
        saveConfig();
    }

    public static void setStoreName(String value) {
        props.setProperty("store_name", String.valueOf(value));
        saveConfig();
    }
}