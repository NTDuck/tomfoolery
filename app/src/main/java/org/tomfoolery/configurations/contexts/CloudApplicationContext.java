package org.tomfoolery.configurations.contexts;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.StandardApplicationContext;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
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
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators.*;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations.CloudReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudStaffRepository;

@NoArgsConstructor(staticName = "of")
public class CloudApplicationContext extends StandardApplicationContext {
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
}
