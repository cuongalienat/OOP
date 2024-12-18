package Controller;

import org.mindrot.jbcrypt.BCrypt;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.Admin;
import library.User;

/**
 * Controller for handling user login operations.
 */
public class loginController {

    @FXML
    private Button Change;

    @FXML
    private PasswordField Confirm_pw;

    @FXML
    private PasswordField New_pw;

    @FXML
    private TextField forget_email;

    @FXML
    private TextField forget_phone;

    @FXML
    private AnchorPane login;

    @FXML
    private AnchorPane login_Forgot;

    @FXML
    private AnchorPane login_SignUp;

    @FXML
    private Hyperlink login_forgot;

    @FXML
    private Button login_login;

    @FXML
    private PasswordField login_password;

    @FXML
    private TextField login_phone;

    @FXML
    private Button login_toSignup;

    @FXML
    private AnchorPane signup;

    @FXML
    private TextField signup_email;

    @FXML
    private TextField signup_name;

    @FXML
    private PasswordField signup_password;

    @FXML
    private TextField signup_phone;

    @FXML
    private Button signup_signup;

    @FXML
    private Button signup_toLogin;

    private Alert alert;

    protected static User user_now;

    public static User getUser_now() {
        return user_now;
    }

    /**
     * Switches between login and signup forms.
     *
     * @param event The action event triggered by the user.
     */
    public void switchForm(ActionEvent event) {

        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == login_toSignup) {

            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(true);
                login_toSignup.setVisible(false);
                signup.setVisible(true);
                login.setVisible(false);
                login_Forgot.setVisible(false);
            });
            slider.play();
        } else if (event.getSource() == signup_toLogin) {

            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(false);
                login_toSignup.setVisible(true);
                signup.setVisible(false);
                login.setVisible(true);
                login_Forgot.setVisible(false);
            });

            slider.play();
        }
    }

    /**
     * Checks if the phone number is valid.
     *
     * @param phone The phone number to check.
     * @return True if the phone number is valid, false otherwise.
     */
    public boolean checkPhone(String phone) {
        if (phone.length() != 10)
            return false;
        if (phone.charAt(0) != '0')
            return false;
        for (int i = 1; i < 10; i++) {
            if (phone.charAt(i) < '0' || phone.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the password is valid.
     *
     * @param password The password to check.
     * @return True if the password is valid, false otherwise.
     */
    public boolean checkPassword(String password) {
        if (password.length() < 6)
            return false;
        return true;
    }

    /**
     * Checks if the email is valid.
     *
     * @param email The email to check.
     * @return True if the email is valid, false otherwise.
     */
    public boolean checkEmail(String email) {
        return email.endsWith("@gmail.com");
    }

    /**
     * Handles the user signup process.
     *
     * @param event The action event triggered by the user.
     * @throws Exception If an error occurs during the signup process.
     */
    public void signUp(ActionEvent event) throws Exception {
        User user = new User();
        user.setPhone(signup_phone.getText());
        user.setPassword(signup_password.getText());
        user.setName(signup_name.getText());
        user.setEmail(signup_email.getText());

        if (event.getSource() == signup_signup) {

            if (signup_phone.getText().equals("") || signup_password.getText().equals("")
                    || signup_name.getText().equals("") || signup_email.getText().equals("")) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Vui lòng nhập hết thông tin ở các ô");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            if (!checkPhone(user.getPhone())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Số điện thoại phải có định dạng 0 và có đúng 10 số");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_phone.clear();
                return;
            }

            if (!checkPassword(user.getPassword())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mật khẩu phải có tối thiểu 6 kí tự");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_password.clear();
                return;
            }

            if (!checkEmail(user.getEmail())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Email không hợp lệ");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_email.clear();
                return;
            }

            if (User.getUser(user.getPhone()) != null) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mỗi số điện thoại chỉ đăng kí được 1 tài khoản");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_phone.clear();
                return;
            }

            user.addData();

            alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Successfull register !");
            alert.setHeaderText(null);
            alert.showAndWait();

            signup_phone.clear();
            signup_password.clear();
            signup_name.clear();
            signup_email.clear();

            TranslateTransition slider = new TranslateTransition();
            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(false);
                login_toSignup.setVisible(true);
                signup.setVisible(false);
                login.setVisible(true);
            });

            slider.play();
        }
    }

    /**
     * Handles the user login process.
     *
     * @param event The action event triggered by the user.
     * @throws Exception If an error occurs during the login process.
     */
    public void logIn(ActionEvent event) throws Exception {
        String phone = login_phone.getText();
        String password = login_password.getText();

        if (event.getSource() == login_login) {
            if (User.getUser(phone) == null && Admin.getUser(phone) == null) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Tài khoản không tồn tại");
                alert.setHeaderText(null);
                alert.showAndWait();
                login_phone.clear();
                login_password.clear();
                return;
            }

            if (User.getUser(phone) == null) {
                user_now = Admin.getUser(phone);
            } else {
                user_now = User.getUser(phone);
            }

            if (!BCrypt.checkpw(password, user_now.getPassword())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mật khẩu sai");
                alert.setHeaderText(null);
                alert.showAndWait();
                login_password.clear();
                return;
            }

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/sample.fxml"));
            Parent newRoot = loader.load();

            HelloController helloController = loader.getController();
            helloController.setName(user_now.getName());
            helloController.setImage(user_now.getProfilePicture());

            Stage newStage = new Stage();
            Scene scene = new Scene(newRoot);

            newStage.setScene(scene);
            newStage.setTitle("LIBRARY");
            newStage.setMinWidth(1040);
            newStage.setMinHeight(585);
            newStage.show();
        }
    }

    /**
     * Handles the forget password process.
     *
     * @param event The action event triggered by the user.
     * @throws Exception If an error occurs during the forget password process.
     */
    public void forgetPassword(ActionEvent event) throws Exception {
        if (event.getSource() == login_forgot) {
            TranslateTransition slider = new TranslateTransition();
            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(true);
                login_toSignup.setVisible(false);
                login_Forgot.setVisible(true);
                login.setVisible(false);
                signup.setVisible(false);
            });
            slider.play();
        }

        String fPhone = forget_phone.getText();
        String fEmail = forget_email.getText();
        String nPassword = New_pw.getText();
        String cfPassword = Confirm_pw.getText();

        if (event.getSource() == Change) {
            if (fPhone.equals("") || fEmail.equals("")
                    || nPassword.equals("") || cfPassword.equals("")) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Vui lòng nhập hết thông tin ở cac ô");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            User user = User.getUser(fPhone);
            if (user == null) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Không tồn tại tài khoản này !");
                alert.setHeaderText(null);
                alert.showAndWait();
                login_password.clear();
                return;
            }
            if (!user.getEmail().equals(fEmail)) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Sai email !");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            if (!checkPassword(nPassword)) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mật khẩu phải có tối thiểu 6 kí tự");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_password.clear();
                return;
            }
            if (!cfPassword.equals(nPassword)) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Xác nhận mật khẩu sai !");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            String hashedPassword = BCrypt.hashpw(cfPassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.Update();
            signup_toLogin.setVisible(false);
            login_toSignup.setVisible(true);
            login_Forgot.setVisible(false);
            login.setVisible(true);
        }
    }
}
