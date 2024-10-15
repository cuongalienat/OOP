package library;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbConfig {
    public static Connection connect() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/data/config.properties")) {
            props.load(fis);
        }
        String url = props.getProperty("DB_URL");
        String user = props.getProperty("USER");
        String pass = props.getProperty("PASS");

        return DriverManager.getConnection(url, user, pass);

    }
}
