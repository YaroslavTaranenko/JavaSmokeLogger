package yar.pets.smoke_logger;

import java.awt.*;

public class AppTheme {
    private final Color background;
    private final Color foreground;
    private final Color buttonBackground;
    private final Color buttonForeground;

    public AppTheme(Color background, Color foreground, Color buttonBackground, Color buttonForeground) {
        this.background = background;
        this.foreground = foreground;
        this.buttonBackground = buttonBackground;
        this.buttonForeground = buttonForeground;
    }

    public Color getBackground() {
        return background;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getButtonBackground() {
        return buttonBackground;
    }

    public Color getButtonForeground() {
        return buttonForeground;
    }
}
