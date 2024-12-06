package org.tomfoolery.infrastructures.contexts.abc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
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
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents.SynchronizedDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedStaffRepository;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Closeable;

import java.util.List;
import java.util.Set;

@Getter
public abstract class ApplicationContext implements Closeable {
    private final @NonNull HttpClientProvider httpClientProvider = this.createHttpClientProvider();

    private final @NonNull DocumentRepository documentRepository = this.createDocumentRepository();

    private final @NonNull DocumentContentRepository documentContentRepository = this.createDocumentContentRepository();
    private final @NonNull BorrowingSessionRepository borrowingSessionRepository = this.createBorrowingSessionRepository();
    private final @NonNull ReviewRepository reviewRepository = this.createReviewRepository();

    private final @NonNull DocumentSearchGenerator documentSearchGenerator = this.createDocumentSearchGenerator();
    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = this.createDocumentRecommendationGenerator();

    private final @NonNull UserSearchGenerator<Administrator> administratorSearchGenerator = this.createUserSearchGenerator();
    private final @NonNull UserSearchGenerator<Patron> patronSearchGenerator = this.createUserSearchGenerator();
    private final @NonNull UserSearchGenerator<Staff> staffSearchGenerator = this.createUserSearchGenerator();

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = this.createDocumentQrCodeGenerator();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = this.createDocumentUrlGenerator();

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = this.createAuthenticationTokenGenerator();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = this.createAuthenticationTokenRepository();
    private final @NonNull PasswordEncoder passwordEncoder = this.createPasswordEncoder();

    private final @NonNull DocumentRepository hybridDocumentRepository = HybridDocumentRepository.of(
        List.of(SynchronizedDocumentRepository.of(
            this.documentRepository,
            List.of(this.documentSearchGenerator, this.documentRecommendationGenerator),
            this.documentContentRepository, this.borrowingSessionRepository, this.reviewRepository
        )), this.createRetrievalDocumentRepositories()
    );

    private final @NonNull AdministratorRepository administratorRepository = SynchronizedAdministratorRepository.of(
        this.createAdministratorRepository(),
        List.of(this.administratorSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    private final @NonNull PatronRepository patronRepository = SynchronizedPatronRepository.of(
        this.createPatronRepository(),
        List.of(this.patronSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    private final @NonNull StaffRepository staffRepository = SynchronizedStaffRepository.of(
        this.createStaffRepository(),
        List.of(this.staffSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(Set.of(
        this.administratorRepository, this.staffRepository, this.patronRepository
    ));

    protected abstract @NonNull DocumentRepository createDocumentRepository();

    protected @NonNull List<DocumentRepository> createRetrievalDocumentRepositories() {
        return List.of();
    }

    protected abstract @NonNull AdministratorRepository createAdministratorRepository();
    protected abstract @NonNull PatronRepository createPatronRepository();
    protected abstract @NonNull StaffRepository createStaffRepository();

    protected abstract @NonNull DocumentContentRepository createDocumentContentRepository();
    protected abstract @NonNull BorrowingSessionRepository createBorrowingSessionRepository();
    protected abstract @NonNull ReviewRepository createReviewRepository();

    protected abstract @NonNull DocumentSearchGenerator createDocumentSearchGenerator();
    protected abstract @NonNull DocumentRecommendationGenerator createDocumentRecommendationGenerator();
    protected abstract <User extends BaseUser> @NonNull UserSearchGenerator<User> createUserSearchGenerator();

    protected abstract @NonNull DocumentQrCodeGenerator createDocumentQrCodeGenerator();
    protected abstract @NonNull DocumentUrlGenerator createDocumentUrlGenerator();
    protected abstract @NonNull AuthenticationTokenGenerator createAuthenticationTokenGenerator();
    protected abstract @NonNull AuthenticationTokenRepository createAuthenticationTokenRepository();
    protected abstract @NonNull PasswordEncoder createPasswordEncoder();

    protected abstract @NonNull HttpClientProvider createHttpClientProvider();
}
