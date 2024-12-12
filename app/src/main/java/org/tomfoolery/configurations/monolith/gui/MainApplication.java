package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.val;
import org.tomfoolery.configurations.contexts.test.DeterministicUsersApplicationContextProxy;
import org.tomfoolery.configurations.contexts.test.KaggleDocumentDatasetApplicationContextProxy;
import org.tomfoolery.configurations.contexts.test.MockingApplicationContextProxy;
import org.tomfoolery.configurations.contexts.utils.containers.ApplicationContextProxies;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StageManager.getInstance().setPrimaryStage(primaryStage);
        StageManager.getInstance().openLoginMenu();

        val applicationContextProxies = ApplicationContextProxies.of(List.of(
                MockingApplicationContextProxy.of(),
                DeterministicUsersApplicationContextProxy.of(),
                KaggleDocumentDatasetApplicationContextProxy.of()
        ));
        CompletableFuture.runAsync(() -> applicationContextProxies.intercept(StageManager.getInstance().getResources()));
    }
}