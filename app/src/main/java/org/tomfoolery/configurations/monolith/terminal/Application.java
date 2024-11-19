package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.ConsoleIOHandler;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.Views;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.UpdateStaffCredentialsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.rating.AddDocumentRatingActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.rating.RemoveDocumentRatingActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search.SearchDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
// import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
// import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.auth.abc.ModifiableUser;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
// import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.ApacheHttpClientDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security.JJWTAuthenticationTokenGenerator;
// import org.tomfoolery.infrastructures.dataproviders.generators.qrgen.documents.references.QrgenDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
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

    private final @NonNull IOHandler ioHandler = ConsoleIOHandler.of();

    private final @NonNull Views views = Views.of(
        GuestSelectionView.of(ioHandler),

        AdministratorSelectionView.of(ioHandler),
        PatronSelectionView.of(ioHandler),
        StaffSelectionView.of(ioHandler),

        // Guest action views
        CreatePatronAccountActionView.of(ioHandler, patronRepository, passwordEncoder),
        LogUserInActionView.of(ioHandler, userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        // Shared user action views
        LogUserOutActionView.of(ioHandler, userRepositories, authenticationTokenGenerator, authenticationTokenRepository),

        GetDocumentByIdActionView.of(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        SearchDocumentsActionView.of(ioHandler, documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        ShowDocumentsActionView.of(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentRecommendationActionView.of(ioHandler, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository),

        // Administrator action views
        CreateStaffAccountActionView.of(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        DeleteStaffAccountActionView.of(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateStaffCredentialsActionView.of(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        // Patron action views
        DeletePatronAccountActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        UpdatePatronMetadataActionView.of(ioHandler, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdatePatronPasswordActionView.of(ioHandler, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        BorrowDocumentActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReadBorrowedDocumentActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReturnDocumentActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowBorrowedDocumentsActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),

        AddDocumentRatingActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),

        // Staff action views
        AddDocumentActionView.of(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentMetadataActionView.of(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository)
    );

    @Override
    public void run() {
        // Decoy
        this.patronRepository.save(Patron.of(
            BaseUser.Id.of(UUID.randomUUID()),
            BaseUser.Credentials.of("admin_123", this.passwordEncoder.encodePassword(SecureString.of("Root_123"))),
            Patron.Audit.of(false, ModifiableUser.Audit.Timestamps.of(Instant.EPOCH)),
            Patron.Metadata.of("", "", "")
        ));

        Class<? extends BaseView> viewClass = GuestSelectionView.class;
        BaseView view;

        do {
            view = this.views.getViewByClass(viewClass);
            assert view != null;   // Expected

            view.run();

            viewClass = view.getNextViewClass();
        } while (viewClass != null);
    }

    @Override
    @SneakyThrows
    public void close() {
        for (val field : this.getClass().getDeclaredFields()) {
            val resource = field.get(this);

            if (resource instanceof AutoCloseable autoCloseableResource)
                autoCloseableResource.close();
        }
    }

    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
