package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepositoryTest;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class CloudPatronRepositoryTest extends BaseUnitTest<CloudPatronRepository> {

    @Override
    protected @NonNull CloudPatronRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudPatronRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Test
    public void WhenSavingPatron_ExpectPatronToBeSavedSuccessfully() {
        Patron.Id patronId = Patron.Id.of(UUID.randomUUID());
        Patron.Credentials credentials = Patron.Credentials.of("test_user", SecureString.of("password".toCharArray()));
        Patron.Audit.Timestamps timestamps = Patron.Audit.Timestamps.of(Instant.now());
        Patron.Audit audit = Patron.Audit.of(timestamps);
        Patron.Metadata metadata = Patron.Metadata.of(
                Patron.Metadata.Name.of("John", "Doe"),
                LocalDate.of(1990, 1, 1),
                "1234567890",
                Patron.Metadata.Address.of("New York", "USA"),
                "john.doe@example.com"
        );
        Patron patron = Patron.of(patronId, audit, credentials, metadata);

        try {
            this.testSubject.save(patron);

            // Retrieve the saved patron to assert it was saved correctly
            Patron retrievedPatron = this.testSubject.getById(patronId);
            assertNotNull(retrievedPatron, "Saved patron should not be null");
            assertEquals(retrievedPatron.getId(), patronId, "Patron ID should match the saved value");
            assertEquals(retrievedPatron.getCredentials().getUsername(), "test_user", "Username should match the saved value");

        } catch (Exception e) {
            fail("Saving patron failed: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = {"WhenSavingPatron_ExpectPatronToBeSavedSuccessfully"})
    public void GivenExistingPatron_WhenDeletingPatron_ExpectPatronToBeDeleted() {
        Patron.Id patronId = Patron.Id.of(UUID.randomUUID());
        Patron.Credentials credentials = Patron.Credentials.of("delete_user", SecureString.of("password".toCharArray()));
        Patron.Audit.Timestamps timestamps = Patron.Audit.Timestamps.of(Instant.now());
        Patron.Audit audit = Patron.Audit.of(timestamps);
        Patron.Metadata metadata = Patron.Metadata.of(
                Patron.Metadata.Name.of("Jane", "Doe"),
                LocalDate.of(1990, 1, 1),
                "9876543210",
                Patron.Metadata.Address.of("Los Angeles", "USA"),
                "jane.doe@example.com"
        );
        Patron patron = Patron.of(patronId, audit, credentials, metadata);

        try {
            this.testSubject.save(patron);

            this.testSubject.delete(patronId);

            Patron retrievedPatron = this.testSubject.getById(patronId);
            assertNull(retrievedPatron, "Deleted patron should not exist");

        } catch (Exception e) {
            fail("Deleting patron failed: " + e.getMessage());
        }
    }

    @Test
    public void WhenQueryingAllPatrons_ExpectAllSavedPatronsToBeReturned() {
        Patron patron1 = Patron.of(
                Patron.Id.of(UUID.randomUUID()),
                Patron.Audit.of(Patron.Audit.Timestamps.of(Instant.now())),
                Patron.Credentials.of("user1", SecureString.of("pass1".toCharArray())),
                Patron.Metadata.of(
                        Patron.Metadata.Name.of("Alice", "Smith"),
                        LocalDate.of(1985, 5, 15),
                        "1111111111",
                        Patron.Metadata.Address.of("San Francisco", "USA"),
                        "alice.smith@example.com"
                )
        );

        Patron patron2 = Patron.of(
                Patron.Id.of(UUID.randomUUID()),
                Patron.Audit.of(Patron.Audit.Timestamps.of(Instant.now())),
                Patron.Credentials.of("user2", SecureString.of("pass2".toCharArray())),
                Patron.Metadata.of(
                        Patron.Metadata.Name.of("Bob", "Johnson"),
                        LocalDate.of(1980, 8, 20),
                        "2222222222",
                        Patron.Metadata.Address.of("Seattle", "USA"),
                        "bob.johnson@example.com"
                )
        );

        try {
            this.testSubject.save(patron1);
            this.testSubject.save(patron2);

            val patrons = this.testSubject.show();

            assertNotNull(patrons, "Patrons list should not be null");
            assertTrue(patrons.size() >= 2, "There should be at least two patrons in the database");

            assertTrue(patrons.contains(patron1), "Patron1 should be in the list of patrons");
            assertTrue(patrons.contains(patron2), "Patron2 should be in the list of patrons");

        } catch (Exception e) {
            fail("Querying all patrons failed: " + e.getMessage());
        }
    }
}
