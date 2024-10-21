package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainController extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root);
        stage.setMinWidth(1040);
        stage.setMinHeight(585);
        stage.setScene(scene);
        stage.setTitle("LIBRARY");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
