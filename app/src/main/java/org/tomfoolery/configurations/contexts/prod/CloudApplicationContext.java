package org.tomfoolery.configurations.contexts.prod;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
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
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

public class CloudApplicationContext extends ApplicationContext {
    @Override
    protected @NonNull DocumentRepository createDocumentRepository() {
        return null;
    }

    @Override
    protected @NonNull AdministratorRepository createAdministratorRepository() {
        return null;
    }

    @Override
    protected @NonNull PatronRepository createPatronRepository() {
        return null;
    }

    @Override
    protected @NonNull StaffRepository createStaffRepository() {
        return null;
    }

    @Override
    protected @NonNull DocumentContentRepository createDocumentContentRepository() {
        return null;
    }

    @Override
    protected @NonNull BorrowingSessionRepository createBorrowingSessionRepository() {
        return null;
    }

    @Override
    protected @NonNull ReviewRepository createReviewRepository() {
        return null;
    }

    @Override
    protected @NonNull DocumentSearchGenerator createDocumentSearchGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator createDocumentRecommendationGenerator() {
        return null;
    }

    @Override
    protected @NonNull <User extends BaseUser> UserSearchGenerator<User> createUserSearchGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator createDocumentQrCodeGenerator() {
        return null;
    }

    @Override
    protected @NonNull DocumentUrlGenerator createDocumentUrlGenerator() {
        return null;
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator createAuthenticationTokenGenerator() {
        return null;
    }

    @Override
    protected @NonNull AuthenticationTokenRepository createAuthenticationTokenRepository() {
        return null;
    }

    @Override
    protected @NonNull PasswordEncoder createPasswordEncoder() {
        return null;
    }

    @Override
    protected @NonNull HttpClientProvider createHttpClientProvider() {
        return null;
    }
}
