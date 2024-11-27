package Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import library.Book;
import library.DbConfig;
import library.GoogleBooksAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchApiController {

    @FXML
    private TextField searchTextField;

    @FXML
    private VBox bookContainer; // VBox để chứa các thẻ sách

    @FXML
    public void searchBooks() {
        String query = searchTextField.getText().trim();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a book title.");
            return;
        }

        GoogleBooksAPI.searchBookByTitle(query, jsonResponse -> {
            List<Book> books = parseBooksFromJson(jsonResponse);
            displayBooks(books);
        });
    }

    private List<Book> parseBooksFromJson(String jsonResponse) {
        List<Book> books = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray items = jsonObject.optJSONArray("items");
    
            if (items == null) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No books found for the given query.");
                return books;
            }
    
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String bookId = item.optString("id", "Unknown ID"); // Lấy ID từ đối tượng item
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
    
                // Extract book information
                String title = volumeInfo.optString("title", "No Title");
                String authors = volumeInfo.has("authors") ? String.join(", ", getAuthors(volumeInfo)) : "Unknown";
                String description = volumeInfo.optString("description", "No description available.");
                String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").optString("thumbnail") : null;
                JSONArray categoriesArray = volumeInfo.optJSONArray("categories");
                String collection = categoriesArray != null ? String.join(", ", getCategories(categoriesArray)) : "Unknown";
    
                // Convert bookId to an integer hash for simplicity (you can customize this)
                int id = bookId.hashCode();
    
                // Create a new Book object
                Book book = new Book(collection, title, authors, id, 1); // Use the hashed ID
                book.setImageSrc(imageUrl); // Store image URL in the book object
                book.setDescription(description);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to parse API response.");
        }
        return books;
    }
    

    private List<String> getAuthors(JSONObject volumeInfo) {
        List<String> authors = new ArrayList<>();
        JSONArray authorsArray = volumeInfo.optJSONArray("authors");
        if (authorsArray != null) {
            for (int j = 0; j < authorsArray.length(); j++) {
                authors.add(authorsArray.getString(j));
            }
        }
        return authors;
    }

    private List<String> getCategories(JSONArray categoriesArray) {
        List<String> categories = new ArrayList<>();
        for (int j = 0; j < categoriesArray.length(); j++) {
            categories.add(categoriesArray.getString(j));
        }
        return categories;
    }

    private void displayBooks(List<Book> books) {
        bookContainer.getChildren().clear(); // Clear existing books
    
        for (Book book : books) {
            HBox card = new HBox(10);
            card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
            card.setPrefHeight(150);
            card.setPrefWidth(710);
    
            // Book Image
            ImageView bookImage = new ImageView();
            bookImage.setFitHeight(120);
            bookImage.setFitWidth(100);
    
            // Load the image asynchronously
            if (book.getImageSrc() != null) {
                loadBookImage(book.getImageSrc(), bookImage);
            } else {
                bookImage.setImage(new Image("/design/Images/default_book.png")); // Default image if URL is null
            }
    
            // Book Info
            VBox bookInfo = new VBox(5);
            Text title = new Text("Title: " + book.getName());
            Text authors = new Text("Author(s): " + book.getAuthor());
            Text collection = new Text("Collection: " + book.getCollection());
            Text bookIdText = new Text("Book ID: " + book.getId());
            bookInfo.getChildren().addAll(title, authors, collection, bookIdText);
    
            // Add Button
            javafx.scene.control.Button addButton = new javafx.scene.control.Button("Add to Database");
            addButton.setOnAction(event -> addBookToDatabase(book)); // Truyền sách hiện tại
    
            // Add components to the card
            VBox controls = new VBox();
            controls.getChildren().add(addButton);
    
            card.getChildren().addAll(bookImage, bookInfo, controls);
            bookContainer.getChildren().add(card);
        }
    }        

    private void loadBookImage(String imageUrl, ImageView imageView) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                // Resize the image to fit the ImageView dimensions
                double fitWidth = imageView.getFitWidth();
                double fitHeight = imageView.getFitHeight();
                return new Image(imageUrl, fitWidth, fitHeight, true, true);
            }

            @Override
            protected void succeeded() {
                // Update the ImageView with the loaded image
                imageView.setImage(getValue());
            }

            @Override
            protected void failed() {
                // Set a default image if the loading fails
                imageView.setImage(new Image("/design/Images/default_book.png"));
            }
        };

        // Run the task in a separate thread to avoid freezing the UI
        new Thread(task).start();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void addBookToDatabase(Book book) {
        try {
            String checkQuery = "SELECT `ID`, `Available` FROM `book` WHERE `ID` = ?";
            String updateQuery = "UPDATE `book` SET `Available` = `Available` + 1 WHERE `ID` = ?";
            String insertQuery = "INSERT INTO `book` (`ID`, `Offer Collection`, `Book Title`, `Contributors`, `Available`, `ImageLink`, `Description`) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DbConfig.connect();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

                // Kiểm tra sách với ID đã tồn tại chưa
                checkStmt.setInt(1, book.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Sách đã tồn tại -> Tăng `Available`
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, book.getId());
                        updateStmt.executeUpdate();
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book already exists. Increased availability.");
                } else {
                    // Sách chưa tồn tại -> Thêm mới
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, book.getId());
                        insertStmt.setString(2, book.getCollection());
                        insertStmt.setString(3, book.getName());
                        insertStmt.setString(4, book.getAuthor());
                        insertStmt.setInt(5, book.getAvailable()); // Available = 1
                        insertStmt.setString(6, book.getImageSrc());
                        insertStmt.setString(7, book.getDescription());
                        insertStmt.executeUpdate();
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book added to the database successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book to the database.");
        }
    }



    
    // private int generateNewBookId() throws Exception {
    //     String query = "SELECT MAX(ID) FROM `book`";
    //     try (Connection conn = DbConfig.connect();
    //          PreparedStatement stmt = conn.prepareStatement(query);
    //          ResultSet rs = stmt.executeQuery()) {
    //         if (rs.next()) {
    //             return rs.getInt(1) + 1; // ID mới = ID lớn nhất + 1
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return 1; // Trả về ID mặc định nếu bảng trống
    // }
}