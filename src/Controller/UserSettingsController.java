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
    private TextField userNameField;  // Name field for user to update
    
    @FXML
    private TextField userPhoneField;  // Phone field (read-only)
    
    @FXML
    private TextField userEmailField;  // Email field (read-only)
    
    @FXML
    private PasswordField userCurrentPasswordField;  // Mật khẩu cũ
    
    @FXML
    private PasswordField userNewPasswordField;  // Mật khẩu mới

    @FXML
    private PasswordField userConfirmPasswordField;  // Xác nhận mật khẩu mới

    @FXML
    private AnchorPane editUserInfoContent;

    @FXML
    private AnchorPane changeBackgroundContent;

    @FXML
    private ImageView imageView; // ImageView hiển thị ảnh người dùng

    // Hàm khởi tạo, dùng để hiển thị thông tin người dùng hiện tại
    
    public void initialize() {
        User currentUser = loginController.getUser_now();  // Lấy người dùng hiện tại
    
        if (currentUser != null) {
            userNameField.setText(currentUser.getName());
            userPhoneField.setText(currentUser.getPhone());  // Hiển thị phone (chỉ đọc)
            userEmailField.setText(currentUser.getEmail());  // Hiển thị email (chỉ đọc)
    
            // Kiểm tra xem người dùng có ảnh đại diện hay không
            String profilePicture = currentUser.getProfilePicture();
            if (profilePicture != null && !profilePicture.isEmpty()) {
                // Tạo đối tượng Image từ đường dẫn và hiển thị vào ImageView
                Image image = new Image(profilePicture);
                imageView.setImage(image);
            // } else {
            //     // Nếu không có ảnh đại diện, có thể hiển thị một ảnh mặc định
            //     Image defaultImage = new Image("/Images/default_avatar.png");
            //     imageView.setImage(defaultImage);
            }
        }
    }

    @FXML
    private void handleImageClick() throws Exception {
        // Tạo một cửa sổ chọn tệp
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Mở cửa sổ chọn tệp và lấy tệp người dùng chọn
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Tạo đối tượng Image từ tệp đã chọn
            String filePath = file.toURI().toString();
            Image image = new Image(filePath);

            // Cập nhật hình ảnh vào ImageView
            imageView.setImage(image);

            // Cập nhật lại đường dẫn ảnh cho người dùng
            User currentUser = loginController.getUser_now();
            if (currentUser != null) {
                currentUser.setProfilePicture(filePath);  // Lưu đường dẫn ảnh mới vào người dùng
                currentUser.Update();  // Cập nhật vào cơ sở dữ liệu nếu cần
            }

            showAlert(AlertType.INFORMATION, "Thông báo", "Ảnh đại diện đã được cập nhật.");
        }
    }

    @FXML
    private void updateUserName() throws Exception {
        String name = userNameField.getText();

        User currentUser = loginController.getUser_now();
        if (currentUser != null && !name.trim().isEmpty() && !name.equals(currentUser.getName())) {
            currentUser.setName(name);
            currentUser.Update();  // Cập nhật vào cơ sở dữ liệu
            loginController.user_now = currentUser;

            showAlert(AlertType.INFORMATION, "Thông báo", "Tên người dùng đã được cập nhật.");
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Tên người dùng không hợp lệ hoặc không có thay đổi.");
        }
    }


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

        // Cập nhật mật khẩu mới
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        currentUser.setPassword(hashedPassword);
        currentUser.Update();  // Cập nhật vào cơ sở dữ liệu
        currentUser.UpdateAdmin();
        loginController.user_now = currentUser;

        showAlert(AlertType.INFORMATION, "Thông báo", "Mật khẩu đã được cập nhật.");
    }

    @FXML
    private void updateUserInfo() throws Exception {
        if(userNameField.getText()!="") {
            updateUserName();  // Thay đổi tên người dùng
        }
        if(userCurrentPasswordField.getText()!=""){
            updatePassword();  // Thay đổi mật khẩu
        }
    }


    // Hàm hỗ trợ hiển thị các thông báo dạng Alert
    private void showAlert(AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Hàm xóa các trường nhập liệu mật khẩu
    // private void clearPasswordFields() {
    //     userCurrentPasswordField.clear();
    //     userNewPasswordField.clear();
    //     userConfirmPasswordField.clear();
    // }

    // Các phương thức xử lý sự kiện cho các tab khác nhau
    @FXML
    private void handleEditUserInfo() {
        btnEditUserInfo.getStyleClass().add("selected");
        btnChangeBackground.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(true);
        changeBackgroundContent.setVisible(false);
    }

    @FXML
    private void handleChangeBackground() {
        btnChangeBackground.getStyleClass().add("selected");
        btnEditUserInfo.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(false);
        changeBackgroundContent.setVisible(true);
    }

    @FXML
    private void handleHelpAndSupport() {
        btnEditUserInfo.getStyleClass().remove("selected");
        btnChangeBackground.getStyleClass().remove("selected");
        editUserInfoContent.setVisible(false);
        changeBackgroundContent.setVisible(false);
    }

    @FXML
    private Button btnEditUserInfo, btnChangeBackground;

    @FXML
    private void handleButtonClick(ActionEvent event) {
        // Loại bỏ class selected khỏi tất cả các nút
        btnEditUserInfo.getStyleClass().remove("selected");
        btnChangeBackground.getStyleClass().remove("selected");

        // Thêm class selected vào nút được click
        Button clickedButton = (Button) event.getSource();
        clickedButton.getStyleClass().add("selected");
    }   
}
