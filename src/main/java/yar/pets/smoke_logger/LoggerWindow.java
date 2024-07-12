package yar.pets.smoke_logger;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

public class LoggerWindow extends JFrame {
    private final SmokeLoggerDomain smokeLoggerDomain;
    private JPanel recordsPanel;
    private JButton btnPrevDay;
    private JButton btnNextDay;
    private JLabel lblDay;
    private JLabel lblTotalEntries;
    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItemReset;
    private JButton btnAdd;
    private JScrollPane scrollPane;
    private JPanel bottomPanel;

    public LoggerWindow() throws HeadlessException {

        System.out.println("LoggerWindow: Constructor called");
        this.smokeLoggerDomain = new SmokeLoggerDomain();
        System.out.println("LoggerWindow: SmokeLoggerDomain initialized");

        setSize(400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Smoke logger");

        initializeComponents();

        drawEntries();

        assignListeners();

        totalLabelChange();
        setVisible(true);
    }

    private void assignListeners(){
        bottomPanel.add(lblTotalEntries);
        add(bottomPanel, BorderLayout.SOUTH);

        // Слушатели событий для кнопок
        btnAdd.addActionListener(e -> {
            smokeLoggerDomain.increase();
            drawEntries();
            totalLabelChange();
        });

        menuItemReset.addActionListener(e -> {
            // Логика сброса дня
            resetDay();
            drawEntries();
            btnNextDay.setEnabled(false);
            btnPrevDay.setEnabled(true);
        });

        btnPrevDay.addActionListener(e -> {
            // Логика переключения на предыдущий день
            previousDay();
            navigationEnabling();
        });

        btnNextDay.addActionListener(e -> {
            // Логика переключения на следующий день
            nextDay();
            navigationEnabling();
        });
    }

    private void initializeComponents(){
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        menuBar = new JMenuBar();
        menu = new JMenu("Меню");
        menuItemReset = new JMenuItem("Сброс");

        btnAdd = new JButton(" + ");
        btnPrevDay = new JButton(" < ");
        btnNextDay = new JButton(" > ");

        navigationEnabling();

        lblDay = new JLabel("dayText"); // Обновляем currentDay по мере изменения
        dayLabelChange();

        leftPanel.add(btnAdd);
        rightPanel.add(btnPrevDay);
        rightPanel.add(lblDay);
        rightPanel.add(btnNextDay);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        menu.add(menuItemReset);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        add(topPanel, BorderLayout.NORTH);

        recordsPanel = new JPanel();
        recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(recordsPanel);
        add(scrollPane, BorderLayout.CENTER);

        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        lblTotalEntries = new JLabel("total entries");
    }

    private void navigationEnabling(){
        btnPrevDay.setEnabled(smokeLoggerDomain.getCurrentDay() > 0);
        System.out.println("Days size: " + smokeLoggerDomain.getDays().size());
        btnNextDay.setEnabled(smokeLoggerDomain.getCurrentDay() < smokeLoggerDomain.getDays().size() -1);
    }

    private void totalLabelChange(){
        int totalEntries = smokeLoggerDomain.getRecords() != null ? smokeLoggerDomain.getTotalEntitiesCount() : 0;
        lblTotalEntries.setText("Всего записей: " + totalEntries);
    }

    private void dayLabelChange(){
        String dayText;
        if(smokeLoggerDomain.getCurrentDay() == smokeLoggerDomain.getDays().size() - 1)
            dayText = "Сегодня";
        else if (smokeLoggerDomain.getCurrentDay() == smokeLoggerDomain.getDays().size() - 2) {
            dayText = "Вчера";
        } else if (smokeLoggerDomain.getCurrentDay() == smokeLoggerDomain.getDays().size() - 3) {
            dayText = "Позавчера";
        } else
            dayText = "День " + smokeLoggerDomain.getCurrentDay();
        lblDay.setText(dayText);
        lblDay.revalidate();
        lblDay.repaint();
    }

    private void drawEntries(){
        recordsPanel.removeAll();
        if(smokeLoggerDomain.getRecords() == null) {
            recordsPanel.revalidate();
            recordsPanel.repaint();
            return;
        }

        for (LogEntry entry : smokeLoggerDomain.getRecords()) {
            recordsPanel.add(logEntryElement(entry));
        }
        recordsPanel.revalidate();
        recordsPanel.repaint();
    }

    private @NotNull JPanel logEntryElement(@NotNull LogEntry entry){
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
        entryPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, Color.BLUE, Color.BLUE));

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));
        dataPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        JLabel lblEntry = new JLabel("" + entry.getValue());
        lblEntry.setFont(new Font(null, Font.BOLD, 20));
        JLabel lblDate = new JLabel(entry.getTimestamp());
        dataPanel.add(lblEntry);
        dataPanel.add(new JLabel(" - "));
        dataPanel.add(lblDate);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

        // Load icons
        ImageIcon deleteIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/trash.png")); // Replace with your actual path
        ImageIcon updateIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/refresh.png"));

        int iconSize = 32;
        Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        Image scaledUpdateImage = updateIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);


        Dimension buttonSize = new Dimension(35, 35);
        JButton btnDelete = new JButton(new ImageIcon(scaledDeleteImage));
        btnDelete.setPreferredSize(buttonSize);
        JButton btnUpdate = new JButton(new ImageIcon(scaledUpdateImage));
        btnUpdate.setPreferredSize(buttonSize);
        actionPanel.add(btnDelete);
        actionPanel.add(btnUpdate);

        btnUpdate.setEnabled(entry.equals(smokeLoggerDomain.getRecords().get(smokeLoggerDomain.getRecords().size() - 1))); // Активна только для последней записи
        btnDelete.setEnabled(entry.equals(smokeLoggerDomain.getRecords().get(smokeLoggerDomain.getRecords().size() - 1))); // Активна только для последней записи

        btnDelete.addActionListener(e -> {
            smokeLoggerDomain.deleteEntry(entry);
            drawEntries();
        });

        btnUpdate.addActionListener(e -> {
            LogEntry updEntry = smokeLoggerDomain.updateEntry(entry);
            lblDate.setText(updEntry.getTimestamp());
            lblDate.revalidate();
            lblDate.repaint();
//            drawEntries();
        });

        entryPanel.add(dataPanel);
        entryPanel.add(Box.createHorizontalGlue());
        entryPanel.add(actionPanel);
        return entryPanel;
    }

    private void resetDay() {
        // Логика сброса дня
        smokeLoggerDomain.reset();
        dayLabelChange();
    }

    private void previousDay() {
        // Логика переключения на предыдущий день
        smokeLoggerDomain.previousDay();
        drawEntries();
        dayLabelChange();
    }

    private void nextDay() {
        // Логика переключения на следующий день
        smokeLoggerDomain.nextDay();
        drawEntries();
        dayLabelChange();
    }

    public static void main(String[] args) {
        System.out.println("Main method started.");
        SwingUtilities.invokeLater(LoggerWindow::new);  // Ensure GUI is created on EDT
    }
}
