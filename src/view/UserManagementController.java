package view;

import java.sql.SQLException;
import java.time.LocalDate;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import library.Admin;
import library.BorrowedBooks;
import library.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManagementController {
    @FXML
    private TableColumn<BorrowedBooks, LocalDate> BookLogs_BDate;

    @FXML
    private TableColumn<BorrowedBooks, LocalDate> BookLogs_DDate;

    @FXML
    private TableColumn<BorrowedBooks, Integer> BookLogs_ID;

    @FXML
    private TableColumn<BorrowedBooks, String> BookLogs_PU;

    @FXML
    private TableView<BorrowedBooks> Table_BookLogs;

    @FXML
    private TableView<User> Table_Um;

    @FXML
    private TableColumn<User, Integer> Um_BorrowedBook;

    @FXML
    private TableColumn<User, String> Um_Email;

    @FXML
    private TableColumn<User, String> Um_Name;

    @FXML
    private TableColumn<User, Integer> Um_OverdueDate;

    @FXML
    private TableColumn<User, String> Um_Phone;

    @FXML
    private Button DeleteUser;

    @FXML
    private Button UserManagement;

    @FXML
    private Button BookLogs;

    @FXML
    private TableColumn<BorrowedBooks, String> Booklogs_Status;

    @FXML
    private Button Update;

    @FXML
    private Button Return;
    @FXML
    private TextField Booklogs_Search;
    @FXML
    private ImageView Search;
    @FXML
    private Button Cancel;
    @FXML
    private TextField Um_search;
    @FXML
    private ComboBox<String> SearchOptions;

    private ObservableList<BorrowedBooks> selectedLogs = FXCollections.observableArrayList();
    private ObservableList<BorrowedBooks> allBookLogs = FXCollections.observableArrayList();
    private List<User> allUsers;

    public void setUserData(List<User> userData) {
        ObservableList<User> users = FXCollections.observableArrayList(userData);

        Um_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Um_Phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        Um_Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        Um_BorrowedBook.setCellValueFactory(new PropertyValueFactory<>("quantityBorrowedBook"));
        Um_OverdueDate.setCellValueFactory(new PropertyValueFactory<>("quantityOverduedateBook"));

        Table_Um.setItems(users);
        allUsers = userData;
    }

    public void setBookLogsData(ObservableList<BorrowedBooks> Booklogs) {
        Table_BookLogs.setEditable(true);
        Booklogs_Status.setEditable(true);
        BookLogs_ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        BookLogs_PU.setCellValueFactory(new PropertyValueFactory<>("phone_user"));
        BookLogs_BDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        BookLogs_DDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        Booklogs_Status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        ObservableList<String> statusOptions = FXCollections.observableArrayList("Pending", "Active", "Overdue",
                "Returned", "Lost");
        Booklogs_Status.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        Booklogs_Status.setOnEditCommit(cellEditEvent -> {
            BorrowedBooks log = cellEditEvent.getRowValue();
            String newStatus = cellEditEvent.getNewValue();
            log.setStatus(newStatus);
            selectedLogs.add(log);
        });
        ObservableList<String> searchOptions = FXCollections.observableArrayList("Id", "Phone", "Status");
        SearchOptions.setItems(searchOptions);
        Table_BookLogs.setItems(Booklogs);
    }

    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == BookLogs) {

            slider.setOnFinished((ActionEvent e) -> {
                Table_BookLogs.setVisible(true);
                Table_Um.setVisible(false);
                UserManagement.setVisible(true);
                BookLogs.setVisible(false);
                DeleteUser.setVisible(false);
                Update.setVisible(true);
                Return.setVisible(true);
                SearchOptions.setVisible(true);
                Booklogs_Search.setVisible(true);
                Um_search.setVisible(false);
                try {
                    allBookLogs = BorrowedBooks.getBookLogs();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                setBookLogsData(allBookLogs);
            });
            slider.play();
        } else if (event.getSource() == UserManagement) {

            slider.setOnFinished((ActionEvent e) -> {
                Table_BookLogs.setVisible(false);
                Table_Um.setVisible(true);
                UserManagement.setVisible(false);
                BookLogs.setVisible(true);
                DeleteUser.setVisible(true);
                Update.setVisible(false);
                Return.setVisible(false);
                SearchOptions.setVisible(false);
                Booklogs_Search.setVisible(false);
                Um_search.setVisible(true);
            });

            slider.play();
        }
    }

    public void DeleteUser(ActionEvent event) throws Exception {
        User selectedUser = Table_Um.getSelectionModel().getSelectedItem();
        String phone_user = selectedUser.getPhone();
        if (event.getSource() == DeleteUser) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Are you sure to delete this user ?");
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedUser.DeleteUser(phone_user);
                ObservableList<User> users = Table_Um.getItems();
                users.remove(selectedUser);
                Table_Um.refresh();
            } else {
                alert.close();
            }
        }
    }

    @FXML
    void ReturnBook(ActionEvent event) throws Exception {
        BorrowedBooks selectedBook = Table_BookLogs.getSelectionModel().getSelectedItem();
        int book_id = selectedBook.getId();
        String phone_user = selectedBook.getPhone_user();
        if (event.getSource() == Return) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Are you sure that book was returned ?");
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedBook.returnBook(phone_user, book_id);
                ObservableList<BorrowedBooks> borrowedBooks = Table_BookLogs.getItems();
                borrowedBooks.remove(selectedBook);
                Table_Um.refresh();
            } else {
                alert.close();
            }
        }
    }

    @FXML
    void Cancel(MouseEvent event) {
        setUserData(allUsers);
        setBookLogsData(allBookLogs);
    }

    @FXML
    void Search(MouseEvent event) throws Exception {
        if (Um_search.isVisible()) {
            if (Um_search.getText() != null && !Um_search.getText().isEmpty()) {
                String phone = Um_search.getText();
                User user = User.getUser(phone);
                Admin admin = Admin.getUser(phone);
                List<User> users = new ArrayList<>();
                if (user != null)
                    users.add(user);
                if (admin != null)
                    users.add(admin);
                for (User u : users) {
                    u.countQuantity();
                }
                setUserData(users);
            } else {
                Alert alert = new Alert(AlertType.WARNING, "Please enter a phone number to search.");
                alert.showAndWait();
            }
            Um_search.clear();
        } else {
            if (SearchOptions.getValue() != null) {
                ObservableList<BorrowedBooks> searched = FXCollections.observableArrayList();
                try {
                    switch (SearchOptions.getValue()) {
                        case "Id":
                            searched = BorrowedBooks.searchIdBookLogs(Integer.parseInt(Booklogs_Search.getText()));
                            break;
                        case "Phone":
                            searched = BorrowedBooks.searchPhoneBookLogs(Booklogs_Search.getText());
                            break;
                        case "Status":
                            searched = BorrowedBooks.searchStatusBookLogs(Booklogs_Search.getText());
                            break;
                        default:
                            Alert alert = new Alert(AlertType.WARNING, "Please select a valid search option.");
                            alert.showAndWait();
                            return;
                    }
                    setBookLogsData(searched);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(AlertType.ERROR, "Please enter a valid numeric ID.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.WARNING, "Please select a search option.");
                alert.showAndWait();
            }
            Booklogs_Search.clear();
        }
    }

    @FXML
    void UpdateBookLogs(ActionEvent event) throws Exception {
        if (event.getSource() == Update) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Are you sure to update these change ?");
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
            for (BorrowedBooks log : selectedLogs) {
                String newStatus = log.getStatus();
                try {
                    log.updateStatus(log.getId(), log.getPhone_user(), newStatus);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            selectedLogs.clear();
        }
    }
}
