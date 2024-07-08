package yar.pets.smoke_logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHelper {
    private static SQLiteDatabaseHelper instance;
    private Connection connection;
    private final String url = "jdbc:sqlite:smokelogger.db";

    private SQLiteDatabaseHelper() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url);
            initializeDatabase();
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database.", e);
        }
    }

    private void initializeDatabase() throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS smoke_logger ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ts DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "value INTEGER NOT NULL"
                + ");";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createTableQuery);
    }

    public static SQLiteDatabaseHelper getInstance() throws SQLException {
        if (instance == null) {
            instance = new SQLiteDatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }

    public List<LogEntry> getLogEntries() throws SQLException {
        String query = "SELECT id, ts, value FROM smoke_logger";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<LogEntry> logEntries = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String timestamp = resultSet.getString("ts");
            int value = resultSet.getInt("value");
            logEntries.add(new LogEntry(id, timestamp, value));
        }
        return logEntries;
    }

    public LogEntry getLogEntryById(int id) throws SQLException {
        String query = "SELECT id, ts, value FROM smoke_logger WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String timestamp = resultSet.getString("ts");
            int value = resultSet.getInt("value");
            return new LogEntry(id, timestamp, value);
        } else {
            return null;
        }
    }

    public int deleteLogEntry(int id) throws SQLException {
        String query = "DELETE FROM smoke_logger WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        return statement.executeUpdate();
    }

    public LogEntry insertLogEntry(int value) throws SQLException {
        String insertQuery = "INSERT INTO smoke_logger (value) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setInt(1, value);
        statement.executeUpdate();

        // Get the last inserted ID
        ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS id");
        if (rs.next()) {
            int id = rs.getInt("id");
            // Fetch the newly inserted record
            return getLogEntryById(id);
        } else {
            return null;
        }
    }
}