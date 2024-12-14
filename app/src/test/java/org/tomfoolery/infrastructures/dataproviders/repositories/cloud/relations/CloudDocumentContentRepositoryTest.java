package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "uni-relation", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudDocumentContentRepositoryTest extends DocumentContentRepositoryTest {
    @Override
    protected @NonNull DocumentContentRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);

        return CloudDocumentContentRepository.of(cloudDatabaseConfigurationsProvider);
    }
}