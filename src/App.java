import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.GoogleBooksAPI;

public class App extends Application {

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

    @Override
    public void stop() {
        System.out.println("Ứng dụng đang dừng.");
        GoogleBooksAPI.shutdownExecutor();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
