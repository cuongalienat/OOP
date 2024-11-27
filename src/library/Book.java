package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import Controller.loginController;

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
    private String description;

    public Book() {
    }

    public Book(int id) {
        this.id = id;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Book(Integer id, String collection, String name, String author, Integer available) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
        this.available = available;
    }

    public Book(String collection, String name, String author, Integer id, Integer available) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
        this.available = available;
    }

    public Book(Integer id, String collection, String name, String author, Integer available, String imageUrl, String description){
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
        this.available = available;
        this.imageSrc = imageUrl;
        this.description = description;
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

    // public String getImageSrc() {
    // return imageSrc;
    // }

    // public void setImageSrc(String imageSrc) {
    // this.imageSrc = imageSrc;
    // }

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

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getAvailable() {
        return this.available;
    }

    // public void addData() throws Exception {
    //     // using " ` " to border collumns contain space
    //     String query = "INSERT INTO book (`Offer Collection`, `Book Title`, `Contributors`, `ID`, `available`) VALUES (?, ?, ?, ?, ?)";
    //     try (Connection conn = DbConfig.connect();
    //             PreparedStatement stmt = conn.prepareStatement(query)) {

    //         stmt.setString(1, collection);
    //         stmt.setString(2, name);
    //         stmt.setString(3, author);
    //         stmt.setInt(4, id);
    //         stmt.setInt(5, available);
    //         stmt.executeUpdate();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void addToDatabase() throws Exception {
    String checkQuery = "SELECT Available FROM book WHERE ID = ?";
    String updateQuery = "UPDATE book SET Available = Available + 1 WHERE ID = ?";
    String insertQuery = "INSERT INTO book (ID, `Offer Collection`, `Book Title`, Contributors, Available, ImageLink, Description) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DbConfig.connect()) {
        // Kiểm tra xem sách đã tồn tại chưa
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Nếu sách đã tồn tại, cập nhật số lượng
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, id);
                    updateStmt.executeUpdate();
                }
            } else {
                // Nếu sách chưa tồn tại, thêm mới
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, id);
                    insertStmt.setString(2, collection);
                    insertStmt.setString(3, name);
                    insertStmt.setString(4, author);
                    insertStmt.setInt(5, available);
                    insertStmt.setString(6, imageSrc);
                    insertStmt.setString(7, description);
                    insertStmt.executeUpdate();
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new Exception("Error adding/updating book in database.");
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
        String query = "SELECT ID, `Offer Collection`, `Book Title`, Contributors, Available, ImageLink, Description FROM book WHERE Available > 0";
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                // Lấy các trường từ cơ sở dữ liệu
                Integer id = rs.getInt("ID");
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                Integer available = rs.getInt("Available");
                String imageLink = rs.getString("ImageLink"); // Thêm đường dẫn hình ảnh
                String description = rs.getString("Description"); // Thêm mô tả sách
    
                // Tạo đối tượng Book với đầy đủ thông tin
                Book book = new Book(id, collection, name, author, available, imageLink, description);
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
    

    public void addBorrowedBookToDB(LocalDate selectedDate) throws Exception {
        LocalDate borrowedDate = LocalDate.now();
        LocalDate dueDate = selectedDate;
        User nUser = loginController.getUser_now();
        String checkAvailableQuery = "SELECT Available FROM book WHERE ID = ?";
        String insertQuery = "INSERT INTO booklogs (book_id, phone_user, borrowedDate, dueDate, status) VALUES (?, ?, ?, ?, ?)";
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
                    insertStmt.setString(5, "Pending");

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

    public static List<Book> searchBookByTitle(String Title) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book WHERE `Book Title` LIKE ? ";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + Title + "%");
            ResultSet rs = stmt.executeQuery();
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

    public static List<Book> searchBookByCollections(String Collection) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book WHERE `Offer Collection` LIKE ? ";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + Collection + "%");
            ResultSet rs = stmt.executeQuery();
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

    public static List<Book> searchBookByAuthor(String Contributor) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book WHERE `Contributors` LIKE ? ";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + Contributor + "%");
            ResultSet rs = stmt.executeQuery();
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

    public static List<Book> searchBookByID(int ID) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT * FROM book WHERE Id =  ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
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

    public void updateBookInDatabase() throws Exception {
        String query = "UPDATE book SET `Book Title` = ?, Contributors = ?, `Offer Collection` = ?, Available = ? WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, author);
            stmt.setString(3, collection);
            stmt.setInt(4, available);
            stmt.setInt(5, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating book with ID: " + id);
        }
    }

    public void deleteBookFromDatabase(int bookId) throws Exception {
        String query = "DELETE FROM book WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            System.out.println("deleted in database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
