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

/**
 * Controller for the main Hello view, managing navigation and user
 * interactions.
 */
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

    /**
     * Sets the displayed user name.
     *
     * @param user_Name The name of the user to display.
     */
    void setName(String user_Name) {
        app_Name.setText(user_Name);
    }

    /**
     * Resets the menu selection styles.
     */
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

    /**
     * Handles the action of returning to the home view.
     *
     * @param event The mouse event triggering the action.
     */
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

    /**
     * Displays the list of borrowed books.
     *
     * @throws Exception If an error occurs while loading the borrowed books view.
     */
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

    /**
     * Initializes the HelloController.
     *
     * @param url            The location used to resolve relative paths for the
     *                       root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
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
    }

    /**
     * Logs out the current user.
     *
     * @param event The mouse event triggering the logout.
     * @throws IOException If an error occurs during the logout process.
     */
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
            // Consume the event
            event.consume();
        }
    }

    /**
     * Handles book management actions.
     *
     * @param event The mouse event triggering the action.
     */
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

    /**
     * Displays available books.
     *
     * @param event The mouse event triggering the display.
     */
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

    /**
     * Manages user-related actions.
     *
     * @param event The mouse event triggering the action.
     * @throws Exception If an error occurs during user management.
     */
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

    
}
