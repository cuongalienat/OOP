package view;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.BorrowedBooks;

public class BorrowedBooksController {
    @FXML
    private TableColumn<BorrowedBooks, String> authorColumn;

    @FXML
    private TableView<BorrowedBooks> borrowedBooksTable;

    @FXML
    private TableColumn<BorrowedBooks, LocalDate> borrowedDateColumn;

    @FXML
    private TableColumn<BorrowedBooks, String> collectionColumn;

    @FXML
    private TableColumn<BorrowedBooks, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<BorrowedBooks, Integer> idColumn;

    @FXML
    private Button returnBookButton;

    @FXML
    private TableColumn<BorrowedBooks, String> titleColumn;

    @FXML
    private Button viewDetailsButton;

    public void addBorrowedBook(BorrowedBooks book, ObservableList<BorrowedBooks> borrowList) throws Exception {
        borrowList.add(book);
        book.addBorrowedBookToDB();
    }

    public void showBorrowedBooks() throws Exception {
        ObservableList<BorrowedBooks> borrowedList = BorrowedBooks.getAllBorrowedBooks();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        collectionColumn.setCellValueFactory(new PropertyValueFactory<>("collection"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        borrowedBooksTable.setItems(borrowedList); // Hiển thị danh sách sách đã mượn
    }
}
