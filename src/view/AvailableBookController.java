package view;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import library.Book;
import library.BorrowedBooks;
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
                    //availableBook_tableview.getItems().remove(selectedBook);
                   // availableBook_tableview.getSelectionModel().clearSelection();
                }
            });
        } else {
            //Nếu chưa chọn quyển nào
            Alert alert = new Alert(AlertType.WARNING);
            //alert.setTitle("");
            alert.setHeaderText("Không có sách nào được chọn.");
            alert.setContentText("Vui lòng chọn một sách để xóa.");
            alert.showAndWait();
        }
    }
    public void setBookData(List<Book> bookData) {
        ObservableList<Book> books = FXCollections.observableArrayList(bookData);

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitle_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        contributors_col.setCellValueFactory(new PropertyValueFactory<>("author"));
        available_col.setCellValueFactory(new PropertyValueFactory<>("available"));
        offerCollection_col.setCellValueFactory(new PropertyValueFactory<>("collection"));

        availableBook_tableview.setItems(books);
    }
}
