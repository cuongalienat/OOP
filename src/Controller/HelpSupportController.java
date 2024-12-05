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
     * Show User FAQ.
     */
    @FXML
    private void showUserFAQ() {
        userFAQContent.setVisible(true);
        adminFAQContent.setVisible(false);
    }

    /**
     * Show Admin FAQ.
     */
    @FXML
    private void showAdminFAQ() {
        userFAQContent.setVisible(false);
        adminFAQContent.setVisible(true);
    }
}
