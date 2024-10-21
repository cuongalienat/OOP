package library;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

public class Book {
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/user";
    static final String USER = "root";
    static final String PASS = "thoitri0909";
    Scanner sc = new Scanner(System.in);
    
    private String name;
    private String imageSrc;
    private String author;
    private String collection;
    private int id;


    public Book() {

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

    public void addData() throws Exception {
        //using " ` " to border collumns contain space
        String query = "INSERT INTO user.book (`Offer Collection`, `Book Title`, `Contributors`, `ID`) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConfig.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, collection);
            stmt.setString(2, name);
            stmt.setString(3, author);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getBook(String inputID) throws Exception {
        String query = "SELECT * FROM user.book WHERE ID = ?";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, inputID);
            ResultSet rs = stmt.executeQuery();        
            
            if(rs.next()) {
                String collection = rs.getString("Offer Collection");
                String name = rs.getString("Book Title");
                String author = rs.getString("Contributors");
                int id = rs.getInt("ID");

                Book book = new Book(collection, name, author, id);
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
