package Controller;

import org.json.JSONObject;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import library.Book;
import library.GoogleBooksAPI;

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
        bookName.setText(book.getName());
        authorName.setText(book.getAuthor());
        box.setStyle("-fx-background-color: #" + colors[(int) (Math.random() * colors.length)] + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropShadown(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 10)");
        GoogleBooksAPI.searchBookByTitle(book.getName(), this::updateBookDetailsFromAPI);
    }

    public String updateBookDetailsFromAPI(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject volumeInfo = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

        // Lấy dữ liệu từ API
        String title = volumeInfo.getString("title");
        String authors = volumeInfo.getJSONArray("authors").getString(0);
        String imageUrl;

        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            imageUrl = imageLinks.optString("thumbnail", "/design/Images/default_book.png");
        } else {
            imageUrl = "/design/Images/default_book.png";
        }

        // Cập nhật giao diện với dữ liệu nhận được
        bookName.setText(title);
        authorName.setText(authors.isEmpty() ? "Unknown Author" : authors);
        loadBookImage(imageUrl);
        return imageUrl;
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
        // Thiết lập kích thước cố định cho `bookImage` từ FXML
        bookImage.setFitWidth(119.0);
        bookImage.setFitHeight(103.0);
        bookImage.setPreserveRatio(true); // Giữ tỷ lệ hình ảnh
        bookImage.setSmooth(true);

        // Thêm clipping để cắt gọn hình ảnh
        Rectangle clip = new Rectangle(121.0, 147.0);
        clip.setArcWidth(10);  // Bo góc trên và dưới
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}
