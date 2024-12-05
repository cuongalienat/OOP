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
import java.util.ArrayList;
import java.util.List;

public class SearchApiController {

    @FXML
    private TextField searchTextField;

    @FXML
    private VBox bookContainer; // VBox để chứa các thẻ sách

    /**
     * Initiates a search for books using the text from the search field.
     */
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

    /**
     * Parses book information from a JSON response.
     *
     * @param jsonResponse JSON string containing book data.
     * @return List of parsed books.
     */
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
                String imageUrl = volumeInfo.has("imageLinks")
                        ? volumeInfo.getJSONObject("imageLinks").optString("thumbnail")
                        : null;
                JSONArray categoriesArray = volumeInfo.optJSONArray("categories");
                String collection = categoriesArray != null ? String.join(", ", getCategories(categoriesArray))
                        : "Unknown";

                int id = bookId.hashCode();

                Book book = new Book(collection, title, authors, id, 1); 
                book.setImageSrc(imageUrl); 
                book.setDescription(description);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to parse API response.");
        }
        return books;
    }

    /**
     * Extracts authors from the volume information.
     *
     * @param volumeInfo JSON object containing book volume information.
     * @return List of authors.
     */
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

    /**
     * Extracts categories from the JSON array of categories.
     *
     * @param categoriesArray JSON array of categories.
     * @return List of categories.
     */
    private List<String> getCategories(JSONArray categoriesArray) {
        List<String> categories = new ArrayList<>();
        for (int j = 0; j < categoriesArray.length(); j++) {
            categories.add(categoriesArray.getString(j));
        }
        return categories;
    }

    /**
     * Displays a list of books in the UI.
     *
     * @param books List of books to display.
     */
    private void displayBooks(List<Book> books) {
        bookContainer.getChildren().clear(); // Clear existing books

        for (Book book : books) {
            HBox card = new HBox(10);
            card.setStyle(
                    "-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
            card.setPrefHeight(150);
            card.setPrefWidth(710);

            ImageView bookImage = new ImageView();
            bookImage.setFitHeight(120);
            bookImage.setFitWidth(100);

            if (book.getImageSrc() != null) {
                loadBookImage(book.getImageSrc(), bookImage);
            } else {
                bookImage.setImage(new Image("/design/Images/default_book.png")); 
            }

            VBox bookInfo = new VBox(5);
            Text title = new Text("Title: " + book.getName());
            Text authors = new Text("Author(s): " + book.getAuthor());
            Text collection = new Text("Collection: " + book.getCollection());
            Text bookIdText = new Text("Book ID: " + book.getId());
            bookInfo.getChildren().addAll(title, authors, collection, bookIdText);

            javafx.scene.control.Button addButton = new javafx.scene.control.Button("Add to Database");
            addButton.setOnAction(event -> addBookToDatabase(book)); // Truyền sách hiện tại

            VBox controls = new VBox();
            controls.getChildren().add(addButton);

            card.getChildren().addAll(bookImage, bookInfo, controls);
            bookContainer.getChildren().add(card);
        }
    }

    /**
     * Loads the book's image in the UI asynchronously.
     *
     * @param imageUrl URL of the book's image.
     * @param imageView ImageView component to display the image.
     */
    private void loadBookImage(String imageUrl, ImageView imageView) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                double fitWidth = imageView.getFitWidth();
                double fitHeight = imageView.getFitHeight();
                return new Image(imageUrl, fitWidth, fitHeight, true, true);
            }

            @Override
            protected void succeeded() {
                imageView.setImage(getValue());
            }

            @Override
            protected void failed() {
                imageView.setImage(new Image("/design/Images/default_book.png"));
            }
        };

        // Run the task in a separate thread to avoid freezing the UI
        new Thread(task).start();
    }

    /**
     * Shows an alert with the specified message.
     *
     * @param alertType The type of alert (e.g., WARNING, ERROR).
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Adds a book to the database, either updating its availability or inserting it.
     *
     * @param book The book to add to the database.
     */
    @FXML
    public void addBookToDatabase(Book book) {
        try {
            String checkQuery = "SELECT `ID`, `Available` FROM `book` WHERE `ID` = ?";
            String updateQuery = "UPDATE `book` SET `Available` = `Available` + 1 WHERE `ID` = ?";
            String insertQuery = "INSERT INTO `book` (`ID`, `Offer Collection`, `Book Title`, `Contributors`, `Available`, `ImageLink`, `Description`) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DbConfig.connect();
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

                checkStmt.setInt(1, book.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, book.getId());
                        updateStmt.executeUpdate();
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book already exists. Increased availability.");
                } else {
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
}