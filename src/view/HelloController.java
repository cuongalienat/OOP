package view;

import java.io.IOException;
import java.util.*;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import library.Book;
import javafx.scene.input.MouseEvent;

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
            e.printStackTrace();  // Handle the exception here (e.g., logging or showing an error message)
        }
    }

    @FXML
    private void showBorrowedBooks() {
        //select style css
        borrowedBooks.getStyleClass().add("selected");
        home.getStyleClass().remove("selected");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("borrowedBooks.fxml"));
            Parent borrowedBooksRoot = fxmlLoader.load();

        //updating Borrowed Books
            choosedScene.getChildren().clear(); //clear Home
            choosedScene.getChildren().add(borrowedBooksRoot); //loading borrowedBooks.fxml
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
            e.printStackTrace();  // Handle the exception here (e.g., logging or showing an error message)
        }
    }
}
