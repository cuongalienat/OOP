package view;

import java.io.IOException;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
<<<<<<< HEAD
=======
import javafx.stage.Stage;
>>>>>>> eded8427ebed24f587498b4bb57f713bd8b17151
import library.Book;

import java.net.URL;

public class HelloController implements Initializable {

    @FXML
    private HBox cardLayout;

    @FXML
    private GridPane bookContainer;

    @FXML
    private Label app_Name;

    private List<Book> recentlyAdded;
    private List<Book> recommended;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recentlyAdded = new ArrayList<>(recentlyAdded());
        recommended = new ArrayList<>(books());
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < recentlyAdded.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("card.fxml"));
                HBox cardBox = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(recentlyAdded.get(i));
                cardLayout.getChildren().add(cardBox);
            }
            for (Book book : recommended) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("book.fxml"));
                VBox bookBox = fxmlLoader.load();
                BookController bookController = fxmlLoader.getController();
                bookController.setData(book);
<<<<<<< HEAD
=======
                bookBox.setOnMouseClicked(event -> showBookDetails(book));
>>>>>>> eded8427ebed24f587498b4bb57f713bd8b17151
                if (column == 6) {
                    column = 0;
                    row++;
                }
                bookContainer.add(bookBox, column++, row);
                GridPane.setMargin(bookBox, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setName(String user_Name) {
        app_Name.setText(user_Name);
    }

    private List<Book> recentlyAdded() {
        List<Book> ls = new ArrayList<>();
        Book book = new Book();
        book.setName("THE ROAD");
        book.setImageSrc("/design/Images/theroad.png");
        book.setAuthor("NguyenTT");
        ls.add(book);

        Book book1 = new Book();
        book1.setName("THE WITCHES");
        book1.setImageSrc("/design/Images/the-witches.png");
        book1.setAuthor("Roald Dahl");
        ls.add(book1);

        Book book2 = new Book();
        book2.setName("THE FAMOUS FIVE");
        book2.setImageSrc("/design/Images/the-famous-five.png");
        book2.setAuthor("Gui Blyton");
        ls.add(book2);
        ls.add(book1);

        return ls;
    }

    private void showBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            // Lấy controller của bookDetails.fxml và truyền thông tin sách vào
            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.setBookDetails(book);

            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<Book> books() {
        List<Book> ls = new ArrayList<>();
        Book book = new Book();
        book.setName("THE ROAD");
        book.setImageSrc("/design/Images/theroad.png");
        book.setAuthor("NguyenTT");
        ls.add(book);

        Book book1 = new Book();
        book1.setName("THE WITCHES");
        book1.setImageSrc("/design/Images/the-witches.png");
        book1.setAuthor("Roald Dahl");
        ls.add(book1);

        Book book2 = new Book();
        book2.setName("THE FAMOUS FIVE");
        book2.setImageSrc("/design/Images/the-famous-five.png");
        book2.setAuthor("Gui Blyton");
        ls.add(book2);
        ls.add(book1);

        Book book3 = new Book();
        book3.setName("THE ROAD");
        book3.setImageSrc("/design/Images/theroad.png");
        book3.setAuthor("NguyenTT");
        ls.add(book3);

        Book book4 = new Book();
        book4.setName("THE WITCHES");
        book4.setImageSrc("/design/Images/the-witches.png");
        book4.setAuthor("Roald Dahl");
        ls.add(book4);

        Book book5 = new Book();
        book5.setName("THE FAMOUS FIVE");
        book5.setImageSrc("/design/Images/the-famous-five.png");
        book5.setAuthor("Gui Blyton");
        ls.add(book5);
        ls.add(book1);
        return ls;
    }
}