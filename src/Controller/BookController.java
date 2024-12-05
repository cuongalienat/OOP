package Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import library.Book;
/**
 * Controller for displaying individual book information in the UI.
 */
public class BookController {

    @FXML
    private Label authorName;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookName;

    /**
     * Sets the data for the book view.
     *
     * @param book The book whose data is to be displayed.
     */
    public void setData(Book book) {
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
    
        String imageUrl = book.getImageSrc() != null ? book.getImageSrc() : "/design/Images/default_book.png";
        loadBookImage(imageUrl);
    }

    /**
     * Loads the book image asynchronously.
     *
     * @param imageUrl The URL of the image to load.
     */
    private void loadBookImage(String imageUrl) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, bookImage.getFitWidth(), bookImage.getFitHeight(), true, true);
            }

            @Override
            protected void succeeded() {
                bookImage.setImage(getValue());
            }

            @Override
            protected void failed() {
                bookImage.setImage(new Image("/design/Images/default_book.png"));
            }
        };

        // Chạy task trên Thread mới
        new Thread(task).start();
    }

    /**
     * Initializes the BookController.
     */
    public void initialize() {
        bookImage.setFitWidth(121.0);
        bookImage.setFitHeight(147.0);
        bookImage.setPreserveRatio(false);
        bookImage.setSmooth(true);

        Rectangle clip = new Rectangle(121.0, 147.0);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}
