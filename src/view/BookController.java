package view;

import org.json.JSONObject;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import library.Book;
import library.GoogleBooksAPI;

public class BookController {
    @FXML
    private Label authorName;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookName;

    BookDetailsController bookDetailsController = new BookDetailsController();
    public void setData(Book book) {
        authorName.setText(book.getName());
    }
}
