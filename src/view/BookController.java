package view;

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

    BookDetailsController bookDetailsController = new BookDetailsController();
    public void setData(Book book) {
        // Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        // bookImage.setImage(image);
        GoogleBooksAPI.searchBookByTitle(book.getName(), this::updateBookDetailsFromAPI);
    }

    public String updateBookDetailsFromAPI(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject volumeInfo = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

        String title = volumeInfo.getString("title");
        String authors = volumeInfo.getJSONArray("authors").getString(0);
        String imageUrl = "";
        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            imageUrl = imageLinks.has("thumbnail") ? imageLinks.getString("thumbnail") : "No image available.";
        } else {
            imageUrl = "/design/Images/default_book.png";
        }

        bookName.setText(title);
        authorName.setText(authors.isEmpty() ? "Unknown Author" : authors);
        loadBookImage(imageUrl);
        return imageUrl;
    }

    private void loadBookImage(String imageUrl) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, bookImage.getFitWidth(), bookImage.getFitHeight(), true, true);
            }
    
            @Override
            protected void succeeded() {
                // Cập nhật ImageView với hình ảnh đã tải
                bookImage.setImage(getValue());
            }
    
            @Override
            protected void failed() {
                // Xử lý lỗi nếu tải hình ảnh không thành công
                bookImage.setImage(null); // hoặc một hình ảnh mặc định
            }
        };
    
        // Khởi động Task trong Thread mới
        new Thread(task).start();
    }

    public void initialize() {
        // Set fixed size
        bookImage.setFitWidth(146.0);
        bookImage.setFitHeight(227.0);
        bookImage.setPreserveRatio(false);
        bookImage.setSmooth(true);
        
        // Add clipping to maintain exact dimensions
        Rectangle clip = new Rectangle(146, 227);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        bookImage.setClip(clip);
    }
}