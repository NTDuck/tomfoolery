package org.tomfoolery.configurations.contexts.test;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.configurations.contexts.dev.InMemoryApplicationContext;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.AdministratorMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@NoArgsConstructor
public class InMemoryTestApplicationContext extends InMemoryApplicationContext {
    private static final @Unsigned int NUMBER_OF_DOCUMENTS = 4444;
    private static final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private static final @Unsigned int NUMBER_OF_PATRONS = 44;
    private static final @Unsigned int NUMBER_OF_STAFF = 4;

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();
    private final @NonNull AdministratorMocker administratorMocker = AdministratorMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();
    private final @NonNull StaffMocker staffMocker = StaffMocker.of();

    {
        this.populate();
    }

    private void populate() {
        val executorService = Executors.newFixedThreadPool(2);

        executorService.submit(this::populateUserRepositoriesWithDeterministicUsers);
        executorService.submit(this::seedRepositories);

        executorService.shutdown();
    }

    private void seedRepositories() {
        val executorService = Executors.newFixedThreadPool(4);

        executorService.submit(this::seedDocumentRepository);
        executorService.submit(this::seedAdministratorRepository);
        executorService.submit(this::seedPatronRepository);
        executorService.submit(this::seedStaffRepository);

        executorService.shutdown();
    }

    private void seedDocumentRepository() {
        val documentRepository = this.getDocumentRepository();
        seedRepositoryWithMockEntities(documentRepository, this.documentMocker, NUMBER_OF_DOCUMENTS);
    }

    private void seedAdministratorRepository() {
        val administratorRepository = this.getAdministratorRepository();
        seedRepositoryWithMockEntities(administratorRepository, this.administratorMocker, NUMBER_OF_ADMINISTRATORS);
    }

    private void seedPatronRepository() {
        val patronRepository = this.getPatronRepository();
        seedRepositoryWithMockEntities(patronRepository, this.patronMocker, NUMBER_OF_PATRONS);
    }

    private void seedStaffRepository() {
        val staffRepository = this.getStaffRepository();
        seedRepositoryWithMockEntities(staffRepository, this.staffMocker, NUMBER_OF_STAFF);
    }

    private void populateUserRepositoriesWithDeterministicUsers() {
        val administratorRepository = this.getAdministratorRepository();
        val patronRepository = this.getPatronRepository();
        val staffRepository = this.getStaffRepository();

        val deterministicAdministrator = this.createMockUserWithUsernameAndPassword(this.administratorMocker, "admin_123", "Root_123");
        val deterministicPatron = this.createMockUserWithUsernameAndPassword(this.patronMocker, "patron_123", "Root_123");
        val deterministicStaff = this.createMockUserWithUsernameAndPassword(this.staffMocker, "staff_123", "Root_123");

        administratorRepository.save(deterministicAdministrator);
        patronRepository.save(deterministicPatron);
        staffRepository.save(deterministicStaff);
    }

    /**
     * Prone to race conditions.
     */
    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> void seedRepositoryWithMockEntities(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull EntityMocker<Entity, EntityId> entityMocker, @Unsigned int numberOfEntities) {
        for (var i = 0; i < numberOfEntities; i++)
            CompletableFuture.runAsync(() -> {
                EntityId entityId;

                do {
                    entityId = entityMocker.createMockEntityId();
                } while (repository.contains(entityId));

                val mockEntity = entityMocker.createMockEntityWithId(entityId);
                repository.save(mockEntity);
            });
    }

    private <User extends BaseUser> User createMockUserWithUsernameAndPassword(@NonNull UserMocker<User> userMocker, @NonNull String username, @NonNull CharSequence rawPassword) {
        val passwordEncoder = this.getPasswordEncoder();

        val wrappedPassword = SecureString.of(rawPassword);
        val encodedPassword = passwordEncoder.encode(wrappedPassword);

        val mockUserId = userMocker.createMockEntityId();
        val credentials = User.Credentials.of(username, encodedPassword);

        return userMocker.createMockUserWithIdAndCredentials(mockUserId, credentials);
    }
}
