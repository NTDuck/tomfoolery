package org.tomfoolery.configurations.monolith.console.dataproviders.contexts;

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
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

@NoArgsConstructor(staticName = "of")
public final class InMemoryApplicationContext extends ApplicationContext {
    private final @NonNull HttpClientProvider httpClientProvider = OkHttpClientProvider.of();

    @Override
    protected @NonNull DocumentRepository getDocumentRepository() {
        return InMemoryDocumentRepository.of();
    }

    @Override
    protected @NonNull AdministratorRepository getAdministratorRepository() {
        return InMemoryAdministratorRepository.of();
    }

    @Override
    protected @NonNull PatronRepository getPatronRepository() {
        return InMemoryPatronRepository.of();
    }

    @Override
    protected @NonNull StaffRepository getStaffRepository() {
        return InMemoryStaffRepository.of();
    }

    @Override
    protected @NonNull DocumentContentRepository getDocumentContentRepository() {
        return InMemoryDocumentContentRepository.of();
    }

    @Override
    protected @NonNull BorrowingSessionRepository getBorrowingSessionRepository() {
        return InMemoryBorrowingSessionRepository.of();
    }

    @Override
    protected @NonNull ReviewRepository getReviewRepository() {
        return InMemoryReviewRepository.of();
    }

    @Override
    protected @NonNull DocumentSearchGenerator getDocumentSearchGenerator() {
        return InMemoryIndexedDocumentSearchGenerator.of();
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator getDocumentRecommendationGenerator() {
        return InMemoryIndexedDocumentRecommendationGenerator.of();
    }

    @Override
    protected @NonNull <User extends BaseUser> UserSearchGenerator<User> getUserSearchGenerator() {
        return InMemoryUserSearchGenerator.of();
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator getDocumentQrCodeGenerator() {
        return ZxingDocumentQrCodeGenerator.of();
    }

    @Override
    protected @NonNull DocumentUrlGenerator getDocumentUrlGenerator() {
        return ApacheHttpClientDocumentUrlGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }

    @Override
    protected @NonNull PasswordEncoder getPasswordEncoder() {
        return BCryptPasswordEncoder.of();
    }
}
