package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import library.Book;

/**
 * Controller for managing individual book cards in the UI.
 */
public class CardController {

    @FXML
    private Label authorName;

    @FXML
    private Label bookName;

    @FXML
    private HBox box;

    @FXML
    private ImageView bookImage;

    private String[] colors = { "D8C3A5", "EAE7DC", "A9C0A6", "FF5056" };

    /**
     * Sets the data for a book card.
     *
     * @param book The book to display on the card.
     */
    public void setData(Book book) {
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor());

        box.setStyle("-fx-background-color: #" + colors[(int) (Math.random() * colors.length)] + ";" +
                    "-fx-background-radius: 15;" +
                    "-fx-effect: dropShadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 10)");

        String imageUrl = book.getImageSrc() != null ? book.getImageSrc() : "/design/Images/default_book.png";
        loadBookImage(imageUrl);
    }

    private void loadBookImage(String imageUrl) {
        bookImage.setImage(new Image(imageUrl, bookImage.getFitWidth(), bookImage.getFitHeight(), true, true));
    }

    @FXML
    public void initialize() {
        bookImage.setFitWidth(93.0);
        bookImage.setFitHeight(103.0);
        bookImage.setPreserveRatio(false);
        bookImage.setSmooth(true);

        Rectangle clip = new Rectangle(93.0, 103.0);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}
