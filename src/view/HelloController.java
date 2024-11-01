package view;

import java.io.IOException;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import library.Admin;
import library.Book;
import library.User;

import java.net.URL;

public class HelloController implements Initializable {
    @FXML
    private HBox home;

    @FXML
    private HBox borrowedBooks;

    @FXML
    private Label app_Name;

    @FXML
    private AnchorPane choosedScene;

    @FXML
    private HBox logout;

    @FXML
    private HBox reports;

    @FXML
    private HBox setting;

    @FXML
    private HBox userManagement;

    @FXML
    private HBox bookManagementBox;

    @FXML
    private HBox availableBooks;

    void setName(String user_Name) {
        app_Name.setText(user_Name);
    }

    private void resetMenuSelection() {
        home.getStyleClass().remove("selected");
        borrowedBooks.getStyleClass().remove("selected");
        availableBooks.getStyleClass().remove("selected");
        userManagement.getStyleClass().remove("selected");
        reports.getStyleClass().remove("selected");
        setting.getStyleClass().remove("selected");
        logout.getStyleClass().remove("selected");
        bookManagementBox.getStyleClass().remove("selected");
    }

    @FXML
    private void returnHome(MouseEvent event) {
        resetMenuSelection();
        home.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent homeRoot = fxmlLoader.load();

            // Initially set the Home view
            choosedScene.getChildren().clear();
            choosedScene.getChildren().add(homeRoot);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception here (e.g., logging or showing an error message)
        }
    }

    @FXML
    private void showBorrowedBooks() throws Exception {
        resetMenuSelection();
        borrowedBooks.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("borrowedBooks.fxml"));
            Parent borrowedBooksRoot = fxmlLoader.load();

            // updating Borrowed Books
            choosedScene.getChildren().clear(); // clear Home
            choosedScene.getChildren().add(borrowedBooksRoot); // loading borrowedBooks.fxml

            BorrowedBooksController BBC = fxmlLoader.getController();
            BBC.showBorrowedBooks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent homeRoot = fxmlLoader.load();

            // Initially set the Home view
            choosedScene.getChildren().clear();
            choosedScene.getChildren().add(homeRoot);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception here (e.g., logging or showing an error message)
        }
    }

    @FXML
    public void Log_Out(MouseEvent event) throws IOException {
        resetMenuSelection();
        logout.getStyleClass().add("selected");
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Bạn có muốn chắc chắn đăng xuất không ?");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setMinWidth(1040);
            stage.setMinHeight(585);
            stage.setScene(scene);
            stage.setTitle("LIBRARY");
            stage.show();
        } else {
            alert.close();
        }
    }

    @FXML
    public void bookManagement(MouseEvent event) {
        resetMenuSelection();
        bookManagementBox.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bookManagement.fxml"));
            Parent bookManagementRoot = fxmlLoader.load();

            // updating Borrowed Books
            choosedScene.getChildren().clear(); // clear Home
            choosedScene.getChildren().add(bookManagementRoot); // loading borrowedBooks.fxml

            // AvailableBookController ABC = fxmlLoader.getController();
            // ABC.setBookData(Book.getAvailableBooks());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAvailableBook(MouseEvent event) {
        resetMenuSelection();
        availableBooks.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("availableBook.fxml"));
            Parent availableBooksRoot = fxmlLoader.load();

            // updating Borrowed Books
            choosedScene.getChildren().clear(); // clear Home
            choosedScene.getChildren().add(availableBooksRoot); // loading borrowedBooks.fxml

            AvailableBookController ABC = fxmlLoader.getController();
            ABC.setBookData(Book.getAvailableBooks());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void userManagement(MouseEvent event) throws Exception {
        resetMenuSelection();
        userManagement.getStyleClass().add("selected");
        if (loginController.getUser_now() instanceof Admin) {
            Admin admin = (Admin) loginController.getUser_now();
            List<User> List_user = admin.showUserData();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserManagement.fxml"));
                Parent userManagement = fxmlLoader.load();
                choosedScene.getChildren().clear();
                choosedScene.getChildren().add(userManagement);

                UserManagementController Um = fxmlLoader.getController();
                Um.setUserData(List_user);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Bạn không phải admin ");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
