package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.view.admin.AdminView;
import org.tomfoolery.configurations.monolith.gui.view.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.SignupView;
import org.tomfoolery.configurations.monolith.gui.view.patron.PatronView;
import org.tomfoolery.configurations.monolith.gui.view.staff.StaffView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.auth.abc.ModifiableUser;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// Using singleton for StageManager
@Getter
public class StageManager {
    private static final double LOGIN_MENU_WIDTH = 600;
    private static final double LOGIN_MENU_HEIGHT = 800;
    private static final double MAIN_STAGE_WIDTH = 1280;
    private static final double MAIN_STAGE_HEIGHT = 720;

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

    // Initialize resources
    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();

    private final @NonNull DocumentSearchGenerator documentSearchGenerator = InMemoryIndexedDocumentSearchGenerator.of();
    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryIndexedDocumentRecommendationGenerator.of();

    // private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = QrgenDocumentQrCodeGenerator.of();
    // private final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheHttpClientDocumentUrlGenerator.of();

    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();
    private final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    public void setAuthStageProperties() {
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);

        primaryStage.setResizable(false);

        primaryStage.setWidth(LOGIN_MENU_WIDTH);
        primaryStage.setHeight(LOGIN_MENU_HEIGHT);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void setMainStageProperties() {
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
                    authenticationTokenGenerator,
                    authenticationTokenRepository,
                    passwordEncoder
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
            SignupView signUpController = new SignupView(patronRepository, passwordEncoder);

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

    public enum ContentType{
        ADMIN_DASHBOARD,
        ADMIN_DISCOVER,
        ADMIN_CONTROL_CENTER,
        STAFF_DASHBOARD,
        STAFF_DISCOVER,
        STAFF_DOCUMENTS_MANAGEMENT,
        PATRON_DASHBOARD,
        PATRON_DISCOVER
    }

    public void populateUserRepositories() {
        this.administratorRepository.save(Administrator.of(
                BaseUser.Id.of(UUID.randomUUID()),
                BaseUser.Credentials.of("admin_123", this.passwordEncoder.encodePassword(SecureString.of("Root_123"))),
                BaseUser.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH))
        ));

        this.patronRepository.save(Patron.of(
                BaseUser.Id.of(UUID.randomUUID()),
                BaseUser.Credentials.of("patron_123", this.passwordEncoder.encodePassword(SecureString.of("Root_123"))),
                Patron.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH)),
                Patron.Metadata.of("", "", "")
        ));

        this.staffRepository.save(Staff.of(
                BaseUser.Id.of(UUID.randomUUID()),
                BaseUser.Credentials.of("staff_123", this.passwordEncoder.encodePassword(SecureString.of("Root_123"))),
                Staff.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH), Administrator.Id.of(UUID.randomUUID()))
        ));
    }
}