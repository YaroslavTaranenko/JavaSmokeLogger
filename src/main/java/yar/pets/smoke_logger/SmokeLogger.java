package yar.pets.smoke_logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;

public class SmokeLogger {

    private static final Logger logger = LoggerFactory.getLogger(SmokeLogger.class);

    public static void main(String[] args){
        new LoggerWindow();
    }

    public static class LoggerWindow extends JFrame {
        private final JTextArea logArea;
        private int _lastCounterValue;
        private final String _fileName = "log.txt";

        public LoggerWindow() throws HeadlessException{

            setSize(400, 700);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setTitle("Smoke logger");

            logArea = new JTextArea();
            Document doc = logArea.getDocument();
            doc.addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    scrollToBottom();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    scrollToBottom();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    scrollToBottom();
                }
                private void scrollToBottom(){
                    SwingUtilities.invokeLater(() -> logArea.setCaretPosition(logArea.getDocument().getLength()));
                }
            });
            JScrollPane scrollPane = new JScrollPane(logArea);


            JPanel actionPanel = new JPanel();
            actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
            actionPanel.setSize(new Dimension(155, 700));

            Dimension btnSize = new Dimension(140, 35);

            JButton btnNext = new JButton("+");
            btnNext.setSize(btnSize);
            btnNext.addActionListener(e -> this.incrementCounter());
            JButton btnPrev = new JButton("-");
            btnPrev.setSize(btnSize);
            btnPrev.addActionListener(e -> this.decrementCounter());
            JButton btnReset = new JButton("Reset");
            btnReset.setSize(btnSize);
            btnReset.addActionListener(e -> this.resetCounter());

            // Set the dark theme colors
            Color darkBackground = new Color(30, 30, 30); // Dark gray background
            Color lightText = new Color(200, 200, 200); // Light gray text
            Color accentColor = new Color(0, 150, 255); // Blue accent color

            // Set the background color of the logArea
            logArea.setBackground(darkBackground);
            logArea.setForeground(lightText);

            // Set the background color of the actionPanel
            actionPanel.setBackground(darkBackground);

            // Set the foreground color for the buttons (optional)
            btnNext.setForeground(accentColor);
            btnPrev.setForeground(accentColor);
            btnReset.setForeground(accentColor);
            btnNext.setBackground(darkBackground);
            btnPrev.setBackground(darkBackground);
            btnReset.setBackground(darkBackground);

            actionPanel.add(btnNext);
            actionPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            actionPanel.add(btnPrev);
            actionPanel.add(Box.createHorizontalGlue());
            actionPanel.add(btnReset);

            add(scrollPane, BorderLayout.CENTER);
            add(actionPanel, BorderLayout.NORTH);

            this.loadFile();

            setVisible(true);
        }

        private void decrementCounter() {
            _lastCounterValue--;
            logAction();
        }

        private void incrementCounter() {
            _lastCounterValue++;
            logAction();
        }

        private void resetCounter() {
            _lastCounterValue = 0;

            try {
                File file = new File(_fileName);
                if (file.exists()) {
                    if(file.delete()) {
                        this.logArea.setText("");
                        this.append("New day started.");
                    }else{
                        this.append("File '" + _fileName + "' was not deleted.");
                    }
                }
            } catch (SecurityException e) {
                logger.error("Error deleting: ", e);
            }
        }

        private void logAction() {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = now.format(formatter);

            try (FileWriter fileWriter = new FileWriter(_fileName, true)) {
                String newLine = formattedDateTime + " - " + _lastCounterValue;
                fileWriter.write(newLine + "\n");
                this.append(newLine);
            } catch (IOException e) {
                logger.error("Error writing to file: ", e);
            }
        }

        private void loadFile(){
            File _logFile = new File(_fileName);
            try{
                if(_logFile.createNewFile()){
                    this.append("New day started.");
                }else{
                    // Загружаем данные из файла
                    try(BufferedReader reader = new BufferedReader(new FileReader(_logFile))){
                        String line;
                        String lastLine; // Переменная для хранения последней строки
                        while((line = reader.readLine()) != null){
                            // Обрабатываем каждую строку из файла
                            lastLine = line;
                            // Извлекаем значение счетчика из строки
                            String[] parts = line.split(" - ");
                            if (parts.length == 2) {
                                try {
                                    _lastCounterValue = Integer.parseInt(parts[1]);
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid counter value in the file.", e);
                                }
                                // Выводим последнюю строку
                                this.append(lastLine);
                            }
                        }
                    } catch(IOException e) {
                        logger.error("An error occurred while reading the file.", e);
                    }
                }
            }catch(IOException e){
                logger.error("An error occurred while loading the file.", e);
            }
        }

        private void append(String str) {
            this.logArea.append(str + "\n");
        }
    }
}
