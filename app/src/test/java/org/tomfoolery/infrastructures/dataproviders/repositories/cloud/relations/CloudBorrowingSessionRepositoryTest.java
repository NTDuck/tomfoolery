package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "bi-relation", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudBorrowingSessionRepositoryTest extends BorrowingSessionRepositoryTest {
    @Override
    protected @NonNull CloudBorrowingSessionRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);

        return CloudBorrowingSessionRepository.of(cloudDatabaseConfigurationsProvider);
    }
}

