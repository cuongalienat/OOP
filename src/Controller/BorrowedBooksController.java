package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import library.BorrowedBooks;
import library.DbConfig;

/**
 * Controller for managing borrowed books, including displaying and returning
 * books.
 */
public class BorrowedBooksController {
    @FXML
    private AnchorPane anchorPane;

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
    private TableColumn<BorrowedBooks, String> statusColumn;

    /**
     * Adds a borrowed book to the database.
     *
     * @param book The borrowed book to add.
     * @throws Exception If an error occurs during the database operation.
     */
    public void addBorrowedBook(BorrowedBooks book) throws Exception {
        book.addBorrowedBookToDB();
    }

    /**
     * Displays all borrowed books in the table view.
     *
     * @throws Exception If an error occurs while retrieving borrowed books.
     */
    public void showBorrowedBooks() throws Exception {
        ObservableList<BorrowedBooks> borrowedList = BorrowedBooks.getAllBorrowedBooks();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        collectionColumn.setCellValueFactory(new PropertyValueFactory<>("collection"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set the column resize policy to constrained resize policy
        borrowedBooksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set the preferred width for each column (proportional to the total width)
        idColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10); // 10% width
        collectionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15% width
        titleColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width
        authorColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15% width
        borrowedDateColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15% width
        dueDateColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15% width
        statusColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10); // 10% width

        borrowedBooksTable.setItems(borrowedList); // Hiển thị danh sách sách đã mượn
    }

    /**
     * Handles the return of a selected book.
     *
     * @param event The mouse event triggering the return.
     */
    @FXML
    public void returnBook(MouseEvent event) {
        BorrowedBooks selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (!"Pending".equals(selectedBook.getStatus())) {
                System.out.println("Không được trả sách này");
                Alert alert = new Alert(AlertType.WARNING);
                alert.setContentText("Vui lòng chọn sách có trạng thái 'pending'.");
                alert.showAndWait();
                return;
            }
            // Hiển thị hộp thoại xác nhận
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận trả sách");
            alert.setHeaderText("Bạn có chắc chắn muốn trả sách này không?");
            alert.setContentText(selectedBook.getName()); // Hiển thị tiêu đề sách

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Xóa ở database
                    try {
                        updateAvailableCount(selectedBook.getId());
                        deleteBookFromDatabase(selectedBook.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Xóa khỏi TableView
                    borrowedBooksTable.getItems().remove(selectedBook);
                    borrowedBooksTable.getSelectionModel().clearSelection();
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

    /**
     * Updates the available count of a book in the database.
     *
     * @param bookId The ID of the book to update.
     * @throws Exception If an error occurs during the update.
     */
    private void updateAvailableCount(int bookId) throws Exception {
        String updateAvailableQuery = "UPDATE book SET Available = Available + 1 WHERE ID = ?";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(updateAvailableQuery)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("Cập nhật Available thành công.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Available: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a book from the database.
     *
     * @param bookId The ID of the book to delete.
     * @throws Exception If an error occurs during the deletion.
     */
    public void deleteBookFromDatabase(int bookId) throws Exception {
        String query = "DELETE FROM booklogs WHERE book_id = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("deleted in database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        try {
            // Hiển thị danh sách sách đã mượn
            showBorrowedBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Xóa chọn khi click ra ngoài bảng
        anchorPane.setOnMouseClicked(event -> {
            // Kiểm tra nếu click không nằm trên bảng
            if (!borrowedBooksTable.isHover()) {
                borrowedBooksTable.getSelectionModel().clearSelection();
            }
        });
    }
}
