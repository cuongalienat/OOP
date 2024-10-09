package view;

import java.io.BufferedReader;

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
import library.User;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class loginController {

    @FXML
    private AnchorPane login;

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
    private TextField signup_name;

    @FXML
    private TextField signup_age;

    @FXML
    private PasswordField signup_password;

    @FXML
    private TextField signup_phone;

    @FXML
    private Button signup_signup;

    @FXML
    private Button signup_toLogin;

    private Alert alert;
    private Map<String, User> Map_user = new HashMap<>();

    public void switchForm(ActionEvent event) {

        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == login_toSignup) {

            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(true);
                login_toSignup.setVisible(false);
                signup.setVisible(true);
                login.setVisible(false);
            });
            slider.play();
        } else if (event.getSource() == signup_toLogin) {

            slider.setOnFinished((ActionEvent e) -> {
                signup_toLogin.setVisible(false);
                login_toSignup.setVisible(true);
                signup.setVisible(false);
                login.setVisible(true);
            });

            slider.play();
        }
    }

    public boolean checkPhone(String phone) {
        if (phone.length() != 10)
            return false;
        if (phone.charAt(0) != '0' && phone.charAt(1) != '9')
            return false;
        for (int i = 2; i < 10; i++) {
            if (phone.charAt(i) < '0' || phone.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public boolean checkPassword(String password) {
        if (password.length() < 6)
            return false;
        return true;
    }

    public boolean checkAge(int age) {
        if (age < 1)
            return false;
        return true;
    }

    public void signUp(ActionEvent event) throws IOException {
        User user = new User();
        user.setPhone(signup_phone.getText());
        user.setPassword(signup_password.getText());
        user.setName(signup_name.getText());

        if (event.getSource() == signup_signup) {

            if (signup_phone.getText().equals("") || signup_password.getText().equals("")
                    || signup_name.getText().equals("") || signup_age.getText().equals("")) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Vui lòng nhập hết thông tin ở cac ô");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            user.setAge(Integer.parseInt(signup_age.getText()));

            if (!checkPhone(user.getPhone())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Số điện thoại phải có định dạng 09 và có đúng 10 số");
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

            if (!checkAge(user.getAge())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Tuổi không hợp lệ");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_age.clear();
                return;
            }

            if (Map_user.containsKey(user.getPhone())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mỗi số điện thoại chỉ đăng kí được 1 tài khoản");
                alert.setHeaderText(null);
                alert.showAndWait();
                signup_phone.clear();
                return;
            }

            try (FileWriter writer = new FileWriter("src/data/User.txt", true)) {
                writer.write(user.getPhone() + " ");
                writer.write(user.getPassword() + " ");
                writer.write(user.getName() + " ");
                writer.write(user.getAge() + " ");
                writer.write("\n");
            }

            alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Đăng kí thành công");
            alert.setHeaderText(null);
            alert.showAndWait();

            signup_phone.clear();
            signup_password.clear();
            signup_name.clear();
            signup_age.clear();
        }
    }

    public void logIn(ActionEvent event) throws IOException {
        try {
            BufferedReader ReadFile = new BufferedReader(new FileReader("src/data/User.txt"));
            String line;
            while ((line = ReadFile.readLine()) != null) {
                String words[] = line.split("\\s+");
                User user = new User(words[2], Integer.parseInt(words[3]), words[0], words[1]);
                Map_user.put(user.getPhone(), user);
            }
            ReadFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String phone = login_phone.getText();
        String password = login_password.getText();
        if (event.getSource() == login_login) {
            if (!Map_user.containsKey(phone)) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Tài khoản không tồn tại");
                alert.setHeaderText(null);
                alert.showAndWait();
                login_phone.clear();
                login_password.clear();
                return;
            }

            if (!password.equals(Map_user.get(phone).getPassword())) {
                alert = new Alert(AlertType.ERROR);
                alert.setContentText("Mật khẩu sai");
                alert.setHeaderText(null);
                alert.showAndWait();
                login_password.clear();
                return;
            }
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent newRoot = loader.load();

            HelloController helloController = loader.getController();
            helloController.setName(Map_user.get(phone).getName());

            Stage newStage = new Stage();
            Scene scene = new Scene(newRoot);

            newStage.setScene(scene);
            newStage.setTitle("LIBRARY");
            newStage.show();

        }
    }

}
