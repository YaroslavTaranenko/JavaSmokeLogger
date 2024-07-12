package yar.pets.smoke_logger;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SmokeLoggerDomain {
    private static final Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
    private int _lastCounterValue;
    private int _currentDay;
    private int _totalRecordsCount;
    private SQLiteDatabaseHelper dbHelper;
    private List<LogEntry> records;
    private List<DayEntry> days;

    public SmokeLoggerDomain(){
        System.out.println("SmokeLoggerDomain: Constructor called");
        try {
            dbHelper = SQLiteDatabaseHelper.getInstance();
            days = dbHelper.getDays();
            records = new ArrayList<>();
            if(days == null || days.isEmpty()){
                reset();
                days = dbHelper.getDays();
                records = dbHelper.getLogEntries();
            }
            _currentDay = days.size() - 1;
            records = dbHelper.getLogEntriesByDay(days.get(_currentDay));
            if(!records.isEmpty()) {
                _lastCounterValue = records.get(records.size() - 1).getValue();
            }else {
                records = new ArrayList<>();
                _lastCounterValue = 0;
            }
            _totalRecordsCount = dbHelper.GetTotalRecords();
        } catch(SQLException e) {
            logger.error(e.getMessage());
        }

        System.out.println("SmokeLoggerDomain: Constructor finished");
    }

    public void increase(){
        try {
            LogEntry res = dbHelper.insertLogEntry(++this._lastCounterValue);
            records.add(res);
            _totalRecordsCount = dbHelper.GetTotalRecords();
        }catch(SQLException e){
            logger.error("Insert entry error: " + e);
        }
    }

    public void deleteEntry(@NotNull LogEntry entry){
        try{
            dbHelper.deleteLogEntry(entry.getId());
            records.remove(entry);
            _lastCounterValue = records.isEmpty() ? 0 : records.get(records.size()-1).getValue();
            _totalRecordsCount = dbHelper.GetTotalRecords();
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    public LogEntry updateEntry(@NotNull LogEntry entry){
        try{
            LogEntry updEntry = dbHelper.updateLogEntry(entry.getId());
            entry.setTimestamp(updEntry.getTimestamp());
            return updEntry;
        } catch (SQLException e){
            e.getStackTrace();
        }
        return null;
    }

    public void previousDay() {
        try {
            if (_currentDay > 0) {
                _currentDay--;
                this.records.clear();
                this.records = dbHelper.getLogEntriesByDay(days.get(_currentDay));
            }
        }catch (SQLException e){
            e.getStackTrace();
        }
    }

    public void nextDay(){
        try {
            if (_currentDay >= 0 && _currentDay < days.size() - 1) {
                _currentDay++;
                this.records.clear();
                this.records = dbHelper.getLogEntriesByDay(days.get(_currentDay));
            }
        }catch (SQLException e){
            e.getStackTrace();
        }
    }

    public void reset() {
        this._lastCounterValue = 0;
        try {
            dbHelper.ResetDay();
            days.clear();
            days = dbHelper.getDays();
            _currentDay = days.size() - 1;
            records.clear();
            records = dbHelper.getLogEntriesByDay(days.get(_currentDay));
        } catch (SQLException e) {
            logger.error("Error resetting: ", e);
        }
    }

    public List<LogEntry> getRecords(){
        return this.records;
    }
    public List<DayEntry> getDays(){
        return this.days;
    }

    public int getCurrentDay(){
        return this._currentDay;
    }
    public void setCurrentDay(int value){
        this._currentDay = value;
    }

    public int getTotalEntitiesCount(){
        return this._totalRecordsCount;
    }
}
