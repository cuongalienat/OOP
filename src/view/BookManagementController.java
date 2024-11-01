package view;

import java.sql.Statement;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import library.Book;
import library.BorrowedBooks;
import library.DbConfig;
import library.User;
import java.util.HashSet;
import java.util.Set;


public class BookManagementController implements Initializable {
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

    // private Set<Book> editedBooks = new HashSet<>();

    public void setBookData(List<Book> bookData) {
        // Chuyển danh sách sách thành ObservableList để hiển thị trong TableView
        ObservableList<Book> books = FXCollections.observableArrayList(bookData);
    
        // Đặt dữ liệu vào TableView
        bookManagementTableView.setItems(books);
    }

    // private void addToEditedBooks(Book book) {
    //     editedBooks.add(book);
    //     System.out.println("da them sach duoc sua: " + book.getName());
    // }

    @FXML
    private void addNewBook(MouseEvent event) {
        Book newBook = new Book("", "", "", 0, 0); // Tạo sách mới với ID = 0
        Platform.runLater(() -> {
            bookManagementTableView.getItems().add(newBook);
            bookManagementTableView.scrollTo(newBook);
            System.out.println("Thêm dòng thành công");
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập các cột có thể chỉnh sửa
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

        setBookData(Book.getLibrary());
    }

    private <T> void handleEditCommit(TableColumn.CellEditEvent<Book, T> event) {
        Book book = event.getRowValue();
        T newValue = event.getNewValue();

        // Cập nhật giá trị cột phù hợp
        if (event.getTableColumn() == idColManage) {
            book.setId((int) newValue);
        }
        if (event.getTableColumn() == bookTitleColManage) {
            book.setName((String) newValue);
        } else if (event.getTableColumn() == contributorsColManage) {
            book.setAuthor((String) newValue);
        } else if (event.getTableColumn() == collectionColManage) {
            book.setCollection((String) newValue);
        } else if (event.getTableColumn() == availableColManage) {
            book.setAvailable((Integer) newValue);
        }

        // Kiểm tra nếu sách mới, thêm vào cơ sở dữ liệu, nếu không thì cập nhật
        if (book.getId() == 0) { // Nếu ID là 0, tức là sách mới
            try {
                int generatedId = insertBookToDatabase(book); // Thêm vào CSDL và lấy ID mới
                book.setId(generatedId); // Cập nhật ID cho đối tượng Book
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm sách vào cơ sở dữ liệu.");
            }
        } else {
            try {
                updateBookInDatabase(book); // Cập nhật sách đã có ID vào cơ sở dữ liệu
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật sách trong cơ sở dữ liệu.");
            }
        }
    }

    public void remove(MouseEvent event) throws Exception {
        Book selectedBook = bookManagementTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                deleteBookFromDatabase(selectedBook.getId());
                bookManagementTableView.getItems().remove(selectedBook);
                bookManagementTableView.getSelectionModel().clearSelection();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Lỗi", "Không thể xóa sách.");
            }
        } else {
            showAlert(AlertType.WARNING, "Không có sách nào được chọn.", "Vui lòng chọn một sách để xóa.");
        }
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void deleteBookFromDatabase(int bookId) throws Exception {
        String query = "DELETE FROM book WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("deleted in database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    
    private int insertBookToDatabase(Book book) throws Exception {
        int newId = generateNewId(); // Gọi phương thức để tạo ID mới
        String query = "INSERT INTO book (`ID`, `Offer Collection`, `Book Title`, `Contributors`, `Available`) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, newId); // Sử dụng ID mới tạo
            stmt.setString(2, book.getCollection());
            stmt.setString(3, book.getName());
            stmt.setString(4, book.getAuthor());
            stmt.setInt(5, book.getAvailable());
            stmt.executeUpdate();
    
            book.setId(newId); // Cập nhật ID vào đối tượng book
            return newId;
        }
    }
    

    @FXML
    private void updateAllBooks(MouseEvent event) {
        // if (editedBooks.isEmpty()) {
        //     showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không có thay đổi nào để cập nhật.");
        //     return;
        // }
        // for (Book book : editedBooks) {
        //     try {
        //         updateBookInDatabase(book); 
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật sách có ID: " + book.getId());
        //     }
        // }
        // // Xóa danh sách sách đã chỉnh sửa sau khi cập nhật thành công
        // editedBooks.clear();
        // showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thông tin sách đã được cập nhật.");
    }
    
    private void updateBookInDatabase(Book book) throws Exception {
        String query = "UPDATE book SET `Book Title` = ?, Contributors = ?, `Offer Collection` = ?, Available = ? WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCollection());
            stmt.setInt(4, book.getAvailable());
            stmt.setInt(5, book.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated book with ID: " + book.getId());
            } else {
                System.out.println("No rows updated for book with ID: " + book.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating book with ID: " + book.getId());
        }
    }    
}