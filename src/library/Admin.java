package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    public Admin(String name, String email, String phone, String password) {
        super(name, email, phone, password);
    }

    public static Admin getUser(String phoneIn) throws Exception {
        String query = "SELECT * FROM admin WHERE phone = ? ";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneIn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String password = rs.getString("password");
                String email = rs.getString("email");

                Admin admin = new Admin(name, email, phone, password);
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> showUserData() throws Exception {
        List<User> List_user = new ArrayList<>();
        String query1 = "SELECT * FROM user";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt1 = conn.prepareStatement(query1);) {
            ResultSet rs1 = stmt1.executeQuery();

            while (rs1.next()) {
                String name = rs1.getString("name");
                String phone = rs1.getString("phone");
                String password = rs1.getString("password");
                String email = rs1.getString("email");
                User user = new User(name, email, phone, password);
                user.countQuantity();
                List_user.add(user);
            }
        }
        String query2 = "SELECT * FROM admin";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt1 = conn.prepareStatement(query2);) {
            ResultSet rs1 = stmt1.executeQuery();

            while (rs1.next()) {
                String name = rs1.getString("name");
                String phone = rs1.getString("phone");
                String password = rs1.getString("password");
                String email = rs1.getString("email");
                User user = new User(name, email, phone, password);
                user.countQuantity();
                List_user.add(user);
            }
        }
        return List_user;
    }
}
