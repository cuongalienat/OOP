package library;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import view.loginController;

import java.util.List;
import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;

public class Book {
    Scanner sc = new Scanner(System.in);

    private String name;
    private String imageSrc;
    private String author;
    private String collection;
    private int id;
    private int available;

    public Book() {

    }

    public Book(String collection, String name, String author, Integer id, Integer available) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
        this.available = available;
    }

    public Book(String collection, String name, String author, Integer id) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getAvailable() {
        return this.available;
    }
    public void addData() throws Exception {
        // using " ` " to border collumns contain space
        String query = "INSERT INTO book (`Offer Collection`, `Book Title`, `Contributors`, `ID`, `available`) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, collection);
            stmt.setString(2, name);
            stmt.setString(3, author);
            stmt.setInt(4, id);
            stmt.setInt(5, available);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getBook(String inputID) throws Exception {
        String query = "SELECT * FROM book WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, inputID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                int id = rs.getInt("ID");
                int available = rs.getInt("Available");

                Book book = new Book(collection, name, author, id, available);
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Book> getLibrary() {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book"; // Truy vấn lấy tất cả sách
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                Integer id = rs.getInt("ID");
                Integer available = rs.getInt("Available");

                Book book = new Book(collection, name, author, id, available);
                bookList.add(book); // Thêm vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi kết nối database
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi khác
        }
        return bookList;
    }

    public static List<Book> getAvailableBooks() {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book WHERE Available > 0"; 
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                Integer id = rs.getInt("ID");
                Integer available = rs.getInt("Available");
    
                Book book = new Book(collection, name, author, id, available);
                bookList.add(book); // Thêm vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi kết nối database
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // Xử lý lỗi khác
        }
        return bookList;
    }
    public void addBorrowedBookToDB() throws Exception {
        LocalDate borrowedDate = LocalDate.now();
        LocalDate dueDate = borrowedDate.plusDays(14);
        User nUser = loginController.getUser_now();
        String checkAvailableQuery = "SELECT Available FROM book WHERE ID = ?";
        String insertQuery = "INSERT INTO booklogs (book_id, phone_user, borrowedDate, dueDate) VALUES (?, ?, ?, ?)";
        String updateAvailableQuery = "UPDATE book SET Available = Available - 1 WHERE ID = ?";
    
        try (Connection conn = DbConfig.connect()) { // Kết nối đến CSDL
            String userPhone = nUser.getPhone();
            int available = 0;
    
            // Kiểm tra giá trị available
            try (PreparedStatement checkStmt = conn.prepareStatement(checkAvailableQuery)) {
                checkStmt.setInt(1, this.getId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        available = rs.getInt("Available");
                    }
                }
            }
    
            if (available > 0) {
                // Tắt kiểm tra khóa ngoại
                try (PreparedStatement stmtSafeOff = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                    stmtSafeOff.executeUpdate();
                }
    
                // Thực hiện câu lệnh INSERT
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, this.getId()); // book_id
                    insertStmt.setString(2, userPhone); // phone_user
                    insertStmt.setDate(3, Date.valueOf(borrowedDate)); // borrowedDate
                    insertStmt.setDate(4, Date.valueOf(dueDate)); // dueDate
    
                    insertStmt.executeUpdate();
                    System.out.println("Thêm vào cơ sở dữ liệu thành công!");
                } catch (SQLException e) {
                    System.err.println("Lỗi khi thêm vào CSDL: " + e.getMessage());
                    throw e; // Ném lại ngoại lệ
                }
    
                // Cập nhật giá trị available
                try (PreparedStatement updateStmt = conn.prepareStatement(updateAvailableQuery)) {
                    updateStmt.setInt(1, this.getId());
                    updateStmt.executeUpdate();
                }
    
                // Bật lại kiểm tra khóa ngoại
                try (PreparedStatement stmtSafeOn = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
                    stmtSafeOn.executeUpdate();
                }
            } else {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setContentText("Không thể mượn sách vì không còn sách có sẵn.");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    }
        }
    }
}
