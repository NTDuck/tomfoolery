package org.tomfoolery.configurations.monolith.gui.utils;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class SelectableLabel extends Label {
    public SelectableLabel() {
        setupSelectability();
    }

    @FXML
    private void initialize() {
        setupSelectability();
    }

    private void setupSelectability() {
        setMouseTransparent(false);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(getText());
            clipboard.setContent(content);
        });

        contextMenu.getItems().add(copyItem);
        setContextMenu(contextMenu);
    }
}
