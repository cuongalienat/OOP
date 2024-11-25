package Controller;

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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import library.*;

/**
 * Controller for displaying detailed information about a specific book.
 */
public class BookDetailsController implements Initializable {
    @FXML
    private ImageView cancelButton;

    @FXML
    private ImageView image;

    @FXML
    private TextArea authorText;

    @FXML
    private TextArea nameText;

    @FXML
    private TextField borrowed;

    @FXML
    private TextField successfull;

    @FXML
    private TextArea bookDetails;

    private Book curBook;

    private BorrowedBooks curBorrowedBook;

    private double xOffset = 0;

    private double yOffset = 0;
    @FXML
    private AnchorPane rootPane;

    /**
     * Initializes the BookDetailsController.
     *
     * @param location  The location used to resolve relative paths for the root
     *                  object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        successfull.setVisible(false); // default
        borrowed.setVisible(false);
        // Set up the mouse pressed and dragged events for the root pane
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    // @FXML
    // private ImageView bookImage;

    // khi click vào bookBox, sẽ lấy thông tin từ book để setBookDetails
    // test

    /**
     * Sets the details of the book to be displayed.
     *
     * @param book The book whose details are to be displayed.
     */
    public void setBookDetails(Book book) {
        // Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        // bookImage.setImage(image);
        curBook = book;
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

        String title = volumeInfo.getString("title");
        String authors = volumeInfo.getJSONArray("authors").getString(0);
        String description = volumeInfo.has("description") ? volumeInfo.getString("description")
                : "No description available.";
        String imageUrl = "";
        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            imageUrl = imageLinks.has("thumbnail") ? imageLinks.getString("thumbnail") : "No image available.";
        } else {
            imageUrl = "/design/Images/default_book.png";

        }

        nameText.setText(title);
        authorText.setText(authors);
        bookDetails.setText(description);
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
                // Sử dụng kích thước của ImageView để resize hình ảnh
                double fitWidth = image.getFitWidth();
                double fitHeight = image.getFitHeight();
                return new Image(imageUrl, fitWidth, fitHeight, true, true); // True giữ tỷ lệ ảnh
            }
    
            @Override
            protected void succeeded() {
                // Cập nhật hình ảnh vào ImageView khi tải thành công
                image.setImage(getValue());
            }
    
            @Override
            protected void failed() {
                // Xử lý nếu không tải được ảnh, dùng ảnh mặc định
                image.setImage(new Image("/design/Images/default_book.png"));
            }
        };
    
        // Chạy task trong Thread mới để không làm UI bị treo
        new Thread(task).start();
    }
    

    protected BorrowedBooksController borrowedBooksController = new BorrowedBooksController();
    // protected static ObservableList<BorrowedBooks> borrowList =
    // FXCollections.observableArrayList();

    private boolean isFirstClick = true;

    /**
     * Handles the borrowing action for a book.
     *
     * @param event The mouse event triggering the borrow action.
     * @throws Exception If an error occurs during the borrowing process.
     */
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
            vbox.getChildren().addAll(new javafx.scene.control.Label("Xác nhận ngày trả sách"), datePicker);
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
                        } else {
                            LocalDate bDate = LocalDate.now();
                            LocalDate dDate = datePicker.getValue();
                            // showBorrwedStatus(successfull);
                            isFirstClick = false;
                            curBorrowedBook = new BorrowedBooks(curBook.getCollection(), curBook.getName(),
                                    curBook.getAuthor(),
                                    curBook.getId(), curBook.getAvailable(), bDate, dDate, "Pending");
                            borrowedBooksController.addBorrowedBook(curBorrowedBook);
                            // if(curBook.getAvailable() > 0) {
                            // Alert successAlert = new Alert(AlertType.INFORMATION);
                            // successAlert.setHeaderText("Mượn sách thành công.");
                            // successAlert.setContentText("Bạn đã mượn sách " + curBook.getName() + " thành
                            // công.");
                            // successAlert.showAndWait();
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

    /**
     * Displays the borrowed status with a fade transition.
     *
     * @param textField The text field to display the status in.
     */
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

    /**
     * Cancels the book details view.
     *
     * @param event The mouse event triggering the cancellation.
     */
    @FXML
    public void cancel(MouseEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
