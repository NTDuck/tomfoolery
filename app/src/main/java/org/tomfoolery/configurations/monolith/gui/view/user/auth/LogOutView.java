package org.tomfoolery.configurations.monolith.gui.view.user.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.users.authentication.LogUserOutController;

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
        noButton.setOnAction(event -> closePopup());
    }

    public void logOut() {
        try {
            this.controller.run();
            this.onSuccess();
        } catch (Exception exception) {
            onException();
        }
    }

    public void closePopup() {
        StageManager.getInstance().getRootStackPane().getChildren().getFirst().setMouseTransparent(false);
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    private void onException() {
        System.out.println("Something went wrong, but now you're a Guest anyway");
    }

    private void onSuccess() {
        closePopup();
        StageManager.getInstance().openLoginMenu();
    }
}
