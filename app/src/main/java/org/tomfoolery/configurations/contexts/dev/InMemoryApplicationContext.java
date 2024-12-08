package org.tomfoolery.configurations.contexts.dev;

import lombok.NoArgsConstructor;
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
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.okhttp.OkHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

import java.util.List;

@NoArgsConstructor
public class InMemoryApplicationContext extends ApplicationContext {
    @Override
    protected @NonNull DocumentRepository createDocumentRepository() {
        return InMemoryDocumentRepository.of();
    }

    @Override
    protected @NonNull List<DocumentRepository> createRetrievalDocumentRepositories() {
        return List.of(
            GoogleApiDocumentRepository.of(this.getHttpClientProvider())
        );
    }

    @Override
    protected @NonNull AdministratorRepository createAdministratorRepository() {
        return InMemoryAdministratorRepository.of();
    }

    @Override
    protected @NonNull PatronRepository createPatronRepository() {
        return InMemoryPatronRepository.of();
    }

    @Override
    protected @NonNull StaffRepository createStaffRepository() {
        return InMemoryStaffRepository.of();
    }

    @Override
    protected @NonNull DocumentContentRepository createDocumentContentRepository() {
        return InMemoryDocumentContentRepository.of();
    }

    @Override
    protected @NonNull BorrowingSessionRepository createBorrowingSessionRepository() {
        return InMemoryBorrowingSessionRepository.of();
    }

    @Override
    protected @NonNull ReviewRepository createReviewRepository() {
        return InMemoryReviewRepository.of();
    }

    @Override
    protected @NonNull DocumentSearchGenerator createDocumentSearchGenerator() {
        return InMemoryIndexedDocumentSearchGenerator.of();
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator createDocumentRecommendationGenerator() {
        return InMemoryIndexedDocumentRecommendationGenerator.of();
    }

    @Override
    protected @NonNull <User extends BaseUser> UserSearchGenerator<User> createUserSearchGenerator() {
        return InMemoryUserSearchGenerator.of();
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator createDocumentQrCodeGenerator() {
        return ZxingDocumentQrCodeGenerator.of();
    }

    @Override
    protected @NonNull DocumentUrlGenerator createDocumentUrlGenerator() {
        return ApacheHttpClientDocumentUrlGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator createAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenRepository createAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of(this.getDotenvProvider());
    }

    @Override
    protected @NonNull PasswordEncoder createPasswordEncoder() {
        return BCryptPasswordEncoder.of();
    }

    @Override
    protected @NonNull DotenvProvider createDotenvProvider() {
        return CdimascioDotenvProvider.of();
    }

    @Override
    protected @NonNull HttpClientProvider createHttpClientProvider() {
        return OkHttpClientProvider.of();
    }
}
