package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "administrator", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudAdministratorRepositoryTest extends AdministratorRepositoryTest {
    @Override
    protected @NonNull AdministratorRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        val cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);

        return CloudAdministratorRepository.of(cloudDatabaseConfigurationsProvider);
    }
}