package yar.pets.smoke_logger;

public class DayEntry {
    private int id;
    private String dayStart;
    private String dayEnd;

    public DayEntry(int id, String dayStart, String dayEnd){
        this.id = id;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public String getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(String dayEnd) {
        this.dayEnd = dayEnd;
    }

    @Override
    public String toString(){
        return String.format("{ id: %d, dayStart: %s, dayEnd: %s }", this.id, this.dayStart, this.dayEnd);
    }
}
