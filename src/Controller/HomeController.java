package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import library.Book;
import library.DbConfig;

import java.net.URL;

/**
 * Controller for managing the Home view and related operations.
 */
public class HomeController implements Initializable {

    @FXML
    private HBox cardLayout;

    @FXML
    private GridPane bookContainer;

    @FXML
    private ImageView loadingGif; 

    /**
     * Initializes the HomeController.
     *
     * @param url            The location used to resolve relative paths for the
     *                       root object.
     * @param resourceBundle The resources used to localize the root object.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showLoadingGif(true);

        // Tạo task để tải dữ liệu nền
        Task<Void> loadDataTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Tải danh sách sách
                List<Book> topBooks = getMostBorrowedBooks(5);
                List<Book> recommendBooks = getRecommendBooks(3, 25);

                Thread.sleep(2000);

                javafx.application.Platform.runLater(() -> {
                    displayTopBooks(topBooks);
                    displayRecommendBooks(recommendBooks);

                    showLoadingGif(false);
                });
                return null;
            }
        };

        // Chạy task trên một thread mới
        new Thread(loadDataTask).start();
    }

    /**
     * Toggles the visibility of the loading gif with a fade transition.
     *
     * @param show Whether to show the loading gif or not.
     */
    private void showLoadingGif(boolean show) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), loadingGif);
        if (show) {
            loadingGif.setVisible(true);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
        } else {
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.setOnFinished(event -> loadingGif.setVisible(false));
        }
        fadeTransition.play();
    }

    /**
     * Displays a list of top books in the UI.
     *
     * @param topBooks A list of the most borrowed books to display.
     */
    private void displayTopBooks(List<Book> topBooks) {
        SequentialTransition sequentialTransition = new SequentialTransition();

        for (Book book : topBooks) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/card.fxml"));
                HBox cardBox = fxmlLoader.load();

                CardController cardController = fxmlLoader.getController();
                cardController.setData(book);

                cardBox.setOnMouseClicked(event -> showBookDetails(book));

                cardLayout.getChildren().add(cardBox);

                FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), cardBox);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);

                sequentialTransition.getChildren().add(fadeTransition);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sequentialTransition.play();
    }

    /**
     * Displays a list of recommended books in the UI.
     *
     * @param recommendBooks A list of recommended books to display.
     */
    private void displayRecommendBooks(List<Book> recommendBooks) {
        SequentialTransition sequentialTransition = new SequentialTransition();
        int column = 0;
        int row = 1;

        for (Book book : recommendBooks) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/book.fxml"));
                VBox bookBox = fxmlLoader.load();

                BookController bookController = fxmlLoader.getController();
                bookController.setData(book);

                bookBox.setOnMouseClicked(event -> showBookDetails(book));

                if (column == 10) {
                    column = 0;
                    row++;
                }
                bookContainer.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(10));

                FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), bookBox);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);

                sequentialTransition.getChildren().add(fadeTransition);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Bắt đầu hiệu ứng hiển thị dần dần
        sequentialTransition.play();
    }

    /**
     * Opens a new window displaying the details of the selected book.
     *
     * @param book The book whose details should be displayed.
     */
    private void showBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            BookDetailsController bookDetailsController = fxmlLoader.getController();

            bookDetailsController.setBookDetails(book);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(bookDetailsRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the most borrowed books from the database.
     *
     * @param limit The maximum number of books to retrieve.
     * @return A list of the most borrowed books.
     * @throws Exception If any error occurs while fetching data.
     */
    private List<Book> getMostBorrowedBooks(int limit) throws Exception {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT b.ID, b.`Offer Collection`, b.`Book Title`, b.Contributors, b.Available, b.ImageLink, b.Description, " +
                       "COUNT(bl.book_id) AS borrow_count " +
                       "FROM librarymanagement.book b " +
                       "JOIN librarymanagement.booklogs bl ON b.ID = bl.book_id " +
                       "GROUP BY b.ID " +
                       "ORDER BY borrow_count DESC " +
                       "LIMIT ?";
    
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                Integer id = rs.getInt("ID");
                Integer available = rs.getInt("Available");
                String imageUrl = rs.getString("ImageLink");
                String description = rs.getString("Description"); // Lấy mô tả
    
                Book book = new Book(id, collection, name, author, available, imageUrl, description);
                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }
    
    /**
     * Retrieves recommended books for the user based on their past activity.
     *
     * @param booksPerCollection The number of books to retrieve from each collection.
     * @param totalBooksLimit The total number of books to retrieve.
     * @return A list of recommended books.
     */
    private List<Book> getRecommendBooks(int booksPerCollection, int totalBooksLimit) {
        List<Book> recommendedBooks = new ArrayList<>();
    
        String collectionsQuery = "SELECT DISTINCT b.`Offer Collection` " +
                                  "FROM librarymanagement.book b " +
                                  "JOIN librarymanagement.booklogs bl ON b.ID = bl.book_id " +
                                  "WHERE bl.phone_user = ?";
    
        // Truy vấn sách từ một Offer Collection cụ thể
        String booksQuery = "SELECT DISTINCT b.ID, b.`Offer Collection`, b.`Book Title`, b.Contributors, b.Available, b.ImageLink, b.Description " +
                            "FROM librarymanagement.book b " +
                            "WHERE b.`Offer Collection` = ? " +
                            "AND b.ID NOT IN ( " +
                            "    SELECT DISTINCT bl.book_id " +
                            "    FROM librarymanagement.booklogs bl " +
                            "    WHERE bl.phone_user = ? " +
                            ") " +
                            "ORDER BY RAND() " + 
                            "LIMIT ?";
    
        try (Connection conn = DbConfig.connect();
             PreparedStatement collectionsStmt = conn.prepareStatement(collectionsQuery)) {
    
            String currentUserPhone = loginController.getUser_now().getPhone();
            collectionsStmt.setString(1, currentUserPhone);
    
            ResultSet collectionsRs = collectionsStmt.executeQuery();
    
            while (collectionsRs.next() && recommendedBooks.size() < totalBooksLimit) {
                String collection = collectionsRs.getString("Offer Collection");
    
                try (PreparedStatement booksStmt = conn.prepareStatement(booksQuery)) {
                    booksStmt.setString(1, collection);       // Offer Collection hiện tại
                    booksStmt.setString(2, currentUserPhone); // Loại trừ sách người dùng đã mượn
                    booksStmt.setInt(3, booksPerCollection);  // Giới hạn số sách mỗi Collection
    
                    ResultSet booksRs = booksStmt.executeQuery();
    
                    // Lặp qua từng sách trong Collection
                    while (booksRs.next() && recommendedBooks.size() < totalBooksLimit) {
                        String collectionName = booksRs.getString("Offer Collection");
                        String name = booksRs.getString("Book Title");
                        String author = booksRs.getString("Contributors");
                        Integer id = booksRs.getInt("ID");
                        Integer available = booksRs.getInt("Available");
                        String imageUrl = booksRs.getString("ImageLink");
                        String description = booksRs.getString("Description");
    
                        Book book = new Book(id, collectionName, name, author, available, imageUrl, description);
                        recommendedBooks.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return recommendedBooks;
    }
}
