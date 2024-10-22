package view;

import java.io.IOException;
import java.util.*;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    void setName(String user_Name) {
        app_Name.setText(user_Name);
    }

    @FXML
    private void returnHome(MouseEvent event) {
        borrowedBooks.getStyleClass().remove("selected");
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
    private void showBorrowedBooks() {
        // select style css
        borrowedBooks.getStyleClass().add("selected");
        home.getStyleClass().remove("selected");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("borrowedBooks.fxml"));
            Parent borrowedBooksRoot = fxmlLoader.load();

            //updating Borrowed Books
            choosedScene.getChildren().clear(); //clear Home
            choosedScene.getChildren().add(borrowedBooksRoot); //loading borrowedBooks.fxml

            BorrowedBooksController BBC = fxmlLoader.getController(); 
            BBC.showBorrowedBooks(BookDetailsController.borrowList);
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
    public void Log_Out(MouseEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Bạn có muốn chắc chắn đăng xuất không ?");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } else {
            alert.close();
        }
    }

    @FXML
    public void showAvailableBook(MouseEvent event) {
                // select style css
                borrowedBooks.getStyleClass().add("selected");
                home.getStyleClass().remove("selected");
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("availableBook.fxml"));
                    Parent availableBooksRoot = fxmlLoader.load();
        
                    //updating Borrowed Books
                    choosedScene.getChildren().clear(); //clear Home
                    choosedScene.getChildren().add(availableBooksRoot); //loading borrowedBooks.fxml
        
                    AvailableBookController ABC = fxmlLoader.getController(); 
                    ABC.setBookData(Book.getAvailableBooks());
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    @FXML
    public void userManagement(MouseEvent event) throws Exception {
        if (loginController.getUser_now() instanceof Admin) {
            Admin admin = (Admin) loginController.getUser_now();
            Map<String, User> map_user = admin.showUserData();
            borrowedBooks.getStyleClass().add("selected");
            home.getStyleClass().remove("selected");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserManagement.fxml"));
                Parent userManagement = fxmlLoader.load();
                choosedScene.getChildren().clear();
                choosedScene.getChildren().add(userManagement);

                UserManagementController Um = fxmlLoader.getController();
                Um.setUserData(map_user);

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
