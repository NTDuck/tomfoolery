package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.user.auth.LogUserOutController;

public class LogOutView {
    private final @NonNull LogUserOutController controller;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    public LogOutView(@NonNull UserRepositories userRepositories,
                  @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                  @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.controller = LogUserOutController.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    public void initialize() {
        yesButton.setOnAction(event -> logOut());
        noButton.setOnAction(event -> {
            Stage stage = (Stage) noButton.getScene().getWindow();
            stage.close();
        });
    }

    public void logOut() {
        try {
            this.controller.run();
            this.onSuccess();
        } catch (Exception exception) {
            onException();
        }
    }

    public void closeStage() {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }

    private void onException() {
        System.out.println("Something went wrong, but now you're a Guest anyway");
    }

    private void onSuccess() {
        closeStage();
        StageManager.getInstance().openLoginMenu();
    }
}