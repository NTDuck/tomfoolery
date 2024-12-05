package org.tomfoolery.configurations.monolith.console.dataproviders.contexts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.contexts.abc.ApplicationContext;
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
import org.tomfoolery.core.domain.users.abc.BaseUser;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CloudApplicationContext extends ApplicationContext {
    @Override
    protected @NonNull DocumentRepository getDocumentRepository() {
        return null;
    }

    @Override
    protected @NonNull AdministratorRepository getAdministratorRepository() {
        return null;
    }

    @Override
    protected @NonNull PatronRepository getPatronRepository() {
        return null;
    }

    @Override
    protected @NonNull StaffRepository getStaffRepository() {
        return null;
    }

    @Override
    protected @NonNull DocumentContentRepository getDocumentContentRepository() {
        return null;
    }

    @Override
    protected @NonNull BorrowingSessionRepository getBorrowingSessionRepository() {
        return null;
    }

    @Override
    protected @NonNull ReviewRepository getReviewRepository() {
        return null;
    }

    @Override
    protected @NonNull DocumentSearchGenerator getDocumentSearchGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator getDocumentRecommendationGenerator() {
        return null;
    }

    @Override
    protected @NonNull <User extends BaseUser> UserSearchGenerator<User> getUserSearchGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator getDocumentQrCodeGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentUrlGenerator getDocumentUrlGenerator() {
        return null;
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator() {
        return null;
    }

    @Override
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenRepository() {
        return null;
    }

    @Override
    protected @NonNull PasswordEncoder getPasswordEncoder() {
        return null;
    }
}
