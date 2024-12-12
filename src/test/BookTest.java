package test;

import library.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Book.
 */
public class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        // Initialize a book object before each test
        book = new Book(1, "Fiction", "Test Book", "Test Author", 10, "testImage.jpg", "Test Description");
    }

    @Test
    void testGettersAndSetters() {
        // Test initial values
        assertEquals(1, book.getId());
        assertEquals("Fiction", book.getCollection());
        assertEquals("Test Book", book.getName());
        assertEquals("Test Author", book.getAuthor());
        assertEquals(10, book.getAvailable());
        assertEquals("testImage.jpg", book.getImageSrc());
        assertEquals("Test Description", book.getDescription());

        // Test setters
        book.setName("Updated Book");
        book.setAuthor("Updated Author");
        book.setAvailable(5);
        book.setCollection("Updated Collection");
        book.setImageSrc("updatedImage.jpg");
        book.setDescription("Updated Description");

        // Validate updated values
        assertEquals("Updated Book", book.getName());
        assertEquals("Updated Author", book.getAuthor());
        assertEquals(5, book.getAvailable());
        assertEquals("Updated Collection", book.getCollection());
        assertEquals("updatedImage.jpg", book.getImageSrc());
        assertEquals("Updated Description", book.getDescription());
    }

    @Test
    void testAddToDatabase() throws Exception {
        try {
            book.addToDatabase();
            assertTrue(true, "Book should be added to database without exceptions.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testGetBook() throws Exception {
        Book retrievedBook = Book.getBook("1");
        assertNotNull(retrievedBook, "Retrieved book should not be null.");
        assertEquals("Fiction", retrievedBook.getCollection(), "Collection should match expected value.");
    }

    @Test
    void testGetLibrary() {
        List<Book> library = Book.getLibrary();
        assertNotNull(library, "Library should not be null.");
        assertTrue(library.size() > 0, "Library should contain books.");
    }

    @Test
    void testGetAvailableBooks() {
        List<Book> availableBooks = Book.getAvailableBooks();
        assertNotNull(availableBooks, "Available books list should not be null.");
        assertTrue(availableBooks.size() > 0, "There should be at least one available book.");
    }


    @Test
    void testSearchBookByCollections() {
        List<Book> books = Book.searchBookByCollections("Fiction");
        assertNotNull(books, "Search result should not be null.");
        assertTrue(books.size() > 0, "Search should return at least one book.");
    }

    @Test
    void testUpdateBookInDatabase() throws Exception {
        try {
            book.updateBookInDatabase();
            assertTrue(true, "Book should be updated in database without exceptions.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testDeleteBookFromDatabase() throws Exception {
        try {
            book.deleteBookFromDatabase(1);
            assertTrue(true, "Book should be deleted from database without exceptions.");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
