package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.view.LoginView;
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
    private static @Getter Stage primaryStage;

    private static final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private static final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();
    private static final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();

    private static final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();
    private static final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);

    private static final @NonNull AuthenticationTokenService authenticationTokenService = Base64AuthenticationTokenService.of();
    private static final @NonNull AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository.of();
    private static final @NonNull PasswordService passwordService = Base64PasswordService.of();

    private StageManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        StageManager.primaryStage = stage;
    }

    public static void openLoginMenu() {
        try {
            LoginView controller = new LoginView(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository);
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/LoginMenu.fxml"));
            loader.setController(controller);
            VBox root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setHeight(800);
            primaryStage.setWidth(600);
            primaryStage.setResizable(false);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openMainMenu(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource(fxmlPath));

            primaryStage.setMinHeight(720);
            primaryStage.setMinWidth(1280);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Library Management App");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openSignupMenu() {
        try {
            SignupView signUpController = new SignupView(patronRepository, passwordService);
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/SignupMenu.fxml"));
            loader.setController(signUpController);
            VBox root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setHeight(800);
            primaryStage.setWidth(600);
            primaryStage.setResizable(false);
            Image icon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Tomfoolery - Sign up");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}