package yar.pets.smoke_logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SqliteConnectionExample {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:smokelogger.db";
        // truncate data
//        String truncSql = "DELETE FROM smoke_logger;";
//        try {
//             Connection conn = DriverManager.getConnection(url);
//             Statement stmt = conn.createStatement();
//             stmt.executeUpdate(truncSql);
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        // Retrieve days
        System.out.println("days");
        String selectDaysSQL = "SELECT id, day_start, day_end FROM days";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectDaysSQL)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("day_start") + "\t" +
                        rs.getString("day_end"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Log Entries");

        // Retrieve data
        String selectSQL = "SELECT id, ts, value FROM smoke_logger";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("ts") + "\t" +
                        rs.getInt("value"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Entries by day");
        String selectByDaySQL = "SELECT id, ts, value FROM smoke_logger WHERE ts BETWEEN '2024-07-09 22:45:05' AND '2024-08-09 22:45:05'";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectByDaySQL)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("ts") + "\t" +
                        rs.getInt("value"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DateTimeFormatter local = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println(ZonedDateTime.of(LocalDateTime.parse("2024-08-09T22:45:05"), ZoneId.systemDefault()).format(local));
    }
}
