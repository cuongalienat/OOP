package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    /**
     * Initializes the HomeController.
     *
     * @param url            The location used to resolve relative paths for the
     *                       root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            List<Book> topBooks = getMostBorrowedBooks(10);
            List<Book> recommendBooks = getRecommendBooks(3,25);

            // Hiển thị sách trong HBox
            for (Book book : topBooks) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/card.fxml"));
                HBox cardBox = fxmlLoader.load();

                // Set dữ liệu từ database vào card
                CardController cardController = fxmlLoader.getController();
                cardController.setData(book);
                cardBox.setOnMouseClicked(event -> showBookDetails(book));
                cardLayout.getChildren().add(cardBox);
            }

            // Hiển thị sách trong GridPane
            int column = 0;
            int row = 1;
            for (Book book : recommendBooks) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/book.fxml"));
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all books from the database.
     *
     * @return A list of all books.
     */
    private List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM librarymanagement.book"; // Truy vấn lấy tất cả sách
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                Integer id = rs.getInt("ID");
                Integer available = rs.getInt("Available");

                Book book = new Book(collection, name, author, id, available);
                bookList.add(book); // Thêm vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi kết nối database
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi khác
        }
        return bookList;
    }

    /**
     * Retrieves a random subset of books.
     *
     * @param books The list of all books.
     * @param cnt   The number of random books to retrieve.
     * @return A list of random books.
     */
    private List<Book> getRandomBooks(List<Book> books, int cnt) {
        List<Book> randomBooks = new ArrayList<>();
        if (books.size() <= cnt) {
            return books;
        }
        List<Integer> usedIdx = new ArrayList<>();
        while (randomBooks.size() < cnt) {
            int randomIdx = (int) (Math.random() * books.size());
            if (!usedIdx.contains(randomIdx)) {
                randomBooks.add(books.get(randomIdx));
                usedIdx.add(randomIdx);
            }
        }
        return randomBooks;
    }

    /**
     * Displays the details of a selected book.
     *
     * @param book The book to display details for.
     */
    private void showBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            // Lấy controller của bookDetails.fxml và truyền thông tin sách vào
            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.setBookDetails(book);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED); // Tắt thanh tiêu đề
            stage.setScene(new Scene(bookDetailsRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Book> getMostBorrowedBooks(int limit) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT b.ID, b.`Offer Collection`, b.`Book Title`, b.Contributors, b.Available, COUNT(bl.book_id) AS borrow_count " +
                       "FROM librarymanagement.book b " +
                       "JOIN librarymanagement.booklogs bl ON b.ID = bl.book_id " +
                       "GROUP BY b.ID " +
                       "ORDER BY borrow_count DESC " +
                       "LIMIT ?"; // Lấy số lượng sách được mượn nhiều nhất, giới hạn bởi 'limit'
    
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
    
                Book book = new Book(collection, name, author, id, available);
                bookList.add(book); // Thêm sách vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi kết nối database
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi khác
        }
        return bookList;
    }   
    private List<Book> getRecommendBooks(int booksPerCollection, int totalBooksLimit) {
        List<Book> recommendedBooks = new ArrayList<>();
        
        // Truy vấn danh sách Offer Collection mà người dùng đã mượn
        String collectionsQuery = "SELECT DISTINCT b.`Offer Collection` " +
                                  "FROM librarymanagement.book b " +
                                  "JOIN librarymanagement.booklogs bl ON b.ID = bl.book_id " +
                                  "WHERE bl.phone_user = ?";
    
        // Truy vấn sách từ một Offer Collection cụ thể
        String booksQuery = "SELECT DISTINCT b.ID, b.`Offer Collection`, b.`Book Title`, b.Contributors, b.Available " +
                            "FROM librarymanagement.book b " +
                            "WHERE b.`Offer Collection` = ? " +
                            "AND b.ID NOT IN ( " +
                            "    SELECT DISTINCT bl.book_id " +
                            "    FROM librarymanagement.booklogs bl " +
                            "    WHERE bl.phone_user = ? " +
                            ") " +
                            "ORDER BY RAND() " +  // Random hóa kết quả
                            "LIMIT ?";
    
        try (Connection conn = DbConfig.connect();
             PreparedStatement collectionsStmt = conn.prepareStatement(collectionsQuery)) {
    
            // Lấy thông tin người dùng hiện tại
            String currentUserPhone = loginController.getUser_now().getPhone();
            collectionsStmt.setString(1, currentUserPhone);
    
            ResultSet collectionsRs = collectionsStmt.executeQuery();
    
            // Lặp qua từng Offer Collection
            while (collectionsRs.next() && recommendedBooks.size() < totalBooksLimit) {
                String collection = collectionsRs.getString("Offer Collection");
    
                try (PreparedStatement booksStmt = conn.prepareStatement(booksQuery)) {
                    booksStmt.setString(1, collection);       // Offer Collection hiện tại
                    booksStmt.setString(2, currentUserPhone); // Loại trừ sách người dùng đã mượn
                    booksStmt.setInt(3, booksPerCollection);  // Giới hạn số sách mỗi Collection
    
                    ResultSet booksRs = booksStmt.executeQuery();
    
                    while (booksRs.next() && recommendedBooks.size() < totalBooksLimit) {
                        String collectionName = booksRs.getString("Offer Collection");
                        String name = booksRs.getString("Book Title");
                        String author = booksRs.getString("Contributors");
                        Integer id = booksRs.getInt("ID");
                        Integer available = booksRs.getInt("Available");
    
                        Book book = new Book(collectionName, name, author, id, available);
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
