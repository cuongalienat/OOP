package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import library.User;

import java.io.File;

import org.mindrot.jbcrypt.BCrypt;

public class UserSettingsController {

    @FXML
    private TextField userNameField;  
    
    @FXML
    private TextField userPhoneField;  
    
    @FXML
    private TextField userEmailField; 
    
    @FXML
    private PasswordField userCurrentPasswordField; 
    
    @FXML
    private PasswordField userNewPasswordField; 

    @FXML
    private PasswordField userConfirmPasswordField; 

    @FXML
    private AnchorPane editUserInfoContent;

    @FXML
    private AnchorPane changeBackgroundContent;

    @FXML
    private ImageView imageView; 

    @FXML
    private Button btnEditUserInfo, btnChangeBackground;

    /**
     * Initializes the controller by setting user data.
     */
    public void initialize() {
        User currentUser = loginController.getUser_now(); 
    
        if (currentUser != null) {
            userNameField.setText(currentUser.getName());
            userPhoneField.setText(currentUser.getPhone());  
            userEmailField.setText(currentUser.getEmail()); 
    
            String profilePicture = currentUser.getProfilePicture();
            if (profilePicture != null && !profilePicture.isEmpty()) {
                Image image = new Image(profilePicture);
                imageView.setImage(image);
            }
        }
    }

    /**
     * Handles the click event for changing the profile picture.
     * Allows the user to upload a new image.
     * 
     * @throws Exception if file upload fails.
     */
    @FXML
    private void handleImageClick() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            String filePath = file.toURI().toString();
            Image image = new Image(filePath);

            imageView.setImage(image);

            User currentUser = loginController.getUser_now();
            if (currentUser != null) {
                currentUser.setProfilePicture(filePath);  
                currentUser.Update();
            }

            showAlert(AlertType.INFORMATION, "Thông báo", "Ảnh đại diện đã được cập nhật.");
        }
    }

    /**
     * Updates the user's name if the new name is valid.
     * 
     * @throws Exception if the update fails.
     */
    @FXML
    private void updateUserName() throws Exception {
        String name = userNameField.getText();

        User currentUser = loginController.getUser_now();
        if (currentUser != null && !name.trim().isEmpty() && !name.equals(currentUser.getName())) {
            currentUser.setName(name);
            currentUser.Update();  
            loginController.user_now = currentUser;
            showAlert(AlertType.INFORMATION, "Thông báo", "Tên người dùng đã được cập nhật.");
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Tên người dùng không hợp lệ hoặc không có thay đổi.");
        }
    }

    /**
     * Updates the user's password after validating the current password and new password.
     * 
     * @throws Exception if password update fails.
     */
    @FXML
    private void updatePassword() throws Exception {
        String currentPassword = userCurrentPasswordField.getText();
        if(currentPassword!=null) {

        }
        String newPassword = userNewPasswordField.getText();
        String confirmPassword = userConfirmPasswordField.getText();

        User currentUser = loginController.getUser_now();

        if (currentUser == null || !BCrypt.checkpw(currentPassword, currentUser.getPassword())) {
            showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu hiện tại không đúng.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu xác nhận không khớp.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        currentUser.setPassword(hashedPassword);
        currentUser.Update(); 
        currentUser.UpdateAdmin();
        loginController.user_now = currentUser;

        showAlert(AlertType.INFORMATION, "Thông báo", "Mật khẩu đã được cập nhật.");
    }

    /**
     * Updates the user information (name and password).
     * 
     * @throws Exception if any update operation fails.
     */
    @FXML
    private void updateUserInfo() throws Exception {
        if(userNameField.getText()!="") {
            updateUserName();  
        }
        if(userCurrentPasswordField.getText()!=""){
            updatePassword(); 
        }
    }

    /**
     * Displays an alert with a specified type, header, and content.
     * 
     * @param type the alert type.
     * @param header the alert header text.
     * @param content the alert content text.
     */
    private void showAlert(AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the event to show the user info edit section.
     */
    @FXML
    private void handleEditUserInfo() {
        btnEditUserInfo.getStyleClass().add("selected");
        btnChangeBackground.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(true);
        changeBackgroundContent.setVisible(false);
    }

    /**
     * Handles the event to show the background change section.
     */
    @FXML
    private void handleChangeBackground() {
        btnChangeBackground.getStyleClass().add("selected");
        btnEditUserInfo.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(false);
        changeBackgroundContent.setVisible(true);
    }

    /**
     * Handles the event to hide both sections and show the default view.
     */
    @FXML
    private void handleHelpAndSupport() {
        btnEditUserInfo.getStyleClass().remove("selected");
        btnChangeBackground.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(false);
        changeBackgroundContent.setVisible(false);
    }

    /**
     * Handles the selection of buttons and highlights the clicked button.
     * 
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void handleButtonClick(ActionEvent event) {
        btnEditUserInfo.getStyleClass().remove("selected");
        btnChangeBackground.getStyleClass().remove("selected");

        Button clickedButton = (Button) event.getSource();
        clickedButton.getStyleClass().add("selected");
    }   
}
