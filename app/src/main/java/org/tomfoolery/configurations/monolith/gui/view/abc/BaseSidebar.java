package org.tomfoolery.configurations.monolith.gui.view.abc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.auth.LogOutView;

@NoArgsConstructor
public abstract class BaseSidebar {
    @FXML
    protected Button dashboardButton;

    @FXML
    protected Button discoverButton;

    @FXML
    protected Button logoutButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> goToDashboard());
        discoverButton.setOnAction(event -> goToDiscover());
        logoutButton.setOnAction(event -> openLogOutView());
    }

    public abstract void goToDiscover();

    public abstract void goToDashboard();

    @SneakyThrows
    public void openLogOutView() {
        LogOutView controller = new LogOutView (
                StageManager.getInstance().getUserRepositories(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogOutView.fxml"));
        loader.setController(controller);

        VBox logoutPopup = loader.load();
        logoutPopup.setMaxSize(400, 250);

        StackPane root = StageManager.getInstance().getRootStackPane();
        root.getChildren().getFirst().setMouseTransparent(true);
        root.getChildren().add(logoutPopup);
    }
}
