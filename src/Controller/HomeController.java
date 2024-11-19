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

public class HomeController implements Initializable {

    @FXML
    private HBox cardLayout;

    @FXML
    private GridPane bookContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Lấy tất cả sách từ database
            List<Book> books = getAllBooks();

            List<Book> randomBooks1 = getRandomBooks(books, 2);
            List<Book> randomBooks2 = getRandomBooks(books, 2);

            // Hiển thị sách trong HBox
            for (Book book : randomBooks1) {
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
            for (Book book : randomBooks2) {
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
}
