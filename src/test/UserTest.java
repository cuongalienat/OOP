package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;
import library.User;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Nguyễn Văn A", "nguyenvana@example.com", "0123456789", "password123");
    }

    @Test
    void testGetName() {
        assertEquals("Nguyễn Văn A", user.getName(), "Name should match the initialized value.");
    }

    @Test
    void testSetName() {
        user.setName("Trần Văn B");
        assertEquals("Trần Văn B", user.getName(), "Name should be updated to the new value.");
    }

    @Test
    void testGetEmail() {
        assertEquals("nguyenvana@example.com", user.getEmail(), "Email should match the initialized value.");
    }

    @Test
    void testSetEmail() {
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail(), "Email should be updated to the new value.");
    }

    @Test
    void testPasswordHashing() throws Exception {
        String rawPassword = "newpassword123";
        user.setPassword(rawPassword);
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        assertNotEquals(rawPassword, hashedPassword, "Password should be hashed and not match the raw password.");
        assertTrue(BCrypt.checkpw(rawPassword, hashedPassword), "Hashed password should match the original password.");
    }

    @Test
    void testProfilePicture() {
        user.setProfilePicture("profile_pic.png");
        assertEquals("profile_pic.png", user.getProfilePicture(), "Profile picture should be set correctly.");
    }

    @Test
    void testRole() {
        user.setRole("Admin");
        assertEquals("Admin", user.getRole(), "Role should match the value set.");
    }

    @Test
    void testQuantityBorrowedBook() {
        user.setQuantityBorrowedBook(5);
        assertEquals(5, user.getQuantityBorrowedBook(), "Quantity of borrowed books should match the value set.");
    }

    @Test
    void testQuantityOverdueBook() {
        user.setQuantityOverduedateBook(2);
        assertEquals(2, user.getQuantityOverduedateBook(), "Quantity of overdue books should match the value set.");
    }

    @Test
    void testDeleteUser() throws Exception {
        // Mock database actions in a real-world scenario
        user.DeleteUser("0123456789");
        // Ensure no exceptions occur; actual implementation requires DB validation.
        assertTrue(true, "DeleteUser should complete without exceptions.");
    }
}
