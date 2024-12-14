package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

@Test(groups = { "unit", "repository", "bi-relation", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudReviewRepositoryTest extends ReviewRepositoryTest {
    @Override
    protected @NonNull CloudReviewRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);

        return CloudReviewRepository.of(cloudDatabaseConfigurationsProvider);
    }
}
