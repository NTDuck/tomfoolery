package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

import java.io.IOException;

public abstract class BaseView {
    protected StackPane root;
    protected HBox mainView;
    protected VBox sidebar;
    protected Parent content;

    public BaseView() {
        root = new StackPane();
        mainView = new HBox();
        mainView.setPrefHeight(720);
        mainView.setPrefWidth(1280);
    }

    public void loadView(StageManager.ContentType contentType) {
        loadSidebar();
        loadContent(contentType);

        mainView.getChildren().add(sidebar);
        mainView.getChildren().add(content);

        root.getChildren().setAll(mainView);

        StageManager.getInstance().setMainStageProperties();
        StageManager.getInstance().getPrimaryStage().setScene(new Scene(root));

        StageManager.getInstance().getPrimaryStage().show();
    }

    public abstract void loadSidebar();

    public abstract void loadContent(StageManager.@NonNull ContentType contentType);
}
