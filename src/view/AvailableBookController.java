package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import library.Book;
import library.BorrowedBooks;
import library.User;

public class AvailableBookController {
    @FXML
    private TextField Search;

    @FXML
    private ComboBox<String> SearchOptions;

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

    @FXML
    private Button Cancel;

    private ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    void borrowBook(MouseEvent event) {
        Book selectedBook = availableBook_tableview.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Hiển thị hộp thoại xác nhận
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận mượn sách");
            alert.setHeaderText("Bạn có chắc chắn muốn mượn sách này không?");
            alert.setContentText(selectedBook.getName()); // Hiển thị tiêu đề sách

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Xóa ở database
                    try {
                        selectedBook.addBorrowedBookToDB();
                        this.setBookData(Book.getAvailableBooks());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Xóa khỏi TableView
                    // availableBook_tableview.getItems().remove(selectedBook);
                    // availableBook_tableview.getSelectionModel().clearSelection();
                }
            });
        } else {
            // Nếu chưa chọn quyển nào
            Alert alert = new Alert(AlertType.WARNING);
            // alert.setTitle("");
            alert.setHeaderText("Không có sách nào được chọn.");
            alert.setContentText("Vui lòng chọn một sách để xóa.");
            alert.showAndWait();
        }
    }

    public void setBookData(List<Book> bookData) {
        ObservableList<String> searchOptions = FXCollections.observableArrayList("Title", "Collection", "Contributor");
        SearchOptions.setItems(searchOptions);
        books = FXCollections.observableArrayList(bookData);

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

    @FXML
    void Search(MouseEvent event) {

        // Kiểm tra giá trị tìm kiếm có trống không
        if (Search.getText().trim().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Vui lòng nhập từ khóa tìm kiếm.");
            alert.showAndWait();
            return;
        }

        List<Book> result = new ArrayList<>();

        if ("Title".equals(SearchOptions.getValue())) {
            result = Book.searchBookByTitle(Search.getText().trim());
        } else if ("Collection".equals(SearchOptions.getValue())) {
            result = Book.searchBookByCollections(Search.getText().trim());
        } else if ("Contributor".equals(SearchOptions.getValue())) {
            result = Book.searchBookByAuthor(Search.getText().trim());
        }

        ObservableList<Book> books = FXCollections.observableArrayList(result);
        availableBook_tableview.setItems(books);
        Search.clear();
    }

    @FXML
    void Cancel(ActionEvent event) {
        if (event.getSource() == Cancel) {
            setBookData(books);
        }
    }
}
