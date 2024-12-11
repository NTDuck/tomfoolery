package org.tomfoolery.configurations.contexts.prod;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.CustomLandingPageDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.users.authentication.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.okhttp.OkHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika.ApacheTikaTemporaryFileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators.*;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika.ApacheTikaFileVerifier;

@NoArgsConstructor(staticName = "of")
public class CloudApplicationContext extends ApplicationContext {
    private static @NonNull CloudDatabaseConfigurationsProvider createCloudDatabaseConfigurationsProvider() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        return CloudDatabaseConfigurationsProvider.of(dotenvProvider);
    }

    @Override
    protected @NonNull DocumentRepository createDocumentRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudDocumentRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull AdministratorRepository createAdministratorRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudAdministratorRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull PatronRepository createPatronRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudPatronRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull StaffRepository createStaffRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudStaffRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull DocumentContentRepository createDocumentContentRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudDocumentContentRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull BorrowingSessionRepository createBorrowingSessionRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudBorrowingSessionRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull ReviewRepository createReviewRepository() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudReviewRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull DocumentSearchGenerator createDocumentSearchGenerator() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudIndexedDocumentSearchGenerator.of(cloudDatabaseConfigurationsProvider);
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator createDocumentRecommendationGenerator() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudIndexedDocumentRecommendationGenerator.of(CloudDocumentRepository.of(cloudDatabaseConfigurationsProvider));
    }

    @Override
    protected @NonNull AdministratorSearchGenerator createAdministratorSearchGenerator() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudLinearAdministratorSearchGenerator.of(CloudAdministratorRepository.of(cloudDatabaseConfigurationsProvider));
    }

    @Override
    protected @NonNull PatronSearchGenerator createPatronSearchGenerator() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudLinearPatronSearchGenerator.of(CloudPatronRepository.of(cloudDatabaseConfigurationsProvider));
    }

    @Override
    protected @NonNull StaffSearchGenerator createStaffSearchGenerator() {
        val cloudDatabaseConfigurationsProvider = createCloudDatabaseConfigurationsProvider();
        return CloudLinearStaffSearchGenerator.of(CloudStaffRepository.of(cloudDatabaseConfigurationsProvider));
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator createDocumentQrCodeGenerator() {
        return ZxingDocumentQrCodeGenerator.of();
    }

    @Override
    protected @NonNull DocumentUrlGenerator createDocumentUrlGenerator() {
        return CustomLandingPageDocumentUrlGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator createAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenRepository createAuthenticationTokenRepository() {
        val dotenvProvider = this.getDotenvProvider();
        return KeyStoreAuthenticationTokenRepository.of(dotenvProvider);
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

    @Override
    protected @NonNull FileVerifier createFileVerifier() {
        return ApacheTikaFileVerifier.of();
    }

    @Override
    protected @NonNull FileStorageProvider createFileStorageProvider() {
        return ApacheTikaTemporaryFileStorageProvider.of();
    }
}
