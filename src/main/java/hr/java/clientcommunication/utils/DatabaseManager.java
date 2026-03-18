package hr.java.clientcommunication.utils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileReader fr = new FileReader("database.properties")) {
            props.load(fr);
        }
        return DriverManager.getConnection(
                props.getProperty("databaseUrl"),
                props.getProperty("username"),
                props.getProperty("password"));
    }

}
