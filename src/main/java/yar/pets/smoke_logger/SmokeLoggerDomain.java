package yar.pets.smoke_logger;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SmokeLoggerDomain {
    private static final Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
    private int _lastCounterValue;
    private final String _fileName = "log.txt";

    public ArrayList<String> Inits;

    public SmokeLoggerDomain(){
        System.out.println("SmokeLoggerDomain: Constructor called");
        this.Inits = loadFile();
        System.out.println("SmokeLoggerDomain: Constructor finished");
    }

    public String increase(){
        return this.logAction(++this._lastCounterValue);
    }

    public String decrease(){
        return this.logAction(--this._lastCounterValue);
    }

    @org.jetbrains.annotations.NotNull
    private String logAction(int value) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String logEntry = dtf.format(now) + " - " + value;
        writeToFile(logEntry);
        return logEntry;
    }

    public String resetLog() {
        this._lastCounterValue = 0;

        try {
            File file = new File(this._fileName);
            if (file.exists()) {
                if(file.delete()) {
                    return "New day started.";
                }else{
                    logger.error("File '" + _fileName + "' was not deleted.");
                    return "IO error.";
                }
            }else{
                return "New day started.";
            }
        } catch (SecurityException e) {
            logger.error("Error deleting: ", e);
            return "IO deleting file.";
        }
    }

    private void writeToFile(String logEntry) {
        try (FileWriter writer = new FileWriter(_fileName, true)) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            logger.error("An error occurred while writing to the file.", e);
        }
    }

    private @Nullable ArrayList<String> loadFile() {
        System.out.println("SmokeLoggerDomain: loadFile called");
        File _logFile = new File(_fileName);
        try {
            ArrayList<String> inits = new ArrayList<>();
            if (_logFile.createNewFile()) {
                System.out.println("SmokeLoggerDomain: New log file created");
                this.writeToFile("New day started.");
                inits.add("New day started.");
            } else {
                System.out.println("SmokeLoggerDomain: Loading existing log file");
                // Load data from file
                try (BufferedReader reader = new BufferedReader(new FileReader(_logFile))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("SmokeLoggerDomain: Reading line - " + line);
                        // Extract counter value from line
                        String[] parts = line.split(" - ");
                        if (parts.length == 2) {
                            try {
                                _lastCounterValue = Integer.parseInt(parts[1]);
                                System.out.println("SmokeLoggerDomain: Last counter value - " + _lastCounterValue);
                            } catch (NumberFormatException e) {
                                logger.error("Invalid counter value in the file.", e);
                            }
                        }
                        inits.add(line);
                    }

                } catch (IOException e) {
                    logger.error("An error occurred while reading the file.", e);
                }
            }
            return inits;
        } catch (IOException e) {
            logger.error("An error occurred while loading the file.", e);
        }
        System.out.println("SmokeLoggerDomain: loadFile finished");
        return null;
    }
}
