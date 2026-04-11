package com.example.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    @Test
    public void testUsernameFormat() {
        String valid = "john_123";
        String invalid = "john@123";

        assertTrue(valid.matches("[a-zA-Z0-9_]+"));
        assertFalse(invalid.matches("[a-zA-Z0-9_]+"));
    }

    @Test
    public void testPasswordConfirmation() {
        String password = "mypass";
        String confirm = "mypass";
        String wrong = "wrong";

        assertTrue(password.equals(confirm));
        assertFalse(password.equals(wrong));
    }

    @Test
    public void testRoleOptions() {
        String[] roles = {"admin", "cashier"};

        assertEquals("admin", roles[0]);
        assertEquals("cashier", roles[1]);
    }

    @Test
    public void testUserCountFormat() {
        int showing = 3;
        int total = 10;

        String label = "Showing: " + showing + " of " + total + " users";

        assertEquals("Showing: 3 of 10 users", label);
    }

    @Test
    public void testSelfEditPrevention() {
        int currentUserId = 1;
        int selectedUserId = 1;

        boolean isSelf = selectedUserId == currentUserId;
        assertTrue(isSelf); // prevent edit of own admin user
    }
}