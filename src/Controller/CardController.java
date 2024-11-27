package Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    private String[] colors = { "D8C3A5", "EAE7DC", "A9C0A6", "FF5056" };

    public void setData(Book book) {
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor());

        // Thay đổi màu nền của card
        box.setStyle("-fx-background-color: #" + colors[(int) (Math.random() * colors.length)] + ";" +
                    "-fx-background-radius: 15;" +
                    "-fx-effect: dropShadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 10)");

        // Kiểm tra và hiển thị ảnh từ dữ liệu sách
        String imageUrl = book.getImageSrc() != null ? book.getImageSrc() : "/design/Images/default_book.png";
        loadBookImage(imageUrl);

        // // Gắn sự kiện click để mở BookDetails
        // box.setOnMouseClicked(event -> {
        //     FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/bookDetails.fxml"));
        //     try {
        //         Parent bookDetailsRoot = fxmlLoader.load();
        //         BookDetailsController controller = fxmlLoader.getController();
        //         controller.setBookDetails(book); // Truyền đầy đủ dữ liệu sách, bao gồm description

        //         Stage stage = new Stage();
        //         stage.initStyle(StageStyle.UNDECORATED);
        //         stage.setScene(new Scene(bookDetailsRoot));
        //         stage.show();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // });
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
