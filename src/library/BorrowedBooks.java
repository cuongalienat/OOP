package library;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import Controller.loginController;

/**
 * Represents a borrowed book in the library system.
 */
public class BorrowedBooks extends Book {
    private String phone_user;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;

    /**
     * Gets the user's phone number.
     *
     * @return the phone number
     */
    public String getPhone_user() {
        return phone_user;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone_user the phone number to set
     */
    public void setPhone_user(String phone_user) {
        this.phone_user = phone_user;
    }

    /**
     * Gets the status of the borrowed book.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the borrowed book.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the borrow date of the book.
     *
     * @param borrowDate the borrow date to set
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Sets the due date of the book.
     *
     * @param dueDate the due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the borrow date of the book.
     *
     * @return the borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Gets the due date of the book.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Constructs a BorrowedBooks object with the specified details.
     *
     * @param id         the ID of the book
     * @param phone_user the user's phone number
     * @param borrowDate the borrow date
     * @param dueDate    the due date
     * @param status     the status of the borrowed book
     */
    public BorrowedBooks(int id, String phone_user, LocalDate borrowDate, LocalDate dueDate, String status) {
        super(id);
        this.phone_user = phone_user;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    /**
     * Constructs a BorrowedBooks object with the specified details.
     *
     * @param collection the collection of the book
     * @param name       the name of the book
     * @param author     the author of the book
     * @param id         the ID of the book
     * @param available  the availability status of the book
     * @param borrowDate the borrow date
     * @param dueDate    the due date
     * @param status     the status of the borrowed book
     */
    public BorrowedBooks(String collection, String name, String author, int id, int available, LocalDate borrowDate,
            LocalDate dueDate, String status) {
        super(collection, name, author, id, available);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    /**
     * Constructs a BorrowedBooks object with the specified details.
     *
     * @param collection the collection of the book
     * @param name       the name of the book
     * @param author     the author of the book
     * @param id         the ID of the book
     * @param borrowDate the borrow date
     * @param dueDate    the due date
     * @param status     the status of the borrowed book
     */
    public BorrowedBooks(String collection, String name, String author, int id, LocalDate borrowDate, LocalDate dueDate,
            String status) {
        super(collection, name, author, id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    /**
     * Adds the borrowed book to the database.
     */
    public void addBorrowedBookToDB() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() { // xử lí đa luồng
                try {
                    User nUser = loginController.getUser_now();
                    String checkAvailableQuery = "SELECT Available FROM book WHERE ID = ?";
                    String insertQuery = "INSERT INTO booklogs (book_id, phone_user, borrowedDate, dueDate, status) VALUES (?, ?, ?, ?, ?)";
                    String updateAvailableQuery = "UPDATE book SET Available = Available - 1 WHERE ID = ?";

                    try (Connection conn = DbConfig.connect()) {
                        String userPhone = nUser.getPhone();
                        int available = 0;

                        // Kiểm tra giá trị available
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkAvailableQuery)) {
                            checkStmt.setInt(1, getId());
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    available = rs.getInt("Available");
                                }
                            }
                        }

                        if (available <= 0) {
                            // Không còn sách để mượn
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setContentText("Sách đã hết, không thể mượn.");
                                alert.showAndWait();
                            });
                        } else {
                            // Tắt kiểm tra khóa ngoại
                            try (PreparedStatement stmtSafeOff = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                                stmtSafeOff.executeUpdate();
                            }

                            // Thêm vào booklogs
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                insertStmt.setInt(1, getId());
                                insertStmt.setString(2, userPhone);
                                insertStmt.setDate(3, Date.valueOf(borrowDate));
                                insertStmt.setDate(4, Date.valueOf(dueDate));
                                insertStmt.setString(5, status);
                                insertStmt.executeUpdate();
                            }

                            // Cập nhật số lượng sách
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateAvailableQuery)) {
                                updateStmt.setInt(1, getId());
                                updateStmt.executeUpdate();
                            }

                            // Bật lại kiểm tra khóa ngoại
                            try (PreparedStatement stmtSafeOn = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
                                stmtSafeOn.executeUpdate();
                            }

