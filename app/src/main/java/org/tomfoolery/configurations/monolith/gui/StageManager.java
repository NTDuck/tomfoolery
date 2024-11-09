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
import org.tomfoolery.configurations.monolith.gui.view.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.Patron.DashboardView;
import org.tomfoolery.configurations.monolith.gui.view.Patron.DiscoverView;
import org.tomfoolery.configurations.monolith.gui.view.SignupView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.document.references.ApacheDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.qrgen.documents.references.QrgenDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.recommendation.InMemoryDocumentRecommendationRepository;

import java.io.IOException;
import java.util.Objects;

// Using singleton for StageManager
public class StageManager {
    private static final double LOGIN_MENU_WIDTH = 600;
    private static final double LOGIN_MENU_HEIGHT = 800;
    private static final double MAIN_STAGE_WIDTH = 1280;
    private static final double MAIN_STAGE_HEIGHT = 720;

    private static StageManager instance;

    @Setter
    private @Getter Stage primaryStage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();

    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryDocumentRecommendationGenerator.of(documentRepository);
    private final @NonNull DocumentRecommendationRepository documentRecommendationRepository = InMemoryDocumentRecommendationRepository.of();

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = QrgenDocumentQrCodeGenerator.of();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheDocumentUrlGenerator.of();

    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();
    private final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    public StageManager(Stage stage) {
        primaryStage = stage;
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

    private void setAuthStageProperties() {
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);
        primaryStage.setResizable(false);
        primaryStage.setWidth(LOGIN_MENU_WIDTH);
        primaryStage.setHeight(LOGIN_MENU_HEIGHT);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void openLoginMenu() {
        try {
            LoginView controller = new LoginView(userRepositories, passwordEncoder, authenticationTokenGenerator,
                    authenticationTokenRepository, this);
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
            SignupView signUpController = new SignupView(patronRepository, passwordEncoder, this);
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

    public void openMenu(String fxmlPath, String menuType) {
        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));

            switch (menuType) {
                case "Dashboard":
                    DashboardView dashboardController = new DashboardView(this);
                    loader.setController(dashboardController);
                    break;
                case "Discover":
                    DiscoverView discoverView = new DiscoverView(this);
                    loader.setController(discoverView);
                    break;
                default:
                    System.out.println("wtf");
            }

            HBox root = loader.load();

            setMainStageProperties();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}