package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.fail;

@Test(groups = { "unit", "repository", "bi-relation", "cloud" }, dependsOnGroups = { "configurations" })
public class CloudBorrowingSessionRepositoryTest extends BaseUnitTest<CloudBorrowingSessionRepository> {
    @Override
    protected @NonNull CloudBorrowingSessionRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider = CloudDatabaseConfigurationsProvider.of(dotenvProvider);
        return CloudBorrowingSessionRepository.of(cloudDatabaseConfigurationsProvider);
    }

    @Test
    public void WhenSavingBorrowingSession_ExpectSessionToBeSavedSuccessfully() {
        BorrowingSession.Id sessionId = BorrowingSession.Id.of(
                Document.Id.of("123456789X"),
                Patron.Id.of(UUID.randomUUID())
        );
        Instant borrowedTimestamp = Instant.now();
        Instant dueTimestamp = borrowedTimestamp.plus(Duration.ofDays(14));
        BorrowingSession session = BorrowingSession.of(sessionId, borrowedTimestamp, dueTimestamp);

        try {
            this.testSubject.save(session);

            BorrowingSession retrievedSession = this.testSubject.getById(sessionId);
            assertNotNull(retrievedSession, "Saved borrowing session should not be null");
            assertEquals(retrievedSession.getBorrowedTimestamp(), borrowedTimestamp, "Borrowed timestamp should match");
            assertEquals(retrievedSession.getDueTimestamp(), dueTimestamp, "Due timestamp should match");
            assertEquals(retrievedSession.getId(), sessionId, "Session ID should match the saved value");

        } catch (Exception e) {
            fail("Saving borrowing session failed: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = { "WhenSavingBorrowingSession_ExpectSessionToBeSavedSuccessfully" })
    public void GivenExistingBorrowingSession_WhenDeletingSession_ExpectSessionToBeDeleted() {
        BorrowingSession.Id sessionId = BorrowingSession.Id.of(
                Document.Id.of("123456789X"),
                Patron.Id.of(UUID.randomUUID())
        );
        Instant borrowedTimestamp = Instant.now();
        Instant dueTimestamp = borrowedTimestamp.plus(Duration.ofDays(14));
        BorrowingSession session = BorrowingSession.of(sessionId, borrowedTimestamp, dueTimestamp);

        try {
            this.testSubject.save(session);
            this.testSubject.delete(sessionId);

            BorrowingSession retrievedSession = this.testSubject.getById(sessionId);
            assertNull(retrievedSession, "Deleted borrowing session should not exist");

        } catch (Exception e) {
            fail("Deleting borrowing session failed: " + e.getMessage());
        }
    }

    @Test
    public void WhenQueryingAllBorrowingSessions_ExpectAllSavedSessionsToBeReturned() {
        BorrowingSession session1 = BorrowingSession.of(
                BorrowingSession.Id.of(
                        Document.Id.of("123456789X"),
                        Patron.Id.of(UUID.randomUUID())
                ),
                Instant.now(),
                Instant.now().plus(Duration.ofDays(14))
        );

        BorrowingSession session2 = BorrowingSession.of(
                BorrowingSession.Id.of(
                        Document.Id.of("0198534531"),
                        Patron.Id.of(UUID.randomUUID())
                ),
                Instant.now(),
                Instant.now().plus(Duration.ofDays(14))
        );

        try {
            this.testSubject.save(session1);
            this.testSubject.save(session2);

            val sessions = this.testSubject.show();

            assertNotNull(sessions, "Borrowing sessions list should not be null");
            assertTrue(sessions.size() >= 2, "There should be at least two sessions in the database");

            assertTrue(sessions.contains(session1), "Session1 should be in the list of sessions");
            assertTrue(sessions.contains(session2), "Session2 should be in the list of sessions");

        } catch (Exception e) {
            fail("Querying all borrowing sessions failed: " + e.getMessage());
        }
    }
}

