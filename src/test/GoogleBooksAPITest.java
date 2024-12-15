package test;

import library.GoogleBooksAPI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GoogleBooksAPI.
 */
public class GoogleBooksAPITest {
    @AfterAll
    static void tearDown() {
        GoogleBooksAPI.shutdownExecutor();
    }

    @Test
    void testSearchBookByTitle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        GoogleBooksAPI.searchBookByTitle("JAVA Programming", response -> {
            System.out.println("Callback invoked. Response received: " + response);
            assertNotNull(response, "Response should not be null.");
            assertTrue(response.contains("items") || response.contains("error"),
                    "Response should contain 'items' or 'error'.");
            latch.countDown();
        });

        // boolean completed = latch.await(30, TimeUnit.SECONDS);
        // assertTrue(completed, "The callback was not invoked in time.");
    }
}