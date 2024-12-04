package library;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Represents a user in the library system.
 */
public class User {
    Scanner sc = new Scanner(System.in);

    protected String role; // New attribute for role
    protected String name;
    protected String password;
    protected String phone;
    protected String email;
    protected int quantityBorrowedBook;
    protected int quantityOverduedateBook;
    protected String profilePicture; // Thêm trường để lưu link ảnh đại diện

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Constructs a User with specified details.
     *
     * @param _name     the user's name
     * @param _email    the user's email
     * @param _phone    the user's phone number
     * @param _password the user's password
     */
    public User(String _name, String _email, String _phone, String _password) {
        email = _email;
        password = _password;
        name = _name;
        phone = _phone;
        quantityBorrowedBook = 0;
        quantityOverduedateBook = 0;
    }

    /**
     * Gets the user's password.
     *
     * @return the password
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the user's email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và setter cho profilePicture
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Gets the quantity of borrowed books.
     *
     * @return the quantity of borrowed books
     */
    public int getQuantityBorrowedBook() {
        return quantityBorrowedBook;
    }

    /**
     * Sets the quantity of borrowed books.
     *
     * @param quantityBorrowedBook the new quantity of borrowed books
     */
    public void setQuantityBorrowedBook(int quantityBorrowedBook) {
        this.quantityBorrowedBook = quantityBorrowedBook;
    }

    /**
     * Gets the quantity of overdue books.
     *
     * @return the quantity of overdue books
     */
    public int getQuantityOverduedateBook() {
        return quantityOverduedateBook;
    }

    /**
     * Sets the quantity of overdue books.
     *
     * @param quantityOverduedateBook the new quantity of overdue books
     */
    public void setQuantityOverduedateBook(int quantityOverduedateBook) {
        this.quantityOverduedateBook = quantityOverduedateBook;
    }

    /**
     * Counts the number of borrowed and overdue books.
     *
     * @throws Exception if an error occurs during the operation
     */
    public void countQuantity() throws Exception {
        String query = "SELECT COUNT(*) AS quantityBorrowedBook FROM booklogs WHERE phone_user = ? AND status in (\'active\', \'overdue\')";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.quantityBorrowedBook = rs.getInt("quantityBorrowedBook");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        String query2 = "Select Count(*) as quantityOverDueDateBook From booklogs WHERE phone_user = ? and dueDate < ? and status in (\'active\', \'overdue\')";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query2)) {

            stmt.setString(1, phone);
            stmt.setString(2, formattedDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.quantityOverduedateBook = rs.getInt("quantityOverDueDateBook");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds user data to the database.
     *
     * @throws Exception if an error occurs during the operation
     */
    public void addData() throws Exception {
        String query = "INSERT INTO user (name, phone, password, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a user by phone number.
     *
     * @param phoneIn the phone number to search for
     * @return the User object if found, otherwise null
     * @throws Exception if an error occurs during the operation
     */
    public static User getUser(String phoneIn) throws Exception {
        String query = "SELECT * FROM librarymanagement.user WHERE phone = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneIn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String profilePicture = rs.getString("profile_picture"); // Đảm bảo lấy đúng cột

                User user = new User(name, email, phone, password);
                user.setProfilePicture(profilePicture); // Cập nhật ảnh đại diện
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the user's information in the database.
     *
     * @throws Exception if an error occurs during the operation
     */
    public void Update() throws Exception {
        String query = "UPDATE librarymanagement.user SET name = ?, password = ?, profile_picture = ? WHERE phone = ?";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set giá trị cho các trường cần cập nhật
            stmt.setString(1, this.getName()); // Cập nhật tên người dùng
            stmt.setString(2, this.getPassword()); // Cập nhật mật khẩu đã mã hóa
            stmt.setString(3, this.getProfilePicture()); // Cập nhật ảnh đại diện (nếu có thay đổi)
            stmt.setString(4, this.getPhone()); // Dùng số điện thoại làm điều kiện cập nhật

            // Thực hiện cập nhật
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Lỗi cập nhật thông tin người dùng.");
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param phone_user the phone number of the user to delete
     * @throws Exception if an error occurs during the operation
     */
    public void DeleteUser(String phone_user) throws Exception {
        String query2 = "DELETE FROM booklogs WHERE phone_user = ?";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query2)) {

            stmt.setString(1, phone_user);

            stmt.executeUpdate(); // Thực hiện câu lệnh DELETE
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "DELETE FROM user WHERE phone = ?";

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phone_user);

            stmt.executeUpdate();
            ; // Thực hiện câu lệnh DELETE
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPassword(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);
    }
}