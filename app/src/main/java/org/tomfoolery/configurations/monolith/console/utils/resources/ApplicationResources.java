package org.tomfoolery.configurations.monolith.console.utils.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.ConsoleIOProvider;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.containers.Views;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.UpdateStaffCredentialsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval.*;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchAdministratorsByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchPatronsByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchStaffByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.search.SearchDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.users.authentication.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByAuthenticationTokenActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByCredentialsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.persistence.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.GetDocumentBorrowStatusActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.review.persistence.AddDocumentReviewActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.review.persistence.RemoveDocumentRatingActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.retrieval.GetPatronUsernameAndMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentContentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentCoverImageActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
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
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.ApacheHttpClientDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.users.authentication.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.InMemoryUserSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.okhttp.OkHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents.SynchronizedDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

import java.util.List;
import java.util.Set;

public class ApplicationResources implements AutoCloseable {
    // Providers
    protected final @NonNull IOProvider ioProvider = ConsoleIOProvider.of();
    protected final @NonNull HttpClientProvider httpClientProvider = OkHttpClientProvider.of();

    // Synchronized Generators
    protected final @NonNull DocumentSearchGenerator documentSearchGenerator = InMemoryIndexedDocumentSearchGenerator.of();
    protected final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryIndexedDocumentRecommendationGenerator.of();

    protected final @NonNull UserSearchGenerator<Administrator> administratorSearchGenerator = InMemoryUserSearchGenerator.of();
    protected final @NonNull UserSearchGenerator<Patron> patronSearchGenerator = InMemoryUserSearchGenerator.of();
    protected final @NonNull UserSearchGenerator<Staff> staffSearchGenerator = InMemoryUserSearchGenerator.of();

    // Relation Repositories
    protected final @NonNull DocumentContentRepository documentContentRepository = InMemoryDocumentContentRepository.of();
    protected final @NonNull ReviewRepository reviewRepository = InMemoryReviewRepository.of();
    protected final @NonNull BorrowingSessionRepository borrowingSessionRepository = InMemoryBorrowingSessionRepository.of();

    // Repositories
    protected final @NonNull DocumentRepository documentRepository = HybridDocumentRepository.of(
        List.of(
            SynchronizedDocumentRepository.of(
//                InMemoryDocumentRepository.of(),
                CloudDocumentRepository.of(),
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

    protected final @NonNull AdministratorRepository administratorRepository = SynchronizedAdministratorRepository.of(
        InMemoryAdministratorRepository.of(),
        List.of(administratorSearchGenerator),
        borrowingSessionRepository,
        reviewRepository
    );
    protected final @NonNull PatronRepository patronRepository = SynchronizedPatronRepository.of(
        InMemoryPatronRepository.of(),
        List.of(patronSearchGenerator),
        borrowingSessionRepository,
        reviewRepository
    );
    protected final @NonNull StaffRepository staffRepository = SynchronizedStaffRepository.of(
        InMemoryStaffRepository.of(),
        List.of(staffSearchGenerator),
        borrowingSessionRepository,
        reviewRepository
    );

    protected final @NonNull UserRepositories userRepositories = UserRepositories.of(Set.of(
        administratorRepository, patronRepository, staffRepository
    ));

    // Others
    protected final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = ZxingDocumentQrCodeGenerator.of();
    protected final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheHttpClientDocumentUrlGenerator.of();

    protected final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    protected final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();

    protected final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    @Getter
    protected final @NonNull Views views = Views.of(
        GuestSelectionView.of(ioProvider),

        AdministratorSelectionView.of(ioProvider),
        PatronSelectionView.of(ioProvider),
        StaffSelectionView.of(ioProvider),

        // Guest action views
        CreatePatronAccountActionView.of(ioProvider, patronRepository, passwordEncoder),
        LogUserInByCredentialsActionView.of(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        LogUserInByAuthenticationTokenActionView.of(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository),

        // Shared user action views
        LogUserOutActionView.of(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository),

        GetDocumentByIdActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        SearchDocumentsActionView.of(ioProvider, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentQrCodeActionView.of(ioProvider, documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        ShowDocumentsActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentRecommendationActionView.of(ioProvider, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository),

        // Administrator action views
        CreateStaffAccountActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        DeleteStaffAccountActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateStaffCredentialsActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        GetAdministratorByIdActionView.of(ioProvider, administratorRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetPatronByIdActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetStaffByIdActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowAdministratorAccountsActionView.of(ioProvider, administratorRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowPatronAccountsActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowStaffAccountsActionView.of(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository),
        SearchAdministratorsByUsernameActionView.of(ioProvider, administratorSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        SearchPatronsByUsernameActionView.of(ioProvider, patronSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),
        SearchStaffByUsernameActionView.of(ioProvider, staffSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository),

        // Patron action views
        GetPatronUsernameAndMetadataActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        DeletePatronAccountActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),
        UpdatePatronMetadataActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdatePatronPasswordActionView.of(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder),

        BorrowDocumentActionView.of(ioProvider, documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReadBorrowedDocumentActionView.of(ioProvider, documentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ReturnDocumentActionView.of(ioProvider, documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository),
        ShowBorrowedDocumentsActionView.of(ioProvider, documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository),
        GetDocumentBorrowStatusActionView.of(ioProvider, documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository),

        AddDocumentReviewActionView.of(ioProvider, documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioProvider, documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository),

        // Staff action views
        AddDocumentActionView.of(ioProvider, documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        RemoveDocumentRatingActionView.of(ioProvider, documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentContentActionView.of(ioProvider, documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentCoverImageActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository),
        UpdateDocumentMetadataActionView.of(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository)
    );

    public static @NonNull ApplicationResources of() {
        return new ApplicationResources();
    }

    protected ApplicationResources() {}

    @Override
    public void close() throws Exception {
        for (val field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            val resource = field.get(this);

            if (resource instanceof AutoCloseable autoCloseableResource)
                autoCloseableResource.close();
        }
    }
}
