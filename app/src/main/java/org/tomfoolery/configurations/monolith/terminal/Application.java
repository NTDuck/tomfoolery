package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.ConsoleIOProvider;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
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
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentContentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search.SearchDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.auth.abc.ModifiableUser;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.ApacheHttpClientDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.builtin.BuiltinHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull IOProvider ioProvider = ConsoleIOProvider.of();
    private final @NonNull HttpClientProvider httpClientProvider = BuiltinHttpClientProvider.of();

    private final @NonNull DocumentRepository documentRepository = HybridDocumentRepository.of(
        InMemoryDocumentRepository.of(),
        List.of(
            GoogleApiDocumentRepository.of(httpClientProvider)
        )
    );

    private final @NonNull DocumentSearchGenerator documentSearchGenerator = InMemoryIndexedDocumentSearchGenerator.of();
    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryIndexedDocumentRecommendationGenerator.of();

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = ZxingDocumentQrCodeGenerator.of();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheHttpClientDocumentUrlGenerator.of();

    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();
    private final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    private final @NonNull Views views = Views.of(
        GuestSelectionView.of(ioProvider),

        AdministratorSelectionView.of(ioProvider),
        PatronSelectionView.of(ioProvider),
        StaffSelectionView.of(ioProvider),

        // Guest action views
        CreatePatronAccountActionView.of(ioProvider, patronRepository, passwordEncoder),
        LogUserInActionView.of(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        // Shared user action views
        LogUserOutActionView.of(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository),

        GetDocumentByIdActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        SearchDocumentsActionView.of(ioProvider, documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentQrCodeActionView.of(ioProvider, documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        ShowDocumentsActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentRecommendationActionView.of(ioProvider, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository),

        // Administrator action views
        CreateStaffAccountActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        DeleteStaffAccountActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateStaffCredentialsActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        // Patron action views
        DeletePatronAccountActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        UpdatePatronMetadataActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdatePatronPasswordActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        BorrowDocumentActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReadBorrowedDocumentActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReturnDocumentActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowBorrowedDocumentsActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),

        AddDocumentRatingActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),

        // Staff action views
        AddDocumentActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentContentActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentMetadataActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository)
    );

    @Override
    public void run() {
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

    private void populate() {
        this.populateUserRepositories();
    }

    private void populateUserRepositories() {
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

    public static void main(String[] args) {
        val application = Application.of();

        application.populate();

        application.run();
        application.close();
    }
}
