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
import org.tomfoolery.configurations.monolith.gui.view.admin.layout.AdminView;
import org.tomfoolery.configurations.monolith.gui.view.user.auth.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.guest.SignupView;
import org.tomfoolery.configurations.monolith.gui.view.patron.layout.PatronView;
import org.tomfoolery.configurations.monolith.gui.view.staff.layout.StaffView;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.users.abc.ModifiableUser;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.ApacheHttpClientDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.users.authentication.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryLinearDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.InMemoryUserSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.builtin.BuiltinHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents.SynchronizedDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.SecretStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    private final @NonNull HttpClientProvider httpClientProvider = BuiltinHttpClientProvider.of();

    // Synchronized Generators
    private final @NonNull DocumentSearchGenerator documentSearchGenerator = InMemoryIndexedDocumentSearchGenerator.of();
    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryIndexedDocumentRecommendationGenerator.of();

    private final @NonNull UserSearchGenerator<Administrator> administratorSearchGenerator = InMemoryUserSearchGenerator.of();
    private final @NonNull UserSearchGenerator<Patron> patronSearchGenerator = InMemoryUserSearchGenerator.of();
    private final @NonNull UserSearchGenerator<Staff> staffSearchGenerator = InMemoryUserSearchGenerator.of();

    // Relation Repositories
    private final @NonNull DocumentContentRepository documentContentRepository = InMemoryDocumentContentRepository.of();
    private final @NonNull ReviewRepository reviewRepository = InMemoryReviewRepository.of();
    private final @NonNull BorrowingSessionRepository borrowingSessionRepository = InMemoryBorrowingSessionRepository.of();

    // Repositories
    private final @NonNull DocumentRepository documentRepository = HybridDocumentRepository.of(
            List.of(
                    SynchronizedDocumentRepository.of(
                            InMemoryDocumentRepository.of(),
                            List.of(documentSearchGenerator, documentRecommendationGenerator),
                            documentContentRepository,
                            borrowingSessionRepository,
                            reviewRepository
                    )
            ),
            List.of(
                    GoogleApiDocumentRepository.of(httpClientProvider)
            )
    );

    private final @NonNull AdministratorRepository administratorRepository = SynchronizedAdministratorRepository.of(
            InMemoryAdministratorRepository.of(),
            List.of(administratorSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );
    private final @NonNull PatronRepository patronRepository = SynchronizedPatronRepository.of(
            InMemoryPatronRepository.of(),
            List.of(patronSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );
    private final @NonNull StaffRepository staffRepository = SynchronizedStaffRepository.of(
            InMemoryStaffRepository.of(),
            List.of(staffSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(Set.of(
            administratorRepository, patronRepository, staffRepository
    ));

    // Others
    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = ZxingDocumentQrCodeGenerator.of();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheHttpClientDocumentUrlGenerator.of();

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = SecretStoreAuthenticationTokenRepository.of();

    private final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

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
                    userRepositories,
                    authenticationTokenGenerator,
                    authenticationTokenRepository,
                    passwordEncoder
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
            SignupView signUpController = new SignupView(patronRepository, passwordEncoder);

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

    public void populateUserRepositories() {
        this.administratorRepository.save(Administrator.of(
                BaseUser.Id.of(UUID.randomUUID()),
                BaseUser.Audit.of(BaseUser.Audit.Timestamps.of(Instant.EPOCH)),
                BaseUser.Credentials.of("admin123", this.passwordEncoder.encode(SecureString.of("Root_123")))
        ));

        this.patronRepository.save(Patron.of(
                BaseUser.Id.of(UUID.randomUUID()),
                Patron.Audit.of(Patron.Audit.Timestamps.of(Instant.EPOCH)),
                BaseUser.Credentials.of("patron123", this.passwordEncoder.encode(SecureString.of("Root_123"))),
                Patron.Metadata.of(Patron.Metadata.Name.of("Duy", "Nguyen"), LocalDate.of(2005, 12, 14), "0984728322", Patron.Metadata.Address.of("new york", "vietnam"), "anhduyxh7@gmail.com")
        ));

        this.staffRepository.save(Staff.of(
                BaseUser.Id.of(UUID.randomUUID()),
                Staff.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH), BaseUser.Id.of(UUID.randomUUID())),
                BaseUser.Credentials.of("staff123", this.passwordEncoder.encode(SecureString.of("Root_123")))
        ));
    }
}