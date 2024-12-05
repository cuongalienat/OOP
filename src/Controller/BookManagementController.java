package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import library.Book;
import library.BorrowedBooks;
import library.DbConfig;

import java.util.ArrayList;

/**
 * Controller for managing book-related operations, including adding, updating,
 * and removing books.
 */
public class BookManagementController {
    @FXML
    private TableView<Book> bookManagementTableView;

    @FXML
    private TableColumn<Book, Integer> availableColManage;

    @FXML
    private TableColumn<Book, String> bookTitleColManage;

    @FXML
    private TableColumn<Book, String> collectionColManage;

    @FXML
    private TableColumn<Book, String> contributorsColManage;

    @FXML
    private TableColumn<Book, Integer> idColManage;

    @FXML
    private Button removeBook;

    @FXML
    private Button updateBook;

    @FXML
    private Button addBook;

    @FXML
    private ComboBox<String> searchOptions;

    @FXML
    private Button cancelSearch;

    @FXML
    private TextField Search;

    private ObservableList<Book> allBooks = FXCollections.observableArrayList();
    // private int id_newbook;

    // private Set<Book> editedBooks = new HashSet<>();

    /**
     * Sets the data for the book management table.
     *
     * @param bookData The list of books to display.
     */
    public void setBookData(List<Book> bookData) {
        // Chuyển danh sách sách thành ObservableList để hiển thị trong TableView
        ObservableList<Book> books = FXCollections.observableArrayList(bookData);
        allBooks = FXCollections.observableArrayList(bookData);
        // Đặt dữ liệu vào TableView
        bookManagementTableView.setItems(books);
        bookManagementTableView.setEditable(true);

        // Các thiết lập khác cho TableView và cột
        idColManage.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bookTitleColManage.setCellFactory(TextFieldTableCell.forTableColumn());
        contributorsColManage.setCellFactory(TextFieldTableCell.forTableColumn());
        collectionColManage.setCellFactory(TextFieldTableCell.forTableColumn());
        availableColManage.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        idColManage.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColManage.setCellValueFactory(new PropertyValueFactory<>("name"));
        contributorsColManage.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColManage.setCellValueFactory(new PropertyValueFactory<>("available"));
        collectionColManage.setCellValueFactory(new PropertyValueFactory<>("collection"));

        idColManage.setOnEditCommit(event -> handleEditCommit(event));
        bookTitleColManage.setOnEditCommit(event -> handleEditCommit(event));
        contributorsColManage.setOnEditCommit(event -> handleEditCommit(event));
        collectionColManage.setOnEditCommit(event -> handleEditCommit(event));
        availableColManage.setOnEditCommit(event -> handleEditCommit(event));

        bookManagementTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idColManage.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        collectionColManage.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        bookTitleColManage.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        contributorsColManage.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        availableColManage.setMaxWidth(1f * Integer.MAX_VALUE * 15);

        ObservableList<String> SearchOptions = FXCollections.observableArrayList("Id", "Title", "Collections",
                "Contributors");
        searchOptions.setItems(SearchOptions);
    }

    /**
     * Handles the addition of a new book.
     *
     * @param event The mouse event triggering the addition.
     * @throws Exception If an error occurs during the addition.
     */

    /**
     * Handles editing commits in the table view.
     *
     * @param event The cell edit event.
     * @param <T>   The type of the edited value.
     */
    private <T> void handleEditCommit(TableColumn.CellEditEvent<Book, T> event) {
        Book book = event.getRowValue();
        T newValue = event.getNewValue();

        if (event.getTableColumn() == bookTitleColManage) {
            book.setName((String) newValue);
        } else if (event.getTableColumn() == contributorsColManage) {
            book.setAuthor((String) newValue);
        } else if (event.getTableColumn() == collectionColManage) {
            book.setCollection((String) newValue);
        } else if (event.getTableColumn() == availableColManage) {
            book.setAvailable((Integer) newValue);
        }

        try {
            book.updateBookInDatabase(); // Cập nhật sách đã có ID vào cơ sở dữ liệu
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật sách trong cơ sở dữ liệu.");
        }
    }

    // private boolean isBookDataComplete(Book book) {
    //     return book.getName() != null && !book.getName().isEmpty() &&
    //             book.getAuthor() != null && !book.getAuthor().isEmpty() &&
    //             book.getCollection() != null && !book.getCollection().isEmpty() &&
    //             book.getAvailable() != 0;
    // }

    /**
     * Removes a selected book from the management table and database.
     *
     * @param event The mouse event triggering the removal.
     * @throws Exception If an error occurs during the removal.
     */
    public void remove(MouseEvent event) throws Exception {
        Book selectedBook = bookManagementTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(AlertType.WARNING, "Không có sách nào được chọn.", "Vui lòng chọn một sách để xóa.");
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure to delete this book ?");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!BorrowedBooks.CheckBookBeforeDelete(selectedBook.getId())) {
                try {
                    selectedBook.deleteBookFromDatabase(selectedBook.getId());
                    bookManagementTableView.getItems().remove(selectedBook);
                    bookManagementTableView.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Lỗi", "Không thể xóa sách.");
                }
                return;
            } else {
                Alert alertFail = new Alert(AlertType.ERROR);
                alertFail.setContentText("This book can't be deleted if it is borrowed or overdue.");
                alertFail.setHeaderText(null);
                alertFail.showAndWait(); // Đảm bảo thông báo lỗi được hiển thị
            }
        }
    }

    /**
     * Displays an alert dialog.
     *
     * @param alertType The type of the alert.
     * @param title     The title of the alert.
     * @param content   The content message of the alert.
     */
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Handles the search functionality based on user input.
     *
     * @param event The mouse event triggering the search.
     */
    @FXML
    void Search(MouseEvent event) throws Exception {
        if (Search.getText().trim().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Vui lòng nhập từ khóa tìm kiếm.");
            alert.showAndWait();
            return;
        }

        List<Book> result = new ArrayList<>();

        if ("Title".equals(searchOptions.getValue())) {
            result = Book.searchBookByTitle(Search.getText().trim());
        } else if ("Collection".equals(searchOptions.getValue())) {
            result = Book.searchBookByCollections(Search.getText().trim());
        } else if ("Contributors".equals(searchOptions.getValue())) {
            result = Book.searchBookByAuthor(Search.getText().trim());
        } else if ("Id".equals(searchOptions.getValue())) {
            try {
                int id = Integer.parseInt(Search.getText().trim());
                result = Book.searchBookByID(id);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Invalid Input, Id should be a valid integer.");
                alert.showAndWait();
                return;
            }
        }

        ObservableList<Book> books = FXCollections.observableArrayList(result);
        bookManagementTableView.setItems(books);
        Search.clear();
    }

    /**
     * Cancels the current search and resets the table view.
     *
     * @param e The action event triggering the cancellation.
     */
    @FXML
    void cancel(ActionEvent e) {
        if (e.getSource() == cancelSearch) {
            setBookData(allBooks);
        }
    }

    @FXML
    private void openSearchApiWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/searchBook.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Search Books via API");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load API search window.");
        }
    }
}