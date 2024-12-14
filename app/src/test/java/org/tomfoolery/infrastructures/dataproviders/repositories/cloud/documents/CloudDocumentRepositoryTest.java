package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

@Test(groups = { "unit", "repository", "documents", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudDocumentRepositoryTest extends BaseUnitTest<CloudDocumentRepository> {
    @Override
    protected @NonNull CloudDocumentRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        val cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);

        return CloudDocumentRepository.of(cloudDatabaseConfigurationsProvider);
    }
}