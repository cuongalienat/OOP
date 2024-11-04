package library;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import view.loginController;

public class BorrowedBooks extends Book {
    private String phone_user;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;

    public String getPhone_user() {
        return phone_user;
    }

    public void setPhone_user(String phone_user) {
        this.phone_user = phone_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public BorrowedBooks(int id, String phone_user, LocalDate borrowDate, LocalDate dueDate, String status) {
        super(id);
        this.phone_user = phone_user;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public BorrowedBooks(String collection, String name, String author, int id, int available, LocalDate borrowDate,
            LocalDate dueDate, String status) {
        super(collection, name, author, id, available);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public BorrowedBooks(String collection, String name, String author, int id, LocalDate borrowDate, LocalDate dueDate,
            String status) {
        super(collection, name, author, id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public void addBorrowedBookToDB() {
    Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() {  // xử lí đa luồng
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
    new Thread(task).start();  // xử lí đa luồng
}

    public void returnBook(String phone_user, int book_id) throws Exception {
        String query = "DELETE FROM booklogs WHERE phone_user = ? and book_id = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phone_user);
            stmt.setInt(2, book_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
