package view;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import library.User;
import java.util.Map;
import java.util.Optional;

public class UserManagementController {
    @FXML
    private TableColumn<?, ?> BookLogs_BDate;

    @FXML
    private TableColumn<?, ?> BookLogs_DDate;

    @FXML
    private TableColumn<?, ?> BookLogs_ID;

    @FXML
    private TableColumn<?, ?> BookLogs_PU;

    @FXML
    private TableView<?> Table_BookLogs;

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

    public void setUserData(Map<String, User> userData) {
        ObservableList<User> users = FXCollections.observableArrayList(userData.values());

        Um_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Um_Phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        Um_Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        Um_BorrowedBook.setCellValueFactory(new PropertyValueFactory<>("quantityBorrowedBook"));
        Um_OverdueDate.setCellValueFactory(new PropertyValueFactory<>("quantityOverduedateBook"));

        Table_Um.setItems(users);
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
            });
            slider.play();
        } else if (event.getSource() == UserManagement) {

            slider.setOnFinished((ActionEvent e) -> {
                Table_BookLogs.setVisible(false);
                Table_Um.setVisible(true);
                UserManagement.setVisible(false);
                BookLogs.setVisible(true);
                DeleteUser.setVisible(true);
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
            } else {
                alert.close();
            }
        }
    }
}