                            // Cập nhật giao diện người dùng
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setContentText("Mượn sách thành công.");
                                alert.showAndWait();
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Đã xảy ra lỗi trong quá trình mượn sách.");
                            alert.showAndWait();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start(); // xử lí đa luồng
    }

    /**
     * Returns a book.
     *
     * @param phone_user the user's phone number
     * @param book_id    the ID of the book to return
     * @throws Exception if an error occurs during the operation
     */
    public void returnBook(String phone_user, int book_id) throws Exception {
        String query = "DELETE FROM booklogs WHERE phone_user = ? AND (book_id = ? OR (? IS NULL AND book_id IS NULL))";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phone_user);
            stmt.setObject(2, book_id == 0 ? null : book_id, java.sql.Types.INTEGER);
            stmt.setObject(3, book_id == 0 ? null : book_id, java.sql.Types.INTEGER);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the status of a borrowed book.
     *
     * @param id     the ID of the book
     * @param phone  the user's phone number
     * @param status the new status to set
     * @throws Exception if an error occurs during the operation
     */
    public void updateStatus(int id, String phone, String status) throws Exception {
        String query = "UPDATE booklogs SET status = ? WHERE phone_user = ? and book_id = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, phone_user);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a book can be deleted.
     *
     * @param book_id the ID of the book
     * @return true if the book can be deleted, false otherwise
     * @throws Exception if an error occurs during the operation
     */
    public static boolean CheckBookBeforeDelete(int book_id) throws Exception {
        String query = "Select * from booklogs WHERE book_id = ? and status in ('Active','Overdue')";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, book_id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Checks if a book can be borrowed.
     *
     * @param phone the user's phone number
     * @return true if the book can be borrowed, false otherwise
     * @throws Exception if an error occurs during the operation
     */
    public static boolean CheckBookBeforeBorrow(String phone) throws Exception {
        String query = "Select * from booklogs WHERE phone_user = ? and status = 'Overdue' ";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Gets all borrowed books for the current user.
     *
     * @return an observable list of borrowed books
     * @throws Exception if an error occurs during the operation
     */
    public static ObservableList<BorrowedBooks> getAllBorrowedBooks() throws Exception {
        User user = loginController.getUser_now();
        String userPhone = user.getPhone();
        ObservableList<BorrowedBooks> borrowedBooksList = FXCollections.observableArrayList();

        String query = """
                    SELECT b.ID AS book_id, b.`Offer Collection`, b.`Book Title`, b.Contributors, bl.borrowedDate, bl.dueDate, bl.status
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
                String status = rs.getString("status");

                BorrowedBooks book = new BorrowedBooks(collection, title, author, id, bDate, dDate, status);
                borrowedBooksList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowedBooksList;
    }

    /**
     * Gets all book logs.
     *
     * @return an observable list of book logs
     * @throws Exception if an error occurs during the operation
     */
    public static ObservableList<BorrowedBooks> getBookLogs() throws Exception {
        ObservableList<BorrowedBooks> bookLogsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM booklogs";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String phone_user = rs.getString("phone_user");
                int id = rs.getInt("book_id");
                LocalDate bDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dDate = rs.getDate("dueDate").toLocalDate();
                String status = rs.getString("status");

                BorrowedBooks book = new BorrowedBooks(id, phone_user, bDate, dDate, status);
                bookLogsList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookLogsList;
    }

    /**
     * Searches for book logs by book ID.
     *
     * @param Id the ID of the book
     * @return an observable list of book logs
     * @throws Exception if an error occurs during the operation
     */
    public static ObservableList<BorrowedBooks> searchIdBookLogs(int Id) throws Exception {
        ObservableList<BorrowedBooks> bookLogsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM booklogs where book_id = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String phone_user = rs.getString("phone_user");
                int id = rs.getInt("book_id");
                LocalDate bDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dDate = rs.getDate("dueDate").toLocalDate();
                String status = rs.getString("status");

                BorrowedBooks book = new BorrowedBooks(id, phone_user, bDate, dDate, status);
                bookLogsList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookLogsList;
    }

    /**
     * Searches for book logs by phone number.
     *
     * @param phone the user's phone number
     * @return an observable list of book logs
     * @throws Exception if an error occurs during the operation
     */
    public static ObservableList<BorrowedBooks> searchPhoneBookLogs(String phone) throws Exception {
        ObservableList<BorrowedBooks> bookLogsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM booklogs where Phone_user = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String phone_user = rs.getString("phone_user");
                int id = rs.getInt("book_id");
                LocalDate bDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dDate = rs.getDate("dueDate").toLocalDate();
                String status = rs.getString("status");

                BorrowedBooks book = new BorrowedBooks(id, phone_user, bDate, dDate, status);
                bookLogsList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookLogsList;
    }

    /**
     * Searches for book logs by status.
     *
     * @param Status the status of the book
     * @return an observable list of book logs
     * @throws Exception if an error occurs during the operation
     */
    public static ObservableList<BorrowedBooks> searchStatusBookLogs(String Status) throws Exception {
        ObservableList<BorrowedBooks> bookLogsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM booklogs where Status = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, Status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String phone_user = rs.getString("phone_user");
                int id = rs.getInt("book_id");
                LocalDate bDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dDate = rs.getDate("dueDate").toLocalDate();
                String status = rs.getString("status");

                BorrowedBooks book = new BorrowedBooks(id, phone_user, bDate, dDate, status);
                bookLogsList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookLogsList;
    }
}
