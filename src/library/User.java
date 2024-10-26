package library;

import java.util.Properties;
//import java.util.Map;
//import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class User {
    Scanner sc = new Scanner(System.in);

    private String name;
    private String password;
    private String phone;
    private String email;
    private int quantityBorrowedBook;
    private int quantityOverduedateBook;
    // private Map <String, Book> myMap_Book = new HashMap<>();

    public User() {
    }

    public User(String _name, String _email, String _phone, String _password) {
        email = _email;
        password = _password;
        name = _name;
        phone = _phone;
        quantityBorrowedBook = 0;
        quantityOverduedateBook = 0;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQuantityBorrowedBook() {
        return quantityBorrowedBook;
    }

    public void setQuantityBorrowedBook(int quantityBorrowedBook) {
        this.quantityBorrowedBook = quantityBorrowedBook;
    }

    public int getQuantityOverduedateBook() {
        return quantityOverduedateBook;
    }

    public void setQuantityOverduedateBook(int quantityOverduedateBook) {
        this.quantityOverduedateBook = quantityOverduedateBook;
    }

    public void countQuantity() throws Exception {
        String query = "Select Count(*) as quantityBorrowedBook From booklogs WHERE phone_user = ?";

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

        String query2 = "Select Count(*) as quantityOverDueDateBook From booklogs WHERE phone_user = ? and dueDate > ?";

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

    public void addData() throws Exception {
        String query = "INSERT INTO user.user (name, phone, password, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setString(4, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static User getUser(String phoneIn) throws Exception {
        String query = "SELECT * FROM librarymanagement.user WHERE phone = ?"; 
        // Cập nhật tên bảng với schema user
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneIn);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String password = rs.getString("password");
                String email = rs.getString("email");
    
                User user = new User(name, email, phone, password);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void Update() throws Exception {
        String query = "UPDATE librarymanagement.user SET password = ? WHERE phone = ?"; 
        // Cập nhật tên bảng với schema user
    
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, password);
            stmt.setString(2, phone);
    
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public void showData() {
        System.out.println(name + " " + phone + " " + email + " " + password);
    }

    /*
     * public Map<String, Book> getMyMap_Book() {
     * return myMap_Book;
     * }
     * 
     * 
     * public void setMyMap_Book(Map<String, Book> myMap_Book) {
     * this.myMap_Book = myMap_Book;
     * }
     * 
     * public void rentBook(Book book) {
     * myMap_Book.put(book.getNameB(), book);
     * }
     * 
     * public void returnBook(Book book) {
     * String nameB = sc.nextLine();
     * if (!myMap_Book.containsKey(nameB)){
     * System.out.println("ban da tra sach r");
     * } else {
     * myMap_Book.remove(nameB);
     * System.out.println("tra sach thanh cong");
     * }
     * }
     * 
     * public void showRentedBook() {
     * int count = 1;
     * for (Map.Entry<String, Book> entry : myMap_Book.entrySet()){
     * System.out.println("");
     * System.out.print(count + ", ");
     * entry.getValue().showBookUser();
     * count++;
     * }
     * }
     */
}