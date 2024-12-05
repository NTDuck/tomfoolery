package org.tomfoolery.configurations.monolith.console.dataproviders.contexts.abc;

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
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents.SynchronizedDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedStaffRepository;

import java.util.List;
import java.util.Set;

@Getter
public abstract class ApplicationContext {
    private final @NonNull DocumentRepository documentRepository = this.getDocumentRepository();

    private final @NonNull DocumentRepository hybridDocumentRepository = HybridDocumentRepository.of(
        List.of(SynchronizedDocumentRepository.of(
            this.documentRepository,
            List.of(this.documentSearchGenerator, this.documentRecommendationGenerator),
            this.documentContentRepository, this.borrowingSessionRepository, this.reviewRepository
        )), this.getRetrievalDocumentRepositories()
    );

    @Setter(AccessLevel.NONE)
    private final @NonNull AdministratorRepository administratorRepository = SynchronizedAdministratorRepository.of(
        this.getAdministratorRepository(),
        List.of(this.administratorSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    @Setter(AccessLevel.NONE)
    private final @NonNull PatronRepository patronRepository = SynchronizedPatronRepository.of(
        this.getPatronRepository(),
        List.of(this.patronSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    @Setter(AccessLevel.NONE)
    private final @NonNull StaffRepository staffRepository = SynchronizedStaffRepository.of(
        this.getStaffRepository(),
        List.of(this.staffSearchGenerator), this.borrowingSessionRepository, this.reviewRepository
    );

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(Set.of(
        this.administratorRepository, this.staffRepository, this.patronRepository
    ));

    private final @NonNull DocumentContentRepository documentContentRepository = this.getDocumentContentRepository();
    private final @NonNull BorrowingSessionRepository borrowingSessionRepository = this.getBorrowingSessionRepository();
    private final @NonNull ReviewRepository reviewRepository = this.getReviewRepository();

    private final @NonNull DocumentSearchGenerator documentSearchGenerator = this.getDocumentSearchGenerator();
    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = this.getDocumentRecommendationGenerator();

    private final @NonNull UserSearchGenerator<Administrator> administratorSearchGenerator = this.getUserSearchGenerator();
    private final @NonNull UserSearchGenerator<Patron> patronSearchGenerator = this.getUserSearchGenerator();
    private final @NonNull UserSearchGenerator<Staff> staffSearchGenerator = this.getUserSearchGenerator();

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = this.getDocumentQrCodeGenerator();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = this.getDocumentUrlGenerator();

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = this.getAuthenticationTokenGenerator();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = this.getAuthenticationTokenRepository();
    private final @NonNull PasswordEncoder passwordEncoder = this.getPasswordEncoder();

    protected abstract @NonNull DocumentRepository getDocumentRepository();

    protected @NonNull List<DocumentRepository> getRetrievalDocumentRepositories() {
        return List.of();
    }

    protected abstract @NonNull AdministratorRepository getAdministratorRepository();
    protected abstract @NonNull PatronRepository getPatronRepository();
    protected abstract @NonNull StaffRepository getStaffRepository();

    protected abstract @NonNull DocumentContentRepository getDocumentContentRepository();
    protected abstract @NonNull BorrowingSessionRepository getBorrowingSessionRepository();
    protected abstract @NonNull ReviewRepository getReviewRepository();

    protected abstract @NonNull DocumentSearchGenerator getDocumentSearchGenerator();
    protected abstract @NonNull DocumentRecommendationGenerator getDocumentRecommendationGenerator();
    protected abstract <User extends BaseUser> @NonNull UserSearchGenerator<User> getUserSearchGenerator();

    protected abstract @NonNull DocumentQrCodeGenerator getDocumentQrCodeGenerator();
    protected abstract @NonNull DocumentUrlGenerator getDocumentUrlGenerator();
    protected abstract @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator();
    protected abstract @NonNull AuthenticationTokenRepository getAuthenticationTokenRepository();
    protected abstract @NonNull PasswordEncoder getPasswordEncoder();
}
