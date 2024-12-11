package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

@Test(groups = { "unit", "repository", "users", "staff", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudStaffRepositoryTest extends StaffRepositoryTest {
    @Override
    protected @NonNull StaffRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        val cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudStaffRepository.of(cloudDatabaseConfigurationsProvider);
    }
}
