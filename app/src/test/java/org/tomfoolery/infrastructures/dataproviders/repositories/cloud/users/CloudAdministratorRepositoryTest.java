package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepositoryTest;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.*;

public class CloudAdministratorRepositoryTest extends BaseUnitTest<CloudAdministratorRepository> {

    @Override
    protected @NonNull CloudAdministratorRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudAdministratorRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Test
    public void WhenSavingAdministrator_ExpectAdministratorToBeSavedSuccessfully() {
        Administrator.Id adminId = Administrator.Id.of(UUID.randomUUID());
        Administrator.Credentials credentials = Administrator.Credentials.of("adminUser", SecureString.of("password123".toCharArray()));
        Administrator.Audit.Timestamps timestamps = Administrator.Audit.Timestamps.of(Instant.now());
        Administrator administrator = Administrator.of(adminId, Administrator.Audit.of(timestamps), credentials);

        try {
            this.testSubject.save(administrator);

            Administrator retrievedAdministrator = this.testSubject.getById(adminId);
            assertNotNull(retrievedAdministrator, "Saved administrator should not be null");
            assertEquals(retrievedAdministrator.getCredentials().getUsername(), "adminUser", "Username should match the saved value");
            assertEquals(retrievedAdministrator.getId(), adminId, "Administrator ID should match the saved value");

        } catch (Exception e) {
            fail("Saving administrator failed: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = {"WhenSavingAdministrator_ExpectAdministratorToBeSavedSuccessfully"})
    public void GivenExistingAdministrator_WhenDeletingAdministrator_ExpectAdministratorToBeDeleted() {
        Administrator.Id adminId = Administrator.Id.of(UUID.randomUUID());
        Administrator.Credentials credentials = Administrator.Credentials.of("adminUser", SecureString.of("password123".toCharArray()));
        Administrator.Audit.Timestamps timestamps = Administrator.Audit.Timestamps.of(Instant.now());
        Administrator administrator = Administrator.of(adminId, Administrator.Audit.of(timestamps), credentials);

        try {
            this.testSubject.save(administrator);

            this.testSubject.delete(adminId);

            Administrator retrievedAdministrator = this.testSubject.getById(adminId);
            assertNull(retrievedAdministrator, "Deleted administrator should not exist");

        } catch (Exception e) {
            fail("Deleting administrator failed: " + e.getMessage());
        }
    }

    @Test
    public void WhenQueryingAllAdministrators_ExpectAllSavedAdministratorsToBeReturned() {
        Administrator admin1 = Administrator.of(
                Administrator.Id.of(UUID.randomUUID()),
                Administrator.Audit.of(Administrator.Audit.Timestamps.of(Instant.now())),
                Administrator.Credentials.of("admin1", SecureString.of("password1".toCharArray()))
        );

        Administrator admin2 = Administrator.of(
                Administrator.Id.of(UUID.randomUUID()),
                Administrator.Audit.of(Administrator.Audit.Timestamps.of(Instant.now())),
                Administrator.Credentials.of("admin2", SecureString.of("password2".toCharArray()))
        );

        try {
            this.testSubject.save(admin1);
            this.testSubject.save(admin2);

            Set<Administrator> administrators = this.testSubject.show();

            assertNotNull(administrators, "Administrators list should not be null");
            assertTrue(administrators.size() >= 2, "There should be at least two administrators in the database");

            assertTrue(administrators.contains(admin1), "Admin1 should be in the list of administrators");
            assertTrue(administrators.contains(admin2), "Admin2 should be in the list of administrators");

        } catch (Exception e) {
            fail("Querying all administrators failed: " + e.getMessage());
        }
    }

    @Test
    public void WhenRetrievingAdministratorByUsername_ExpectCorrectAdministratorToBeReturned() {
        Administrator.Id adminId = Administrator.Id.of(UUID.randomUUID());
        Administrator.Credentials credentials = Administrator.Credentials.of("specificUser", SecureString.of("password123".toCharArray()));
        Administrator.Audit.Timestamps timestamps = Administrator.Audit.Timestamps.of(Instant.now());
        Administrator administrator = Administrator.of(adminId, Administrator.Audit.of(timestamps), credentials);

        try {
            this.testSubject.save(administrator);

            Administrator retrievedAdministrator = this.testSubject.getByUsername("specificUser");
            assertNotNull(retrievedAdministrator, "Retrieved administrator should not be null");
            assertEquals(retrievedAdministrator.getCredentials().getUsername(), "specificUser", "Username should match the saved value");
            assertEquals(retrievedAdministrator.getId(), adminId, "Administrator ID should match the saved value");

        } catch (Exception e) {
            fail("Retrieving administrator by username failed: " + e.getMessage());
        }
    }
}
