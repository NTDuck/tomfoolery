package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;

public class BaseView {
    protected HBox mainView;
    protected VBox sidebar;
    protected Parent content;

    public BaseView() {
        mainView = new HBox();
        mainView.setPrefHeight(720);
        mainView.setPrefWidth(1280);
    }
}
