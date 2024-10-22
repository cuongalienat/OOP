package view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import library.*;

public class BookDetailsController implements Initializable{

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

    //khi click vào bookBox, sẽ lấy thông tin từ book để setBookDetails
    //test
    public void setBookDetails(Book book) {
        // Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        // bookImage.setImage(image);
        curBook = book;
        bookDetails.setText("Title: " + book.getName() + "\nAuthor: " + book.getAuthor());
    }

    protected BorrowedBooksController borrowedBooksController = new BorrowedBooksController();
    protected static ObservableList<BorrowedBooks> borrowList = FXCollections.observableArrayList();

    private boolean isFirstClick = true;

    @FXML

    public void borrow(MouseEvent event) throws Exception {
        LocalDate bDate = LocalDate.now();
        LocalDate dDate = bDate.plusWeeks(2);
        if(isFirstClick) {
            showBorrwedStatus(successfull);
            isFirstClick = false;
            curBorrowedBook = new BorrowedBooks(curBook.getCollection(), curBook.getName(), curBook.getAuthor(), curBook.getId(), curBook.getAvailable(), bDate, dDate);
            borrowedBooksController.addBorrowedBook(curBorrowedBook, borrowList);
            return;
        }
        showBorrwedStatus(borrowed);
    }

    @FXML
    public void showBorrwedStatus(TextField textField) {
        textField.setVisible(true);
        //Appear gradually in 0.25s
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25), textField);
        fadeIn.setFromValue(0); //fully transparent
        fadeIn.setToValue(1);//opaque
        //after finishing fadeIn
        fadeIn.setOnFinished(e -> {
            //textField will display in 0.75s
            Timeline timeLine = new Timeline(new KeyFrame(Duration.seconds(0.75), ae -> {
                //Disappear gradually in 0.25s  
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.25), textField);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(a -> textField.setVisible(false)); //after disappearing, set -> false
                fadeOut.play();
            }));
            timeLine.play();    
        });
        fadeIn.play();
    }
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        successfull.setVisible(false); //default
        borrowed.setVisible(false);
    }
}

