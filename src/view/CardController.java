package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import library.Book;

public class CardController {

    @FXML
    private Label authorName;

    @FXML
    private Label bookName;

    @FXML
    private HBox box;

    @FXML
    private ImageView bookImage;

    @FXML
    private ImageView rateImage;

    private String[] colors = { "B9E5FF", "BDB2FE", "FB9AA8", "FF5056" };

    public void setData(Book book) {
        Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor());
        box.setStyle("-fx-background-color: #" + colors[(int) (Math.random() * colors.length)] + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropShadown(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 10)");
    }
}
