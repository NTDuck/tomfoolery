package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.utils.FxmlPathParser;
import org.tomfoolery.configurations.monolith.gui.view.Admin.AdminDashboardView;
import org.tomfoolery.configurations.monolith.gui.view.Admin.AdminDiscoverView;
import org.tomfoolery.configurations.monolith.gui.view.Admin.ControlCenterView;
import org.tomfoolery.configurations.monolith.gui.view.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.Patron.PatronDashboardView;
import org.tomfoolery.configurations.monolith.gui.view.Patron.PatronDiscoverView;
import org.tomfoolery.configurations.monolith.gui.view.SignupView;
import org.tomfoolery.configurations.monolith.gui.view.View;
import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64AuthenticationTokenService;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64PasswordService;
import org.tomfoolery.infrastructures.dataproviders.inmemory.*;

import java.io.IOException;
import java.util.Objects;

// Using singleton for StageManager
public class StageManager {
    private static final double LOGIN_MENU_WIDTH = 600;
    private static final double LOGIN_MENU_HEIGHT = 800;
    private static final double MAIN_STAGE_WIDTH = 1280;
    private static final double MAIN_STAGE_HEIGHT = 720;

    private static StageManager instance;

    @Setter @Getter
    private Stage primaryStage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    // Initialize resources
    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();

    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();

    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();

    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(
            administratorRepository,
            staffRepository,
            patronRepository
    );

    private final @NonNull AuthenticationTokenService authenticationTokenService = Base64AuthenticationTokenService.of();

    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository.of();

    private final @NonNull PasswordService passwordService = Base64PasswordService.of();

    private void setAuthStageProperties() {
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);

        primaryStage.setResizable(false);

        primaryStage.setWidth(LOGIN_MENU_WIDTH);
        primaryStage.setHeight(LOGIN_MENU_HEIGHT);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    private void setMainStageProperties() {
        primaryStage.setResizable(true);

        boolean isMaximized = primaryStage.isMaximized();
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        if (currentHeight < MAIN_STAGE_HEIGHT || currentWidth < MAIN_STAGE_WIDTH) {
            primaryStage.setWidth(MAIN_STAGE_WIDTH);
            primaryStage.setHeight(MAIN_STAGE_HEIGHT);
        } else {
            primaryStage.setMaximized(isMaximized);
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);
        }

        primaryStage.setMinHeight(MAIN_STAGE_HEIGHT);
        primaryStage.setMinWidth(MAIN_STAGE_WIDTH);

        primaryStage.setTitle("Tomfoolery - Library Management Application");
    }

    public void openLoginMenu() {
        try {
            LoginView controller = new LoginView(
                    userRepositories,
                    passwordService,
                    authenticationTokenService,
                    authenticationTokenRepository
            );

            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/LoginMenu.fxml"));
            loader.setController(controller);

            VBox root = loader.load();

            Scene scene = new Scene(root);

            setAuthStageProperties();
            primaryStage.setTitle("Tomfoolery - Login");
            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openSignupMenu() {
        try {
            SignupView signUpController = new SignupView(patronRepository, passwordService);

            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/SignupMenu.fxml"));
            loader.setController(signUpController);

            VBox root = loader.load();

            Scene scene = new Scene(root);

            setAuthStageProperties();
            primaryStage.setTitle("Tomfoolery - Sign up");
            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openMenu(String fxmlPath) {
        String userType = FxmlPathParser.getSceneInfo(fxmlPath).getUserType();
        String sceneType = FxmlPathParser.getSceneInfo(fxmlPath).getSceneType();

        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));

            View view = loadView(userType, sceneType);
            loader.setController(view);

            HBox root = loader.load();

            setMainStageProperties();
            primaryStage.setScene(new Scene(root));

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private View loadView(String userType, String sceneType) {
        View view = new View();

        switch (userType) {
            case "Admin":
                view = switch (sceneType) {
                    case "Dashboard" -> new AdminDashboardView();
                    case "Discover" -> new AdminDiscoverView();
                    case "ControlCenter" -> new ControlCenterView();
                    default -> view;
                };
                break;
            case "Patron":
                view = switch (sceneType) {
                    case "Dashboard" -> new PatronDashboardView();
                    case "Discover" -> new PatronDiscoverView();
                    default -> view;
                };
                break;
            case "Staff":
                System.out.println("staff views not declared");
                break;
        }

        return view;
    }
}