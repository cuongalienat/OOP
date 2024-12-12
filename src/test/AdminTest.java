package test;

import library.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Admin.
 */
public class AdminTest {

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin("Admin User", "admin@example.com", "0123456789", "admin123");
    }

    @Test
    void testAdminMethods() {
        assertEquals("Admin User", admin.getName());
        assertEquals("admin@example.com", admin.getEmail());
        admin.setName("Updated Admin");
        assertEquals("Updated Admin", admin.getName());
    }
}
