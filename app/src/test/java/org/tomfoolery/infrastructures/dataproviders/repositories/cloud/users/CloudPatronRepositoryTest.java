package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "patron", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudPatronRepositoryTest extends PatronRepositoryTest {
    @Override
    protected @NonNull CloudPatronRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudPatronRepository.of(cloudDatabaseConfigurationsProvider);
    }
}
