package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager stageManager = new StageManager(primaryStage);
        stageManager.openMenu("/fxml/Patron/Dashboard.fxml", );
    }
}