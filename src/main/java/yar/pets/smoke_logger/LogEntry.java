package yar.pets.smoke_logger;

public class LogEntry {
    private int id;
    private String timestamp;
    private int value;

    // Constructor
    public LogEntry(int id, String timestamp, int value) {
        this.id = id;
        this.timestamp = timestamp;
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
