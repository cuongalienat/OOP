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

/**
 * Represents a book in the library system.
 */
public class Book {
    Scanner sc = new Scanner(System.in);

    private String name;
    private String author;
    private String collection;
    private int id;
    private int available;

    /**
     * Default constructor.
     */
    public Book() {
    }

    /**
     * Constructs a Book with a specific ID.
     *
     * @param id the book ID
     */
    public Book(int id) {
        this.id = id;
    }

    /**
     * Constructs a Book with detailed information.
     *
     * @param collection the collection the book belongs to
     * @param name       the title of the book
     * @param author     the author of the book
     * @param id         the book ID
     * @param available  the number of available copies
     */
    public Book(String collection, String name, String author, Integer id, Integer available) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
        this.available = available;
    }

    /**
     * Constructs a Book with detailed information.
     *
     * @param collection the collection the book belongs to
     * @param name       the title of the book
     * @param author     the author of the book
     * @param id         the book ID
     */
    public Book(String collection, String name, String author, Integer id) {
        this.collection = collection;
        this.name = name;
        this.author = author;
        this.id = id;
    }

    /**
     * Gets the book's name.
     *
     * @return the name of the book
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the book's name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the book's author.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the book's author.
     *
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the book's ID.
     *
     * @return the ID of the book
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the book's ID.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the book's collection.
     *
     * @return the collection of the book
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Sets the book's collection.
     *
     * @param collection the collection to set
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * Sets the availability of the book.
     *
     * @param available the availability to set
     */
    public void setAvailable(int available) {
        this.available = available;
    }

    /**
     * Gets the availability of the book.
     *
     * @return the availability of the book
     */
    public int getAvailable() {
        return this.available;
    }

    /**
     * Adds the book data to the database.
     *
     * @throws Exception if an error occurs during the operation
     */
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

    /**
     * Retrieves a book by its ID.
     *
     * @param inputID the ID of the book to retrieve
     * @return the Book object if found, otherwise null
     * @throws Exception if an error occurs during the operation
     */
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

    /**
     * Retrieves the list of all books in the library.
     *
     * @return the list of all books
     */
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

    /**
     * Retrieves the list of available books in the library.
     *
     * @return the list of available books
     */
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

    /**
     * Adds a borrowed book to the database.
     *
     * @param selectedDate the due date for the borrowed book
     * @throws Exception if an error occurs during the operation
     */
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

    /**
     * Searches for books by title.
     *
     * @param Title the title to search for
     * @return the list of books matching the title
     */
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

    /**
     * Searches for books by collection.
     *
     * @param Collection the collection to search for
     * @return the list of books matching the collection
     */
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

    /**
     * Searches for books by author.
     *
     * @param Contributor the author to search for
     * @return the list of books matching the author
     */
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
                bookList.add(book); // Thêm vào danh s��ch
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

    /**
     * Searches for books by ID.
     *
     * @param ID the ID to search for
     * @return the list of books matching the ID
     */
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

    /**
     * Updates the book information in the database.
     *
     * @throws Exception if an error occurs during the operation
     */
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

    /**
     * Deletes the book from the database.
     *
     * @param bookId the ID of the book to delete
     * @throws Exception if an error occurs during the operation
     */
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
