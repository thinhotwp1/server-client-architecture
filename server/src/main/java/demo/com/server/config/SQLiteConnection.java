package demo.com.server.config;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
    private static final String URL = "jdbc:sqlite:./data/database.db";

    public static Connection getConnection() throws SQLException {
        ensureDatabaseExists();
        return DriverManager.getConnection(URL);
    }

    private static void ensureDatabaseExists() {
        File dbFile = new File("./data/database.db");
        if (!dbFile.exists()) {
            try {
                File directory = new File("./data");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                dbFile.createNewFile();
                System.out.println("Database file created at: " + dbFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
