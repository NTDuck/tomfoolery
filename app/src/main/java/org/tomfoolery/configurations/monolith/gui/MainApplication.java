package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.getInstance().setPrimaryStage(primaryStage);
        StageManager.getInstance().populateUserRepositories();
        StageManager.getInstance().openLoginMenu();

//        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DASHBOARD);
//        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DISCOVER);
//        StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_CONTROL_CENTER);
//
//        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DASHBOARD);
//        StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DISCOVER);

//        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DASHBOARD);
//        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DISCOVER);
//        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DOCUMENTS_MANAGEMENT);
    }
}