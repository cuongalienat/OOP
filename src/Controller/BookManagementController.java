package Controller;

import java.sql.Statement;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import javafx.util.converter.IntegerStringConverter;
import library.Book;
import library.BorrowedBooks;
import library.DbConfig;
import library.User;

import java.util.ArrayList;

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
    private int id_newbook;

    // private Set<Book> editedBooks = new HashSet<>();

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

    // private void addToEditedBooks(Book book) {
    // editedBooks.add(book);
    // System.out.println("da them sach duoc sua: " + book.getName());
    // }

    @FXML
    private void addNewBook(MouseEvent event) throws Exception {
        id_newbook = generateNewId();
        Book newBook = new Book("", "", "", id_newbook, 0);
        Platform.runLater(() -> {
            bookManagementTableView.getItems().add(newBook);
            bookManagementTableView.scrollTo(newBook);
            System.out.println("Thêm dòng thành công");
        });
    }

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

        if (book.getId() == id_newbook) {
            if (isBookDataComplete(book)) {
                try {
                    book.addData();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm sách vào cơ sở dữ liệu.");
                }
            }
        } else {
            try {
                book.updateBookInDatabase(); // Cập nhật sách đã có ID vào cơ sở dữ liệu
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật sách trong cơ sở dữ liệu.");
            }
        }
    }

    private boolean isBookDataComplete(Book book) {
        return book.getName() != null && !book.getName().isEmpty() &&
                book.getAuthor() != null && !book.getAuthor().isEmpty() &&
                book.getCollection() != null && !book.getCollection().isEmpty() &&
                book.getAvailable() != 0;
    }

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

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private int generateNewId() throws Exception {

        String query = "SELECT MAX(ID) FROM book";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1; // Tăng ID lớn nhất thêm 1
            } else {
                return 1; // Nếu bảng trống, bắt đầu từ ID 1
            }
        }
    }

    @FXML
    void Search(MouseEvent event) {
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

    @FXML
    void cancel(ActionEvent e) {
        if (e.getSource() == cancelSearch) {
            setBookData(allBooks);
        }
    }
}