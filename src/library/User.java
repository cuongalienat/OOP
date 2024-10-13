package library;

import java.io.IOException;
//import java.util.Map;
//import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    static final String DB_URL = "jdbc:mysql://192.168.1.18:3306/librarymanagement";
    static final String USER = "root";
    static final String PASS = "Cuong@2005";
    Scanner sc = new Scanner(System.in);

    private String name;
    private String password;
    private String phone;
    private String email;
    // private Map <String, Book> myMap_Book = new HashMap<>();

    public User() {
    }

    public User(String _name, String _email, String _phone, String _password) {
        email = _email;
        password = _password;
        name = _name;
        phone = _phone;
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

    public void addData() throws IOException {
        String query = "INSERT INTO user (name, phone, password, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

    public static User getUser(String phoneIn) {
        String query = "SELECT * FROM user WHERE phone = ? ";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

    public void Update() throws IOException {
        String query = "UPDATE user SET password = ? WHERE phone = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, password);
            stmt.setString(2, phone);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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