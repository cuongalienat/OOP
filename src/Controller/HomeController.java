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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import library.Book;
import library.DbConfig;

import java.net.URL;

public class HomeController implements Initializable {

    @FXML
    private HBox cardLayout;

    @FXML
    private GridPane bookContainer;

    // @FXML
    // private ProgressIndicator loadingSpinner;

    @FXML
    private ImageView loadingGif; 

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hiển thị GIF khi bắt đầu tải
        showLoadingGif(true);

        // Tạo task để tải dữ liệu nền
        Task<Void> loadDataTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Tải danh sách sách
                List<Book> topBooks = getMostBorrowedBooks(5);
                List<Book> recommendBooks = getRecommendBooks(3, 25);

                // Chờ 2 giây để hiển thị GIF
                Thread.sleep(2000);

                // Cập nhật giao diện trên JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    displayTopBooks(topBooks);
                    displayRecommendBooks(recommendBooks);

                    // Ẩn GIF sau khi tải xong
                    showLoadingGif(false);
                });
                return null;
            }
        };

        // Chạy task trên một thread mới
        new Thread(loadDataTask).start();
    }

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
    // @Override
    // public void initialize(URL url, ResourceBundle resourceBundle) {
    //     // Hiển thị ProgressIndicator
    //     loadingSpinner.setVisible(true);

    //     // Tạo task để tải dữ liệu
    //     Task<Void> loadDataTask = new Task<>() {
    //         @Override
    //         protected Void call() throws Exception {
    //             // Giả lập tiến trình
    //             for (int i = 0; i <= 100; i++) {
    //                 Thread.sleep(10); // Giả lập thời gian tải
    //                 updateProgress(i, 100); // Cập nhật tiến độ
    //             }

    //             // Tải dữ liệu thực tế
    //             List<Book> topBooks = getMostBorrowedBooks(5);
    //             List<Book> recommendBooks = getRecommendBooks(3, 25);

    //             // Cập nhật giao diện trên JavaFX Application Thread
    //             javafx.application.Platform.runLater(() -> {
    //                 displayTopBooks(topBooks);
    //                 displayRecommendBooks(recommendBooks);
    //             });

    //             return null;
    //         }
    //     };

    //     // Liên kết progress của ProgressIndicator với Task
    //     loadingSpinner.progressProperty().bind(loadDataTask.progressProperty());

    //     // Ẩn spinner sau khi task hoàn tất
    //     loadDataTask.setOnSucceeded(event -> loadingSpinner.setVisible(false));

    //     // Chạy task trên một thread mới
    //     new Thread(loadDataTask).start();
    // }

    private void displayTopBooks(List<Book> topBooks) {
        SequentialTransition sequentialTransition = new SequentialTransition();

        for (Book book : topBooks) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/card.fxml"));
                HBox cardBox = fxmlLoader.load();

                // Truyền dữ liệu vào CardController
                CardController cardController = fxmlLoader.getController();
                cardController.setData(book);

                // Gắn sự kiện click
                cardBox.setOnMouseClicked(event -> showBookDetails(book));

                // Thêm card vào cardLayout
                cardLayout.getChildren().add(cardBox);

                // Tạo hiệu ứng fade-in cho card
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), cardBox);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);

                // Thêm vào SequentialTransition
                sequentialTransition.getChildren().add(fadeTransition);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Bắt đầu hiệu ứng hiển thị dần dần
        sequentialTransition.play();
    }

    private void displayRecommendBooks(List<Book> recommendBooks) {
        SequentialTransition sequentialTransition = new SequentialTransition();
        int column = 0;
        int row = 1;

        for (Book book : recommendBooks) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/book.fxml"));
                VBox bookBox = fxmlLoader.load();

                // Truyền dữ liệu vào BookController
                BookController bookController = fxmlLoader.getController();
                bookController.setData(book);

                // Gắn sự kiện click
                bookBox.setOnMouseClicked(event -> showBookDetails(book));

                // Thêm book vào GridPane
                if (column == 10) {
                    column = 0;
                    row++;
                }
                bookContainer.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(10));

                // Tạo hiệu ứng fade-in cho book
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), bookBox);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);

                // Thêm vào SequentialTransition
                sequentialTransition.getChildren().add(fadeTransition);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Bắt đầu hiệu ứng hiển thị dần dần
        sequentialTransition.play();
    }

    private void showBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            // Lấy controller của bookDetails.fxml
            BookDetailsController bookDetailsController = fxmlLoader.getController();

            // Truyền thông tin sách từ card vào
            bookDetailsController.setBookDetails(book);

            // Hiển thị cửa sổ chi tiết sách
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(bookDetailsRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    

    private List<Book> getRecommendBooks(int booksPerCollection, int totalBooksLimit) {
        List<Book> recommendedBooks = new ArrayList<>();
    
        // Truy vấn danh sách Offer Collection mà người dùng đã mượn
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
                            "ORDER BY RAND() " + // Random hóa kết quả
                            "LIMIT ?";
    
        try (Connection conn = DbConfig.connect();
             PreparedStatement collectionsStmt = conn.prepareStatement(collectionsQuery)) {
    
            // Lấy thông tin người dùng hiện tại
            String currentUserPhone = loginController.getUser_now().getPhone();
            collectionsStmt.setString(1, currentUserPhone);
    
            ResultSet collectionsRs = collectionsStmt.executeQuery();
    
            // Lặp qua từng Offer Collection mà người dùng đã mượn
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
                        String description = booksRs.getString("Description"); // Lấy mô tả
    
                        // Tạo đối tượng Book và thêm vào danh sách recommend
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
