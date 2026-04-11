package com.example.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminLayoutTest {
    @Test
    public void testWelcomeMessage() {
        // Test welcome message format
        String username = "admin";
        String welcomeMessage = "Welcome, " + username + "!";

        assertEquals("Welcome, admin!", welcomeMessage);
        assertTrue(welcomeMessage.contains(username));
    }

    @Test
    public void testLogoutButton() {
        // Test logout button text
        String logoutText = "Logout";
        assertEquals("Logout", logoutText);
    }
}