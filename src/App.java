import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.GoogleBooksAPI;

public class App extends Application {
    
    /**
     * Initializes and starts the JavaFX application.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     * @throws Exception If the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(root);
        stage.setMinWidth(1040);
        stage.setMinHeight(585);
        stage.setScene(scene);
        stage.setTitle("LIBRARY");
        stage.show();

        // Đảm bảo ứng dụng thoát khi cửa sổ đóng
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });
    }

    /**
     * Executes tasks needed before the application exits.
     * Specifically, it shuts down the GoogleBooksAPI executor to release resources.
     */
    @Override
    public void stop() {
        System.out.println("Ứng dụng đang dừng.");
        GoogleBooksAPI.shutdownExecutor();
    }

    /**
     * The main method that serves as the entry point for the application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
