package yar.pets.smoke_logger;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SQLiteDatabaseHelper {
    private static SQLiteDatabaseHelper instance;
    private Connection connection;
    private final String url = "jdbc:sqlite:smokelogger.db";

    public SQLiteDatabaseHelper() throws SQLException {
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
        String createDayTableQuery = "CREATE TABLE IF NOT EXISTS days ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "day_start DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "day_end DATETIME"
                + ");";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createTableQuery);

        Statement dayStmt = connection.createStatement();
        dayStmt.executeUpdate(createDayTableQuery);
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

    public List<LogEntry> getLogEntries() throws SQLException {
        String query = "SELECT id, ts, value FROM smoke_logger";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<LogEntry> logEntries = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            Timestamp timestamp = resultSet.getTimestamp("ts");
            int value = resultSet.getInt("value");
            logEntries.add(new LogEntry(id, timestamp, value));
        }
        return logEntries;
    }

    public List<LogEntry> getLogEntriesByDay(@NotNull DayEntry day) throws SQLException{
        String query = "SELECT id, ts, value FROM smoke_logger WHERE ts BETWEEN ? AND ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, day.getDayStart());
        String end = day.getDayEnd() == null ? "CURRENT_TIMESTAMP" : day.getDayEnd();
        stmt.setString(2, end);
        ResultSet rs = stmt.executeQuery();
        List<LogEntry> logEntries = new ArrayList<>();
        while(rs.next()){
            logEntries.add(new LogEntry(rs.getInt("id"), rs.getTimestamp("ts"), rs.getInt("value")));
        }
        return logEntries;
    }

    public int GetTotalRecords() throws SQLException{
        String query = "SELECT COUNT(id) AS total FROM smoke_logger;";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs.getInt("total");
    }

    public Map<Integer, List<LogEntry>> getAllDaysWithEntries() throws SQLException {
        String query = "SELECT id FROM days ORDER BY start_ts";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        Map<Integer, List<LogEntry>> daysWithEntries = new HashMap<>();
        while (rs.next()) {
            daysWithEntries.put(rs.getInt("id"), getLogEntriesByDay(new DayEntry(rs.getInt("id"), rs.getString("day_start"), rs.getString("day_end"))));
        }
        return daysWithEntries;
    }

    public LogEntry getLogEntryById(int id) throws SQLException {
        String query = "SELECT id, ts, value FROM smoke_logger WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {

            Timestamp timestamp = resultSet.getTimestamp("ts");
            int value = resultSet.getInt("value");
            return new LogEntry(id, timestamp, value);
        } else {
            return null;
        }
    }

    public void deleteLogEntry(int id) throws SQLException {
        String query = "DELETE FROM smoke_logger WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public LogEntry insertLogEntry(int value) throws SQLException {
        String insertQuery = "INSERT INTO smoke_logger (value) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setInt(1, value);
        statement.executeUpdate();

        // Get the last inserted ID

        Statement selectStatement = connection.createStatement();
        ResultSet rs = selectStatement.executeQuery("SELECT last_insert_rowid() AS id");
        if (rs.next()) {
            int id = rs.getInt("id");
            // Fetch the newly inserted record
            return getLogEntryById(id);
        } else {
            return null;
        }
    }

    public LogEntry updateLogEntry(int id) throws SQLException {
        String insertQuery = "UPDATE smoke_logger SET ts=CURRENT_TIMESTAMP WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setInt(1, id);
        statement.executeUpdate();

        return getLogEntryById(id);
    }

    public List<DayEntry> getDays() throws SQLException{
        String query = "SELECT id, day_start, day_end FROM days";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<DayEntry> result = new ArrayList<>();
        while(rs.next()){
            result.add(new DayEntry(rs.getInt("id"), rs.getString("day_start"), rs.getString("day_end")));
        }
        return result;
    }

    public DayEntry getCurrentDay() throws SQLException{
        String query = "SELECT id, day_start, day_end FROM days WHERE day_end IS NULL";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()){
            return new DayEntry(rs.getInt("id"), rs.getString("day_start"), rs.getString("day_end"));
        }else{
            return null;
        }
    }

    public DayEntry getDayById(int id) throws SQLException{
        String query = "SELECT id, day_start, day_end FROM days WHERE id = ?";
        PreparedStatement stm = connection.prepareStatement(query);
        stm.setInt(1, id);
        ResultSet rs = stm.executeQuery();

        return new DayEntry(rs.getInt("id"), rs.getString("day_start"), rs.getString("day_end"));
    }

    public DayEntry ResetDay() throws SQLException{
        String endCurrentDay = "UPDATE days SET day_end = CURRENT_TIMESTAMP WHERE day_end IS NULL;";
        String startNewDay = "INSERT INTO days(day_start) VALUES(CURRENT_TIMESTAMP);";
        String getNewDayId = "SELECT last_insert_rowid() as id;";

        Statement endStm = connection.createStatement();
        endStm.executeUpdate(endCurrentDay);

        Statement newStm = connection.createStatement();
        newStm.executeUpdate(startNewDay);

        Statement lastDayStm = connection.createStatement();
        ResultSet rs = lastDayStm.executeQuery(getNewDayId);

        if(rs.next()){
            int id = rs.getInt("id");
            DayEntry d = getDayById(id);
            return d;
        }else{
            return null;
        }
    }
}