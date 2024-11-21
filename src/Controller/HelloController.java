package Controller;

import java.io.IOException;
import java.util.*;

import javafx.animation.ScaleTransition;
import javafx.scene.layout.Region;
import javafx.util.Duration;
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
import javafx.scene.layout.Pane;
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

    // @FXML
    // private Pane move;

    // private double xOffset = 0;

    // private double yOffset = 0;
    
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Parent homeRoot = fxmlLoader.load();

            // Initially set the Home view
            choosedScene.getChildren().clear();
            choosedScene.getChildren().add(homeRoot);
            AnchorPane.setTopAnchor(homeRoot, 0.0);
            AnchorPane.setBottomAnchor(homeRoot, 0.0);
            AnchorPane.setLeftAnchor(homeRoot, 0.0);
            AnchorPane.setRightAnchor(homeRoot, 0.0);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception here (e.g., logging or showing an error message)
        }
    }

    @FXML
    private void showBorrowedBooks() throws Exception {
        resetMenuSelection();
        borrowedBooks.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/borrowedBooks.fxml"));
            Parent borrowedBooksRoot = fxmlLoader.load();

            // updating Borrowed Books
            choosedScene.getChildren().clear(); // clear Home
            choosedScene.getChildren().add(borrowedBooksRoot); // loading borrowedBooks.fxml

            AnchorPane.setTopAnchor(borrowedBooksRoot, 0.0);
            AnchorPane.setBottomAnchor(borrowedBooksRoot, 0.0);
            AnchorPane.setLeftAnchor(borrowedBooksRoot, 0.0);
            AnchorPane.setRightAnchor(borrowedBooksRoot, 0.0);

            BorrowedBooksController BBC = fxmlLoader.getController();
            BBC.showBorrowedBooks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent homeRoot = fxmlLoader.load();

            // Initially set the Home view
            choosedScene.getChildren().clear();
            choosedScene.getChildren().add(homeRoot);
            AnchorPane.setTopAnchor(homeRoot, 0.0);
            AnchorPane.setBottomAnchor(homeRoot, 0.0);
            AnchorPane.setLeftAnchor(homeRoot, 0.0);
            AnchorPane.setRightAnchor(homeRoot, 0.0);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception here (e.g., logging or showing an error message)
        }

        // move.setOnMousePressed(event -> {
        //     xOffset = event.getSceneX();
        //     yOffset = event.getSceneY();
        // });

        // move.setOnMouseDragged(event -> {
        //     Stage stage = (Stage) move.getScene().getWindow();
        //     if (!stage.isMaximized()) { // Chỉ cho phép kéo khi không phóng to
        //         stage.setX(event.getScreenX() - xOffset);
        //         stage.setY(event.getScreenY() - yOffset);
        //     }
        // });
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
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setMinWidth(1040);
            stage.setMinHeight(585);
            stage.setScene(scene);
            stage.setTitle("LIBRARY");
            stage.show();
        }
    }

    @FXML
    public void bookManagement(MouseEvent event) {
        resetMenuSelection();
        bookManagementBox.getStyleClass().add("selected");
        if (loginController.getUser_now() instanceof Admin) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/bookManagement.fxml"));
                Parent bookManagementRoot = fxmlLoader.load();

                // updating Borrowed Books
                choosedScene.getChildren().clear(); // clear Home
                choosedScene.getChildren().add(bookManagementRoot); // loading borrowedBooks.fxml

                AnchorPane.setTopAnchor(bookManagementRoot, 0.0);
                AnchorPane.setBottomAnchor(bookManagementRoot, 0.0);
                AnchorPane.setLeftAnchor(bookManagementRoot, 0.0);
                AnchorPane.setRightAnchor(bookManagementRoot, 0.0);

                BookManagementController BMC = fxmlLoader.getController();
                BMC.setBookData(Book.getLibrary());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Only Admin account can access this !");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    public void showAvailableBook(MouseEvent event) {
        resetMenuSelection();
        availableBooks.getStyleClass().add("selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/availableBook.fxml"));
            Parent availableBooksRoot = fxmlLoader.load();

            // updating Borrowed Books
            choosedScene.getChildren().clear(); // clear Home
            choosedScene.getChildren().add(availableBooksRoot); // loading borrowedBooks.fxml
            AnchorPane.setTopAnchor(availableBooksRoot, 0.0);
            AnchorPane.setBottomAnchor(availableBooksRoot, 0.0);
            AnchorPane.setLeftAnchor(availableBooksRoot, 0.0);
            AnchorPane.setRightAnchor(availableBooksRoot, 0.0);
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
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UserManagement.fxml"));
                Parent userManagement = fxmlLoader.load();
                choosedScene.getChildren().clear();
                choosedScene.getChildren().add(userManagement);

                AnchorPane.setTopAnchor(userManagement, 0.0);
                AnchorPane.setBottomAnchor(userManagement, 0.0);
                AnchorPane.setLeftAnchor(userManagement, 0.0);
                AnchorPane.setRightAnchor(userManagement, 0.0);

                UserManagementController Um = fxmlLoader.getController();
                Um.setUserData(List_user);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Only Admin account can access this !");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // @FXML
    // private Label minimizeButton;

    // @FXML
    // private Label maximizeButton;

    // @FXML
    // private Label closeButton;

    // @FXML
    // private void handleMinimizeButtonAction(MouseEvent event) {
    //     Stage stage = (Stage) minimizeButton.getScene().getWindow();
    //     stage.setIconified(true); // Ẩn cửa sổ
    // }

    // @FXML
    // private void handleMaximizeButtonAction(MouseEvent event) {
    //     Stage stage = (Stage) maximizeButton.getScene().getWindow();
    //     boolean isMaximized = !stage.isMaximized();
    //     stage.setMaximized(isMaximized);

    //     // Thay đổi biểu tượng nút phóng to
    //     maximizeButton.setText(isMaximized ? "❐" : "⛶");
    // }

    // @FXML
    // private void handleCloseButtonAction(MouseEvent event) {
    //     Stage stage = (Stage) closeButton.getScene().getWindow();
    //     stage.close(); // Đóng cửa sổ
    // }
}
