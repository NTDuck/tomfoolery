package org.tomfoolery.configurations.contexts.test;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.contexts.dev.FileCachedInMemoryApplicationContext;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.relations.DocumentContentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.AdministratorMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
public class FullyMockedInMemoryTestApplicationContext extends FileCachedInMemoryApplicationContext {
    private static final @Unsigned int NUMBER_OF_DOCUMENTS = 4444;
    private static final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private static final @Unsigned int NUMBER_OF_PATRONS = 44;
    private static final @Unsigned int NUMBER_OF_STAFF = 4;
    private static final @Unsigned double PROPORTION_OF_DOCUMENTS_WITH_CONTENT = 0.9;

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();
    private final @NonNull AdministratorMocker administratorMocker = AdministratorMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();
    private final @NonNull StaffMocker staffMocker = StaffMocker.of();
    private final @NonNull DocumentContentMocker documentContentMocker = DocumentContentMocker.of();

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
        val executorService = Executors.newFixedThreadPool(5);

        executorService.submit(this::seedDocumentRepository);
        executorService.submit(this::seedAdministratorRepository);
        executorService.submit(this::seedPatronRepository);
        executorService.submit(this::seedStaffRepository);
        executorService.submit(this::seedDocumentContentRepository);

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

    private void seedDocumentContentRepository() {
        val documentContentRepository = this.getDocumentContentRepository();
        val documentRepository = this.getDocumentRepository();

        val documentContentIds = documentRepository.show().parallelStream()
            .map(Document::getId)
            .map(DocumentContent.Id::of)
            .collect(Collectors.toUnmodifiableList());

        val documentContentIdsSubset = getRandomSubset(documentContentIds, PROPORTION_OF_DOCUMENTS_WITH_CONTENT);
        seedRepositoryWithMockEntities(documentContentRepository, documentContentIdsSubset, this.documentContentMocker::createMockEntityWithId);
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

    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> void seedRepositoryWithMockEntities(@NonNull BaseRepository<Entity, EntityId> repository, @NonNull Collection<EntityId> entityIds, @NonNull Function<EntityId, Entity> entityByIdFunction) {
        entityIds.parallelStream()
            .map(entityByIdFunction)
            .forEach(repository::save);
    }

    private static <T> @NonNull Collection<T> getRandomSubset(@NonNull Collection<T> source, @Unsigned double proportion) {
        assert 0 <= proportion && proportion <= 1;

        return IntStream.range(0, source.size()).parallel()
            .boxed()
            .sorted((i, j) -> Double.compare(Math.random(), Math.random()))
            .limit((long) (source.size() * proportion))
            .map(index -> source.parallelStream()
                .skip(index)
                .findFirst()
                .orElseThrow())
            .collect(Collectors.toList());
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
