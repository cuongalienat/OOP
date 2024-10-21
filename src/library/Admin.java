package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, User> showUserData() throws Exception {
        Map<String, User> Map_user = new HashMap<>();
        String query = "SELECT * FROM user";
        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            User user;
            while (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String password = rs.getString("password");
                String email = rs.getString("email");
                user = new User(name, email, phone, password);
                user.countQuantity();
                Map_user.put(phone, user);
            }
        }
        return Map_user;
    }

}
