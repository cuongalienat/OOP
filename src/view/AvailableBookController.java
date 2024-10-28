package view;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import library.Book;
import library.User;

public class AvailableBookController {
    @FXML
    private TableView<Book> availableBook_tableview;

    @FXML
    private TableColumn<Book, Integer> available_col;

    @FXML
    private TableColumn<Book, String> bookTitle_col;

    @FXML
    private TableColumn<Book, String> contributors_col;

    @FXML
    private TableColumn<Book, Integer> id_col;

    @FXML
    private TableColumn<Book, String> offerCollection_col;

    public void setBookData(List<Book> bookData) {
        ObservableList<Book> books = FXCollections.observableArrayList(bookData);

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitle_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        contributors_col.setCellValueFactory(new PropertyValueFactory<>("author"));
        available_col.setCellValueFactory(new PropertyValueFactory<>("available"));
        offerCollection_col.setCellValueFactory(new PropertyValueFactory<>("collection"));

        availableBook_tableview.setItems(books);
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

    public void initialize() {
        availableBook_tableview.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Kiểm tra nếu nhấp đúp
                Book selectedBook = availableBook_tableview.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    showBookDetails(selectedBook);
                }
            }
        });
    }
}
