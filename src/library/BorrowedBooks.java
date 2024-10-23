package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.mysql.cj.xdevapi.Result;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import view.BookDetailsController;
import view.loginController;

public class BorrowedBooks extends Book {
    private LocalDate borrowDate;
    private LocalDate dueDate;
    public static ObservableList<BorrowedBooks> borrowedBooksList = FXCollections.observableArrayList();
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BorrowedBooks(String collection, String name, String author, int id, int available, LocalDate borrowDate, LocalDate dueDate) {
        super(collection, name, author, id, available);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
    public BorrowedBooks(String collection, String name, String author, int id, LocalDate borrowDate, LocalDate dueDate) {
        super(collection, name, author, id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    // public static List<BorrowedBooks> getBorrowedBooks() {
    //     List<BorrowedBooks> borrowedBooksList = new ArrayList<>();
    //     String query = "SELECT b.ID, b.`Offer Collection`, b.`Book Title`, b.Contributors, bl.borrowedDate, bl.dueDate " +
    //                    "FROM book b " +
    //                    "JOIN booklogs bl ON b.ID = bl.book_id " +
    //                    "WHERE bl.phone_user = ?";
    
    //     try (Connection conn = DbConfig.connect();
    //          PreparedStatement stmt = conn.prepareStatement(query)) {
             
    //         stmt.setString(1, loginController.getUser_now().getPhone());
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             while (rs.next()) {
    //                 int id = rs.getInt("ID");
    //                 String offerCollection = rs.getString("Offer Collection");
    //                 String bookTitle = rs.getString("Book Title");
    //                 String contributors = rs.getString("Contributors");
    //                 LocalDate borrowDate = rs.getDate("borrowedDate").toLocalDate();
    //                 LocalDate dueDate = rs.getDate("dueDate").toLocalDate();
    
    //                 BorrowedBooks borrowedBook = new BorrowedBooks(offerCollection, bookTitle, contributors, id, 0, borrowDate, dueDate);
    //                 borrowedBooksList.add(borrowedBook);
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.out.println("SQL Error: " + e.getMessage());
    //         e.printStackTrace();
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    //     return borrowedBooksList;
    // }
    
    @Override
    public void addBorrowedBookToDB() throws Exception {
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
                    insertStmt.setDate(3, java.sql.Date.valueOf(this.getBorrowDate())); // borrowedDate
                    insertStmt.setDate(4, java.sql.Date.valueOf(this.getDueDate())); // dueDate
    
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

    public static ObservableList<BorrowedBooks> getAllBorrowedBooks() throws Exception {
        User user = loginController.getUser_now();
        String userPhone = user.getPhone();
        
        String query = """
            SELECT b.ID AS book_id, b.`Offer Collection`, b.`Book Title`, b.Contributors, bl.borrowedDate, bl.dueDate
            FROM booklogs bl 
            JOIN book b ON bl.book_id = b.ID
            WHERE bl.phone_user = ?     
        """;
    
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userPhone);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String collection = rs.getString("Offer Collection");
                String title = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                LocalDate bDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dDate = rs.getDate("dueDate").toLocalDate();
    
                BorrowedBooks book = new BorrowedBooks(collection, title, author, id, bDate, dDate);
                borrowedBooksList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowedBooksList;
    }    
}
