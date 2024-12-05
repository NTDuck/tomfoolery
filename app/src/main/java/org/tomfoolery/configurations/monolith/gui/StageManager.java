package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.utils.PopulatedAppResources;
import org.tomfoolery.configurations.monolith.gui.view.admin.layout.AdminView;
import org.tomfoolery.configurations.monolith.gui.view.guest.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.guest.SignupView;
import org.tomfoolery.configurations.monolith.gui.view.patron.layout.PatronView;
import org.tomfoolery.configurations.monolith.gui.view.staff.layout.StaffView;
import java.io.IOException;
import java.util.Objects;

@Getter
public class StageManager {
    private static StageManager instance;

    @Setter
    private Stage primaryStage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public StackPane getRootStackPane() {
        return (StackPane) this.getPrimaryStage().getScene().getRoot();
    }

    private final @NonNull PopulatedAppResources resources = PopulatedAppResources.of();

    public void setLoginStageProperties() {
        primaryStage.setMinHeight(0);
        primaryStage.setMinWidth(0);

        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);

        primaryStage.setWidth(600);
        primaryStage.setHeight(550);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void setSignupStageProperties() {
        primaryStage.setResizable(false);

        primaryStage.setMaximized(false);

        primaryStage.setWidth(900);
        primaryStage.setHeight(600);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void setMainStageProperties() {
        primaryStage.setResizable(true);

        boolean isMaximized = primaryStage.isMaximized();
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        if (currentHeight < 720 || currentWidth < 1280) {
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
        } else {
            primaryStage.setMaximized(isMaximized);
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);
        }

        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1280);

        primaryStage.setTitle("Tomfoolery - Library Management Application");
    }

    public void openLoginMenu() {
        try {
            LoginView controller = new LoginView(
                    resources.getUserRepositories(),
                    resources.getAuthenticationTokenGenerator(),
                    resources.getAuthenticationTokenRepository(),
                    resources.getPasswordEncoder()
            );

            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/LoginMenu.fxml"));
            loader.setController(controller);

            VBox root = loader.load();

            Scene scene = new Scene(root);

            setLoginStageProperties();
            primaryStage.setTitle("Tomfoolery - Login");
            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openSignupMenu() {
        try {
            SignupView signUpController = new SignupView(resources.getPatronRepository(), resources.getPasswordEncoder());

            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/SignupMenu.fxml"));
            loader.setController(signUpController);

            VBox root = loader.load();

            Scene scene = new Scene(root);

            setSignupStageProperties();
            primaryStage.setTitle("Tomfoolery - Sign up");
            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAdminView(StageManager.ContentType contentType) {
        AdminView adminView = new AdminView();
        adminView.loadView(contentType);
    }

    public void loadStaffView(StageManager.ContentType contentType) {
        StaffView staffView = new StaffView();
        staffView.loadView(contentType);
    }

    public void loadPatronView(StageManager.ContentType contentType) {
        PatronView patronView = new PatronView();
        patronView.loadView(contentType);
    }

    public enum ContentType {
        ADMIN_DASHBOARD,
        ADMIN_DISCOVER,
        ADMIN_CONTROL_CENTER,
        STAFF_DASHBOARD,
        STAFF_DISCOVER,
        STAFF_DOCUMENTS_MANAGEMENT,
        PATRON_DASHBOARD,
        PATRON_SHOW_BORROWED,
        PATRON_DISCOVER,
        PATRON_ACCOUNT_CENTER
    }
}