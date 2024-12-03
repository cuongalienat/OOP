package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class HelpSupportController {

    @FXML
    private AnchorPane userFAQContent;

    @FXML
    private AnchorPane adminFAQContent;

    @FXML
    private Button btnUserFAQ;

    @FXML
    private Button btnAdminFAQ;

    /**
     * Hiển thị User FAQ.
     */
    @FXML
    private void showUserFAQ() {
        // Chuyển đổi giữa các FAQ
        userFAQContent.setVisible(true);
        adminFAQContent.setVisible(false);
    }

    /**
     * Hiển thị Admin FAQ.
     */
    @FXML
    private void showAdminFAQ() {
        // Chuyển đổi giữa các FAQ
        userFAQContent.setVisible(false);
        adminFAQContent.setVisible(true);
    }
}
