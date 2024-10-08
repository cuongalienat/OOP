package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import view.Book;

public class BookDetailsController implements Initializable{

    @FXML
    private TextField successfull;

    @FXML
    private TextArea bookDetails;

    @FXML
    private ImageView bookImage;

    //khi click vào bookBox, sẽ lấy thông tin từ book để setBookDetails
    public void setBookDetails(Book book) {
        Image image = new Image(getClass().getResourceAsStream(book.getImageSrc()));
        bookImage.setImage(image);
        bookDetails.setText("Title: " + book.getName() + "\nAuthor: " + book.getAuthor());
    }

    @FXML

    void borrow(MouseEvent event) {
        successfull.setVisible(true);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        successfull.setVisible(false);
    }
}

