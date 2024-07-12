package yar.pets.smoke_logger;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
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
    private JMenuItem menuItemDark;
    private JMenuItem menuItemLight;
    private JButton btnAdd;
    private JScrollPane scrollPane;
    private JPanel bottomPanel;
    private AppTheme theme;

    public LoggerWindow() throws HeadlessException {

        System.out.println("LoggerWindow: Constructor called");
        this.smokeLoggerDomain = new SmokeLoggerDomain();
        System.out.println("LoggerWindow: SmokeLoggerDomain initialized");
        theme = loadTheme("themes/darkTheme.properties");

        setSize(400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Smoke logger");

        setAppIcon();

        initializeComponents();
        drawEntries();
        assignListeners();
        totalLabelChange();

        setVisible(true);
    }

    @Contract("_ -> new")
    private @NotNull AppTheme loadTheme(String themeFile){
        Color background = Color.decode("0x333333");
        Color foreground = Color.decode("0xFFFFFF");
        Color buttonBackground = Color.decode("0x555555");
        Color buttonForeground = Color.decode("0xFFFFFF");

        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(themeFile)) {
            if (input == null) {
                AppLogger.warn("Sorry, unable to find " + themeFile);
                return new AppTheme(background, foreground, buttonBackground, buttonForeground);
            }
            props.load(input);
        } catch (IOException ex) {
            AppLogger.error("IO exception: ", ex);
        }


        background = Color.decode(props.getProperty("background"));
        foreground = Color.decode(props.getProperty("foreground"));
        buttonBackground = Color.decode(props.getProperty("buttonBackground"));
        buttonForeground = Color.decode(props.getProperty("buttonForeground"));
        return new AppTheme(background, foreground, buttonBackground, buttonForeground);
    }

    private void applyTheme(String themeFile){
        theme = loadTheme(themeFile);
//        mainPanel.setBackground(background);
        recordsPanel.setBackground(theme.getBackground());
        topPanel.setBackground(theme.getBackground());
        leftPanel.setBackground(theme.getBackground());
        rightPanel.setBackground(theme.getBackground());
        btnNextDay.setForeground(theme.getButtonForeground());
        btnNextDay.setBackground(theme.getButtonBackground());
        btnPrevDay.setBackground(theme.getButtonBackground());
        btnPrevDay.setForeground(theme.getButtonForeground());
        menu.setBackground(theme.getBackground());
        menu.setForeground(theme.getForeground());
        menuBar.setBackground(theme.getBackground());
        lblDay.setForeground(theme.getForeground());
        btnAdd.setForeground(theme.getForeground());
        btnAdd.setBackground(theme.getBackground());
        menuItemDark.setBackground(theme.getBackground());
        menuItemLight.setBackground(theme.getBackground());
        menuItemReset.setBackground(theme.getBackground());
        menuItemDark.setForeground(theme.getForeground());
        menuItemLight.setForeground(theme.getForeground());
        menuItemReset.setForeground(theme.getForeground());
        bottomPanel.setBackground(theme.getBackground());
        lblTotalEntries.setForeground(theme.getForeground());

        drawEntries();
        repaint();
        revalidate();
    }

    private void setAppIcon() {
        try {
            // Load the .ico file
            InputStream iconStream = getClass().getClassLoader().getResourceAsStream("icons/smoke.png");
            if (iconStream != null) {
                Image icon = ImageIO.read(iconStream);
                setIconImage(icon);
            } else {
                AppLogger.warn("smoke.ico not found");
            }
        } catch (IOException e) {
            AppLogger.error("Error loading application icon: ", e);
        }
    }

    private void assignListeners(){
        bottomPanel.add(lblTotalEntries);
        add(bottomPanel, BorderLayout.SOUTH);

        // Слушатели событий для кнопок
        btnAdd.addActionListener(e -> {
            smokeLoggerDomain.increase();
            totalLabelChange();
            drawEntries();
        });

        menuItemReset.addActionListener(e -> {
            // Логика сброса дня
            resetDay();
            drawEntries();
            btnNextDay.setEnabled(false);
            btnPrevDay.setEnabled(true);
        });

        menuItemLight.addActionListener(e -> {
            applyTheme("themes/lightTheme.properties");
        });

        menuItemDark.addActionListener(e -> applyTheme("themes/darkTheme.properties"));

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
        topPanel.setBackground(theme.getBackground());

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leftPanel.setBackground(theme.getBackground());
        rightPanel.setBackground(theme.getBackground());

        menuBar = new JMenuBar();
        menuBar.setBackground(theme.getBackground());

        menu = new JMenu("Меню");
        menu.setForeground(theme.getForeground());

        menuItemReset = new JMenuItem("Сброс");
        menuItemDark = new JMenuItem("Темная тема");
        menuItemLight = new JMenuItem("Светлая тема");
        menuItemDark.setBackground(theme.getBackground());
        menuItemLight.setBackground(theme.getBackground());
        menuItemReset.setBackground(theme.getBackground());
        menuItemDark.setForeground(theme.getForeground());
        menuItemLight.setForeground(theme.getForeground());
        menuItemReset.setForeground(theme.getForeground());


        btnAdd = new JButton(" + ");
        btnPrevDay = new JButton(" < ");
        btnNextDay = new JButton(" > ");
        btnAdd.setBackground(theme.getButtonBackground());
        btnAdd.setForeground(theme.getButtonForeground());
        btnPrevDay.setBackground(theme.getButtonBackground());
        btnPrevDay.setForeground(theme.getButtonForeground());
        btnNextDay.setBackground(theme.getButtonBackground());
        btnNextDay.setForeground(theme.getButtonForeground());

        menu.add(menuItemReset);
        menu.add(menuItemDark);
        menu.add(menuItemLight);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        navigationEnabling();

        lblDay = new JLabel("dayText"); // Обновляем currentDay по мере изменения
        lblDay.setForeground(theme.getForeground());
        dayLabelChange();

        leftPanel.add(btnAdd);
        rightPanel.add(btnPrevDay);
        rightPanel.add(lblDay);
        rightPanel.add(btnNextDay);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);


        recordsPanel = new JPanel();
        recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.Y_AXIS));
        recordsPanel.setBackground(theme.getBackground());

        scrollPane = new JScrollPane(recordsPanel);
        add(scrollPane, BorderLayout.CENTER);

        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        bottomPanel.setBackground(theme.getBackground());

        lblTotalEntries = new JLabel("total entries");
        lblTotalEntries.setForeground(theme.getForeground());
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

        // Scroll to the bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });

        recordsPanel.revalidate();
        recordsPanel.repaint();
    }

    private @NotNull JPanel logEntryElement(@NotNull LogEntry entry){
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
        entryPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, Color.BLUE, Color.BLUE));
        entryPanel.setBackground(theme.getBackground());

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));
        dataPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        dataPanel.setBackground(theme.getBackground());

        JLabel lblEntry = new JLabel("" + entry.getValue());
        lblEntry.setFont(new Font(null, Font.BOLD, 20));
        lblEntry.setForeground(theme.getForeground());

        JLabel lblDate = new JLabel(entry.getTimestamp());
        lblDate.setForeground(theme.getForeground());
        dataPanel.add(lblEntry);

        JLabel lblSplitter =new JLabel(" - ");
        lblSplitter.setForeground(theme.getForeground());

        dataPanel.add(lblSplitter);
        dataPanel.add(lblDate);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        actionPanel.setBackground(theme.getBackground());

        // Load icons
        ImageIcon deleteIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/trash.png")); // Replace with your actual path
        ImageIcon updateIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/refresh.png"));

        int iconSize = 32;
        Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        Image scaledUpdateImage = updateIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);


        Dimension buttonSize = new Dimension(35, 35);
        JButton btnDelete = new JButton(new ImageIcon(scaledDeleteImage));
        btnDelete.setBackground(theme.getButtonBackground());
        btnDelete.setForeground(theme.getButtonForeground());

        btnDelete.setPreferredSize(buttonSize);
        JButton btnUpdate = new JButton(new ImageIcon(scaledUpdateImage));
        btnUpdate.setPreferredSize(buttonSize);
        btnUpdate.setBackground(theme.getButtonBackground());
        btnUpdate.setForeground(theme.getButtonForeground());

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
