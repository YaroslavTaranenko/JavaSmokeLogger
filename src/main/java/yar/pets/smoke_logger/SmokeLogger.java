package yar.pets.smoke_logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;


public class SmokeLogger {
    private static String _fileName = "log.txt";
    private static File _logFile;
    private static int _lastCounterValue = 0;

    public static void main(String[] args){
        loadFile();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if (command.isEmpty()) {
                incrementCounter();
            } else if (command.equalsIgnoreCase("back")) {
                decrementCounter();
            } else if (command.equalsIgnoreCase("reset")) {
                resetCounter();
            } else if (command.equalsIgnoreCase("exit")){
                exit();
            } else {
                System.out.println("Неверная команда. Используйте пустую строку, 'back' или 'reset'.");
            }
        }
    }

    private static void decrementCounter() {
        _lastCounterValue--;
        logAction("decrease");
    }

    private static void incrementCounter() {
        _lastCounterValue++;
        logAction("increase");
    }

    private static void resetCounter() {
        _lastCounterValue = 0;

        try {
            File file = new File(_fileName);
            if (file.exists()) {
                if(file.delete()) {
                    System.out.println("New day started.");
                }else{
                    System.out.println("File '" + _fileName + "' was not deleted.");
                }
            }
        } catch (SecurityException e) {
            Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
            logger.error("Error deleting: ", e);
        }
    }

    private static void logAction(String action) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(formatter);

        try (FileWriter fileWriter = new FileWriter(_fileName, true)) {
            String newLine = formattedDateTime + " - " + _lastCounterValue + "\n";
            fileWriter.write(newLine);
            System.out.print(newLine);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
            logger.error("Error writing to file: ", e);
        }
    }

    private static void loadFile(){
        _logFile = new File(_fileName);
        try{
            if(_logFile.createNewFile()){
                System.out.println("New day started.");
            }else{
                // Загружаем данные из файла
                try(BufferedReader reader = new BufferedReader(new FileReader(_logFile))){
                    String line;
                    String lastLine = ""; // Переменная для хранения последней строки
                    while((line = reader.readLine()) != null){
                        // Обрабатываем каждую строку из файла
                        lastLine = line;
                        // Извлекаем значение счетчика из строки
                        String[] parts = line.split(" - ");
                        if (parts.length == 2) {
                            try {
                                _lastCounterValue = Integer.parseInt(parts[1]);
                            } catch (NumberFormatException e) {
                                Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
                                logger.error("Invalid counter value in the file.", e);
                            }
                        }
                        System.out.println(lastLine); // Выводим последнюю строку
                    }
                } catch(IOException e) {
                    Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
                    logger.error("An error occurred while reading the file.", e);
                }
            }
        }catch(IOException e){
            Logger logger = LoggerFactory.getLogger(SmokeLogger.class);
            logger.error("An error occurred while loading the file.", e);
        }
    }

    public static void exit() {
        // You might want to add additional cleanup logic here, such as:
        // - Closing any open resources (file handles, network connections, etc.)
        // - Performing any other necessary cleanup tasks

        // Terminate the application
        System.exit(0);
    }

}
