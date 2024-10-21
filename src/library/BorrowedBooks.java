package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import view.loginController;

public class BorrowedBooks extends Book {
    private LocalDate borrowDate;
    private LocalDate dueDate;

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

    public BorrowedBooks(String collection, String name, String author, int id, LocalDate borrowDate, LocalDate dueDate) {
        super(collection, name, author, id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public void addBorrowedBookToDB() throws Exception {
        User nUser = loginController.getUser_now();
        String query = "INSERT INTO booklogs (book_id, phone_user, borrowedDate, dueDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect()) { // Kết nối đến CSDL
            String userPhone = nUser.getPhone();
             // Tắt kiểm tra khóa ngoại
            try (PreparedStatement stmtSafeOff = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                stmtSafeOff.executeUpdate();
            }

            // Thực hiện câu lệnh INSERT
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, this.getId()); // book_id
                stmt.setString(2, userPhone); // phone_user
                stmt.setDate(3, java.sql.Date.valueOf(this.getBorrowDate())); // borrowedDate
                stmt.setDate(4, java.sql.Date.valueOf(this.getDueDate())); // dueDate

                stmt.executeUpdate();
                System.out.println("Thêm vào cơ sở dữ liệu thành công!");
            } catch (SQLException e) {
                System.err.println("Lỗi khi thêm vào CSDL: " + e.getMessage());
                throw e; // Ném lại ngoại lệ
            }

            // Bật lại kiểm tra khóa ngoại
            try (PreparedStatement stmtSafeOn = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
                stmtSafeOn.executeUpdate();
            }
        }
    }
}
