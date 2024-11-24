package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.NoArgsConstructor;
import org.tomfoolery.configurations.monolith.gui.StageManager;

import java.io.IOException;

@NoArgsConstructor
public abstract class BaseSidebar {
    @FXML
    protected Button dashboardButton;

    @FXML
    protected Button discoverButton;

    @FXML
    protected Button profileButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> goToDashboard());
        discoverButton.setOnAction(event -> goToDiscover());
        profileButton.setOnAction(event -> openLogOutView());
    }

    public abstract void goToDiscover();

    public abstract void goToDashboard();

    public void openLogOutView() {
        try {
            LogOutView controller = new LogOutView (
                    StageManager.getInstance().getUserRepositories(),
                    StageManager.getInstance().getAuthenticationTokenGenerator(),
                    StageManager.getInstance().getAuthenticationTokenRepository()
            );
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogOutView.fxml"));
            loader.setController(controller);
            VBox dialogPane = loader.load();

            // Create a stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setTitle("Logout");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(dialogPane));
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
