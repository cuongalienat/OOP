package Controller;

import org.json.JSONObject;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
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

    public void setData(Book book) {
        // Cập nhật giao diện từ thông tin sách
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
    
        // Kiểm tra và hiển thị ảnh bìa
        String imageUrl = book.getImageSrc() != null ? book.getImageSrc() : "/design/Images/default_book.png";
        loadBookImage(imageUrl);
    }

    private void loadBookImage(String imageUrl) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                // Tải ảnh với kích thước của ImageView từ API
                return new Image(imageUrl, bookImage.getFitWidth(), bookImage.getFitHeight(), true, true);
            }

            @Override
            protected void succeeded() {
                // Cập nhật ảnh cho ImageView khi tải thành công
                bookImage.setImage(getValue());
            }

            @Override
            protected void failed() {
                // Nếu tải ảnh thất bại, dùng ảnh mặc định
                bookImage.setImage(new Image("/design/Images/default_book.png"));
            }
        };

        // Chạy task trên Thread mới
        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        bookImage.setFitWidth(121.0);
        bookImage.setFitHeight(147.0);
        bookImage.setPreserveRatio(false); // Cho phép hình ảnh co giãn để lấp đầy ImageView
        bookImage.setSmooth(true);

        // Thêm clipping để bo góc hình ảnh
        Rectangle clip = new Rectangle(121.0, 147.0);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}
