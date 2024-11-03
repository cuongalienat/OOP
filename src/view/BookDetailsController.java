package view;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import library.*;

public class BookDetailsController implements Initializable {
    @FXML
    private ImageView image;

    @FXML
    private TextField borrowed;

    @FXML
    private TextField successfull;

    @FXML
    private TextArea bookDetails;

    private Book curBook;

    private BorrowedBooks curBorrowedBook;

    // @FXML
    // private ImageView bookImage;

    // khi click vào bookBox, sẽ lấy thông tin từ book để setBookDetails
    // test
    public void setBookDetails(Book book) {
        // Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        // bookImage.setImage(image);
        curBook = book;
        bookDetails.setText("Title: " + book.getName() + "\nAuthor: " + book.getAuthor());
        GoogleBooksAPI.searchBookByTitle(book.getName(), this::updateBookDetailsFromAPI);

    }

    public String updateBookDetailsFromAPI(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject volumeInfo = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

        String title = volumeInfo.getString("title");
        String authors = volumeInfo.getJSONArray("authors").getString(0);
        String description = volumeInfo.has("description") ? volumeInfo.getString("description") : "No description available.";
        String imageUrl = "";
        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            imageUrl = imageLinks.has("thumbnail") ? imageLinks.getString("thumbnail") : "No image available.";
        } else {
            imageUrl = "No image available.";
        }

        // Cập nhật TextArea với thông tin từ API
        bookDetails.setText("Title: " + title + "\nAuthor: " + authors + "\n\nDescription:\n" + description);
        loadBookImage(imageUrl);
        return imageUrl;
    }

    private void loadBookImage(String imageUrl) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl);
            }

            @Override
            protected void succeeded() {
                // Cập nhật ImageView với hình ảnh đã tải
                image.setImage(getValue());
            }

            @Override
            protected void failed() {
                // Xử lý lỗi nếu tải hình ảnh không thành công
                image.setImage(null); // hoặc một hình ảnh mặc định
            }
        };

        // Khởi động Task trong Thread mới
        new Thread(task).start();
    }

    protected BorrowedBooksController borrowedBooksController = new BorrowedBooksController();
    // protected static ObservableList<BorrowedBooks> borrowList =
    // FXCollections.observableArrayList();

    private boolean isFirstClick = true;

    @FXML

    public void borrow(MouseEvent event) throws Exception {
        if (isFirstClick) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận mượn sách");
            alert.setHeaderText("Bạn có chắc chắn muốn mượn sách này không?");
            alert.setContentText(curBook.getName()); // Hiển thị tiêu đề sách

            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now()); // Set default value to current date
            
            // Add the DatePicker to the Alert's content
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.getChildren().addAll( new javafx.scene.control.Label("Xác nhận ngày trả sách"),datePicker);
            alert.getDialogPane().setContent(vbox);
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Xóa ở database
                    try {
                        LocalDate selectedDate = datePicker.getValue();
                        if (selectedDate.isBefore(LocalDate.now())) {
                            Alert dateAlert = new Alert(Alert.AlertType.WARNING);
                            dateAlert.setHeaderText("Ngày trả sách không hợp lệ.");
                            dateAlert.setContentText("Vui lòng chọn ngày hợp lệ.");
                            dateAlert.showAndWait();
                            return;
                        } else if (selectedDate.isAfter(LocalDate.now().plusDays(31))) {
                            Alert dateAlert = new Alert(Alert.AlertType.WARNING);
                            dateAlert.setHeaderText("Ngày trả sách không hợp lệ.");
                            dateAlert.setContentText("Sách chỉ được mượn tối đa 31 ngày.");
                            dateAlert.showAndWait();
                            return;
                        }
                        else {
                            LocalDate bDate = LocalDate.now();
                            LocalDate dDate = datePicker.getValue();
                            //  showBorrwedStatus(successfull);
                            isFirstClick = false;
                            curBorrowedBook = new BorrowedBooks(curBook.getCollection(), curBook.getName(), curBook.getAuthor(),
                            curBook.getId(), curBook.getAvailable(), bDate, dDate, "Pending");
                            borrowedBooksController.addBorrowedBook(curBorrowedBook);
                            // if(curBook.getAvailable() > 0) {
                            //     Alert successAlert = new Alert(AlertType.INFORMATION);
                            //     successAlert.setHeaderText("Mượn sách thành công.");
                            //     successAlert.setContentText("Bạn đã mượn sách " + curBook.getName() + " thành công.");
                            //     successAlert.showAndWait();
                            // }
                            return;
                        }
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    }
                });
                return;
        }
            showBorrwedStatus(borrowed);
    }

    @FXML
    public void showBorrwedStatus(TextField textField) {
        textField.setVisible(true);
        // Appear gradually in 0.25s
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25), textField);
        fadeIn.setFromValue(0); // fully transparent
        fadeIn.setToValue(1);// opaque
        // after finishing fadeIn
        fadeIn.setOnFinished(e -> {
            // textField will display in 0.75s
            Timeline timeLine = new Timeline(new KeyFrame(Duration.seconds(0.75), ae -> {
                // Disappear gradually in 0.25s
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.25), textField);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(a -> textField.setVisible(false)); // after disappearing, set -> false
                fadeOut.play();
            }));
            timeLine.play();    
        });
        fadeIn.play();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        successfull.setVisible(false); // default
        borrowed.setVisible(false);
    }
}
