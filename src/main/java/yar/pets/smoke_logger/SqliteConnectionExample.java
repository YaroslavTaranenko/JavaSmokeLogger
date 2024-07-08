package yar.pets.smoke_logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteConnectionExample {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:mydatabase.db";

        // Create a new database if it does not exist
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                // Database creation message
                System.out.println("A new database has been created.");

                // Create a new table
                String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + " name TEXT NOT NULL,\n"
                        + " email TEXT NOT NULL\n"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Table created.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Insert data
        String insertSQL = "INSERT INTO users(name, email) VALUES('John Doe', 'john@example.com')";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(insertSQL);
            System.out.println("Data inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Retrieve data
        String selectSQL = "SELECT id, name, email FROM users";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
