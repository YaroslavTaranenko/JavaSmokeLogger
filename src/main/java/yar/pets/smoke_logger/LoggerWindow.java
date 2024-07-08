package yar.pets.smoke_logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import javax.swing.SwingUtilities;

public class LoggerWindow extends JFrame {
    private final JTextArea logArea;
    private final SmokeLoggerDomain smokeLoggerDomain;
    public LoggerWindow() throws HeadlessException {

        System.out.println("LoggerWindow: Constructor called");
        this.smokeLoggerDomain = new SmokeLoggerDomain();
        System.out.println("LoggerWindow: SmokeLoggerDomain initialized");

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
        btnNext.addActionListener(e -> this.append(this.smokeLoggerDomain.increase()));
        JButton btnPrev = new JButton("-");
        btnPrev.setSize(btnSize);
        btnPrev.addActionListener(e -> this.append(this.smokeLoggerDomain.decrease()));
        JButton btnReset = new JButton("Reset");
        btnReset.setSize(btnSize);
        btnReset.addActionListener(e -> this.resetLog());

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

        this.loadInitialLogs();
        setVisible(true);
        System.out.println("Window is visible.");
    }

    private void resetLog() {
        String logEntry = this.smokeLoggerDomain.resetLog();
        logArea.setText("");
        append(logEntry);
    }

    private void append(String str) {
        logArea.append(str + "\n");
    }

    private void loadInitialLogs() {
        for(int i = 0; i < smokeLoggerDomain.Inits.size(); i++){
            this.append(smokeLoggerDomain.Inits.get(i));
        }
    }

    public static void main(String[] args) {
        System.out.println("Main method started.");
        SwingUtilities.invokeLater(LoggerWindow::new);  // Ensure GUI is created on EDT
    }
}
