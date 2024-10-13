package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    @FXML
    private ImageView bookImage;

    //khi click vào bookBox, sẽ lấy thông tin từ book để setBookDetails
    //test
    public void setBookDetails(Book book) {
        Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);
        bookDetails.setText("Title: " + book.getName() + "\nAuthor: " + book.getAuthor());
    }

    private boolean isFirstClick = true;

    @FXML

    public void borrow(MouseEvent event) {
        if(isFirstClick) {
            showBorrwedStatus(successfull);
            isFirstClick = false;
        }
        else {
            showBorrwedStatus(borrowed);
        }
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

