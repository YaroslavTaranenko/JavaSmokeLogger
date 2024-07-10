package yar.pets.smoke_logger;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogEntry {
    private int id;
    private String timestamp;
    private int value;

    // Constructor
    public LogEntry(int id, @NotNull Timestamp timestamp, int value) {
        DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime utcDateTime = LocalDateTime.parse(timestamp.toLocalDateTime().format(utcFormatter), utcFormatter);
        ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String localTimestamp = localZonedDateTime.format(localFormatter);

        this.id = id;
        this.timestamp = localTimestamp;
        this.value = value;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", timestamp:'" + timestamp + '\'' +
                ", value:" + value +
                '}';
    }
}
