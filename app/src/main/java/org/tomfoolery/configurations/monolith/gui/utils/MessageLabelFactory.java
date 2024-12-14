package org.tomfoolery.configurations.monolith.gui.utils;

import javafx.scene.control.Label;

public class MessageLabelFactory {
    public static String ERROR_TEXT_FILL = "#bf616a";
    public static String SUCCESS_TEXT_FILL = "#a3be8c";
    public static String FONT_FAMILY = "Segoe UI Variable";

    public static Label createErrorLabel(String message, int fontSize, Label label) {
        label.setText(message);
        label.setStyle("-fx-font-size: " + fontSize + "; -fx-font-family: " + FONT_FAMILY + "; -fx-text-fill: " + ERROR_TEXT_FILL + ";");
        label.setVisible(true);
        return label;
    }

    public static Label createSuccessLabel(String message, int fontSize, Label label) {
        label.setText(message);
        label.setStyle("-fx-font-size: " + fontSize + "; -fx-font-family: " + FONT_FAMILY + "; -fx-text-fill: " + SUCCESS_TEXT_FILL + ";");
        label.setVisible(true);
        return label;
    }
}
