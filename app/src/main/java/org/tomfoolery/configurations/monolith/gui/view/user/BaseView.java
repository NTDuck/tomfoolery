package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public abstract class BaseView {
    protected StackPane rootStackPane;
    protected HBox mainView;
    protected VBox sidebar;
    protected Parent content;

    public BaseView() {
        rootStackPane = new StackPane();
        rootStackPane.setAlignment(Pos.CENTER);
        mainView = new HBox();
    }

    public void loadView(StageManager.ContentType contentType) {
        loadSidebar();
        loadContent(contentType);

        mainView.getChildren().add(sidebar);
        mainView.getChildren().add(content);

        rootStackPane.getChildren().setAll(mainView);

        StageManager.getInstance().setMainStageProperties();
        StageManager.getInstance().getPrimaryStage().setScene(new Scene(rootStackPane));

        StageManager.getInstance().getPrimaryStage().show();
    }

    public abstract void loadSidebar();

    public abstract void loadContent(StageManager.@NonNull ContentType contentType);
}
