package view;

import java.io.IOException;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    void borrowBook(MouseEvent event) throws Exception {
        User user_now = loginController.getUser_now();
        if (BorrowedBooks.CheckBookBeforeBorrow(user_now.getPhone())) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setContentText("You need to return all overdue book to borrow new !");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }
        Book selectedBook = availableBook_tableview.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Hiển thị hộp thoại xác nhận
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận mượn sách");
            alert.setHeaderText("Bạn có chắc chắn muốn mượn sách này không?");
            alert.setContentText(selectedBook.getName()); // Hiển thị tiêu đề sách

            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now()); // Set default value to current date

            // Add the DatePicker to the Alert's content
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.getChildren().addAll(new javafx.scene.control.Label("Xác nhận ngày trả sách"), datePicker);
            alert.getDialogPane().setContent(vbox);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Xóa ở database
                    try {
                        LocalDate selectedDate = datePicker.getValue();
                        if (selectedDate.isBefore(LocalDate.now())) {
                            Alert dateAlert = new Alert(AlertType.WARNING);
                            dateAlert.setHeaderText("Ngày trả sách không hợp lệ.");
                            dateAlert.setContentText("Vui lòng chọn ngày trả sách hợp lệ.");
                            dateAlert.showAndWait();
                            return;
                        } else if (selectedDate.isAfter(LocalDate.now().plusDays(31))) {
                            Alert dateAlert = new Alert(AlertType.WARNING);
                            dateAlert.setHeaderText("Ngày mượn không hợp lệ.");
                            dateAlert.setContentText("Sách chỉ được mượn tối đa 31 ngày.");
                            dateAlert.showAndWait();
                            return;
                        }
                        selectedBook.addBorrowedBookToDB(selectedDate);
                        this.setBookData(Book.getAvailableBooks());
                        Alert successAlert = new Alert(AlertType.INFORMATION);
                        successAlert.setHeaderText("Mượn sách thành công.");
                        successAlert.setContentText("Bạn đã mượn sách " + selectedBook.getName() + " thành công.");
                        successAlert.showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Xóa khỏi TableView
                    // availableBook_tableview.getItems().remove(selectedBook);
                    // availableBook_tableview.getSelectionModel().clearSelection();
                }
            });
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            // alert.setTitle("");
            alert.setHeaderText("Không có sách nào được chọn.");
            alert.setContentText("Vui lòng chọn một sách để mượn.");
            alert.showAndWait();
        }
    }

    public void setBookData(List<Book> bookData) {
        ObservableList<String> searchOptions = FXCollections.observableArrayList("Id", "Title", "Collection",
                "Contributors");
        SearchOptions.setItems(searchOptions);
        books = FXCollections.observableArrayList(bookData);

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitle_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        contributors_col.setCellValueFactory(new PropertyValueFactory<>("author"));
        available_col.setCellValueFactory(new PropertyValueFactory<>("available"));
        offerCollection_col.setCellValueFactory(new PropertyValueFactory<>("collection"));

        // Set the column resize policy to constrained resize policy
        availableBook_tableview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set the preferred width for each column (proportional to the total width)
        id_col.setMaxWidth(1f * Integer.MAX_VALUE * 10); // 10% width
        offerCollection_col.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width
        bookTitle_col.setMaxWidth(1f * Integer.MAX_VALUE * 30); // 30% width
        contributors_col.setMaxWidth(1f * Integer.MAX_VALUE * 25); // 25% width
        available_col.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15% width

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
            stage.initStyle(StageStyle.UNDECORATED); // Tắt thanh tiêu đề
            stage.setScene(new Scene(bookDetailsRoot));

            stage.initModality(Modality.APPLICATION_MODAL); // Đặt cửa sổ modal
            stage.setOnCloseRequest(event -> {
                setBookData(Book.getAvailableBooks());
            });

            stage.show(); // Hiển thị cửa sổ
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
        } else if ("Contributors".equals(SearchOptions.getValue())) {
            result = Book.searchBookByAuthor(Search.getText().trim());
        } else if ("Id".equals(SearchOptions.getValue())) {
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
