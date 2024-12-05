package Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
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

    /**
     * Sets the details of the book to be displayed.
     *
     * @param book The book whose details are to be displayed.
     */
    public void setBookDetails(Book book) {
        curBook = book;
    
        nameText.setText(book.getName());
        authorText.setText(book.getAuthor());
        bookDetails.setText(book.getDescription()); // Nếu không có thông tin mô tả trong DB
        
        
        // Kiểm tra và hiển thị ảnh
        String imageUrl = book.getImageSrc() != null ? book.getImageSrc() : "/design/Images/default_book.png";
        loadBookImage(imageUrl);
    }

    /**
     * Loads the book image asynchronously.
     *
     * @param imageUrl The URL of the image to load.
     */
    private void loadBookImage(String imageUrl) {
        double fixedWidth = 202;
        double fixedHeight = 324;
    
        image.setFitWidth(fixedWidth);
        image.setFitHeight(fixedHeight);
    
        // Tạo Task để tải ảnh
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, fixedWidth, fixedHeight, true, true);
            }
    
            @Override
            protected void succeeded() {
                image.setImage(getValue());
            }
    
            @Override
            protected void failed() {
                image.setImage(new Image("/design/Images/default_book.png", fixedWidth, fixedHeight, true, true));
            }
        };
    
        image.setPreserveRatio(false); 
        image.setSmooth(true);
    
        Rectangle clip = new Rectangle(fixedWidth, fixedHeight);
        clip.setArcWidth(10);  // Bo góc
        clip.setArcHeight(10);
        image.setClip(clip);
    
        // Chạy Task trong Thread mới
        new Thread(task).start();
    }    

    protected BorrowedBooksController borrowedBooksController = new BorrowedBooksController();
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
            datePicker.setValue(LocalDate.now()); 

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.getChildren().addAll(new javafx.scene.control.Label("Xác nhận ngày trả sách"), datePicker);
            alert.getDialogPane().setContent(vbox);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
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
                            isFirstClick = false;
                            curBorrowedBook = new BorrowedBooks(curBook.getCollection(), curBook.getName(),
                                    curBook.getAuthor(),
                                    curBook.getId(), curBook.getAvailable(), bDate, dDate, "Pending");
                            borrowedBooksController.addBorrowedBook(curBorrowedBook);
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
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25), textField);
        fadeIn.setFromValue(0); 
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> {
            Timeline timeLine = new Timeline(new KeyFrame(Duration.seconds(0.75), ae -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.25), textField);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(a -> textField.setVisible(false)); 
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
