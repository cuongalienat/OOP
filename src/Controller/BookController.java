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
        // Lấy dữ liệu từ API và cập nhật thông tin sách
        GoogleBooksAPI.searchBookByTitle(book.getName(), this::updateBookDetailsFromAPI);
    }

    /**
     * Updates book details from the API response.
     *
     * @param jsonResponse The JSON response from the Google Books API.
     * @return The URL of the book image.
     */
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

    /**
     * Loads the book image asynchronously.
     *
     * @param imageUrl The URL of the image to load.
     */
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

    /**
     * Initializes the BookController.
     */
    public void initialize() {
        // Thiết lập kích thước cố định cho `bookImage` từ FXML
        bookImage.setFitWidth(121.0);
        bookImage.setFitHeight(147.0);
        bookImage.setPreserveRatio(true); // Giữ tỷ lệ hình ảnh
        bookImage.setSmooth(true);

        // Thêm clipping để cắt gọn hình ảnh
        Rectangle clip = new Rectangle(121.0, 147.0);
        clip.setArcWidth(10);  // Bo góc trên và dưới
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}
