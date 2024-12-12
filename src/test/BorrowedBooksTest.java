package test;

import library.BorrowedBooks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BorrowedBooks.
 */
public class BorrowedBooksTest {

    private BorrowedBooks borrowedBook;

    @BeforeEach
    void setUp() {
        borrowedBook = new BorrowedBooks(1, "0123456789", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15), "Pending");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, borrowedBook.getId());
        assertEquals("0123456789", borrowedBook.getPhone_user());
        assertEquals(LocalDate.of(2024, 1, 1), borrowedBook.getBorrowDate());
        assertEquals(LocalDate.of(2024, 1, 15), borrowedBook.getDueDate());
        assertEquals("Pending", borrowedBook.getStatus());

        // Test setters
        borrowedBook.setPhone_user("0987654321");
        borrowedBook.setBorrowDate(LocalDate.of(2024, 2, 1));
        borrowedBook.setDueDate(LocalDate.of(2024, 2, 15));
        borrowedBook.setStatus("Returned");

        assertEquals("0987654321", borrowedBook.getPhone_user());
        assertEquals(LocalDate.of(2024, 2, 1), borrowedBook.getBorrowDate());
        assertEquals(LocalDate.of(2024, 2, 15), borrowedBook.getDueDate());
        assertEquals("Returned", borrowedBook.getStatus());
    }

    @Test
    void testAddBorrowedBookToDB() {
        // This test requires a functional database connection to verify behavior.
        try {
            borrowedBook.addBorrowedBookToDB();
            assertTrue(true, "Borrowed book should be added to database without exceptions.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testReturnBook() {
        try {
            borrowedBook.returnBook("0123456789", 1);
            assertTrue(true, "Book should be returned successfully.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStatus() {
        try {
            borrowedBook.updateStatus(1, "0123456789", "Returned");
            assertTrue(true, "Book status should be updated successfully.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testCheckBookBeforeDelete() {
        try {
            boolean result = BorrowedBooks.CheckBookBeforeDelete(1);
            assertFalse(result, "Book should not be deletable if it is active or overdue.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testCheckBookBeforeBorrow() {
        try {
            boolean result = BorrowedBooks.CheckBookBeforeBorrow("0123456789");
            assertFalse(result, "Book should not be borrowable if there are overdue logs.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testGetBookLogs() {
        try {
            List<BorrowedBooks> bookLogs = BorrowedBooks.getBookLogs();
            assertNotNull(bookLogs, "Book logs should not be null.");
            assertTrue(bookLogs.size() > 0, "Book logs should contain at least one entry.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }


    @Test
    void testSearchStatusBookLogs() {
        try {
            List<BorrowedBooks> bookLogs = BorrowedBooks.searchStatusBookLogs("Pending");
            assertNotNull(bookLogs, "Book logs by status should not be null.");
            assertTrue(bookLogs.size() > 0, "Book logs by status should contain at least one entry.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}