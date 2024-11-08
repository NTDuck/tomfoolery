package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.view.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.Patron.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.SignupView;
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

public class StageManager {
    private static final double LOGIN_MENU_WIDTH = 600;
    private static final double LOGIN_MENU_HEIGHT = 800;
    private static final double MAIN_STAGE_WIDTH = 1600;
    private static final double MAIN_STAGE_HEIGHT = 900;

    @Setter
    private @Getter Stage primaryStage;

    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();

    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();
    private final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository,
            staffRepository, patronRepository);

    private final @NonNull AuthenticationTokenService authenticationTokenService = Base64AuthenticationTokenService
            .of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository
            .of();
    private final @NonNull PasswordService passwordService = Base64PasswordService.of();

    public StageManager(Stage stage) {
        primaryStage = stage;
    }

    private void setSize(double width, double height) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }

    private void setIcon(String path) {
        Image icon = new Image(StageManager.class.getResourceAsStream(path));
        primaryStage.getIcons().add(icon);
    }

    public void openLoginMenu() {
        try {
            LoginView controller = new LoginView(userRepositories, passwordService, authenticationTokenService,
                    authenticationTokenRepository, this);
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/LoginMenu.fxml"));
            loader.setController(controller);
            VBox root = loader.load();

            Scene scene = new Scene(root);
            setSize(LOGIN_MENU_WIDTH, LOGIN_MENU_HEIGHT);
            setIcon("/images/logo.png");
            primaryStage.setResizable(false);
            primaryStage.setTitle("Tomfoolery - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openMenu(String fxmlPath, String menuType) {
        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));

            switch (menuType) {
                case "Dashboard":
                    DashboardView dashboardController = new DashboardView(this);
                    loader.setController(dashboardController);
                    break;
                case "Discover":
                    System.out.println("discover");
                    break;
                default:
                    System.out.println("wtf");
            }

            HBox root = loader.load();

            setSize(MAIN_STAGE_WIDTH, MAIN_STAGE_HEIGHT);
            setIcon("/images/logo.png");
            primaryStage.setTitle("Tomfoolery - Library Management App");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openSignupMenu() {
        try {
            SignupView signUpController = new SignupView(patronRepository, passwordService, this);
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/SignupMenu.fxml"));
            loader.setController(signUpController);
            VBox root = loader.load();

            Scene scene = new Scene(root);

            setSize(LOGIN_MENU_WIDTH, LOGIN_MENU_HEIGHT);
            setIcon("/images/logo.png");
            primaryStage.setResizable(false);
            primaryStage.setTitle("Tomfoolery - Sign up");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}