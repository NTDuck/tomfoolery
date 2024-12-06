package org.tomfoolery.configurations.contexts.test;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.configurations.contexts.dev.InMemoryApplicationContext;

import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class InMemoryTestApplicationContext extends InMemoryApplicationContext {
    private final @Unsigned int NUMBER_OF_DOCUMENTS = 4444;
    private final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private final @Unsigned int NUMBER_OF_PATRONS = 44;
    private final @Unsigned int NUMBER_OF_STAFF = 4;

    private final @Unsigned int DAYS_CREATED_WITHIN = 4444;

    private final @Unsigned int MIN_PASSWORD_LENGTH = 8;
    private final @Unsigned int MAX_PASSWORD_LENGTH = 32;

    private final @Unsigned int MIN_NUMBER_OF_AUTHORS = 1;
    private final @Unsigned int MAX_NUMBER_OF_AUTHORS = 14;
    private final @Unsigned int MIN_NUMBER_OF_GENRES = 1;
    private final @Unsigned int MAX_NUMBER_OF_GENRES = 14;
    private final @Unsigned int MIN_PUBLISHED_YEAR = 444;
    private final @Unsigned int MAX_PUBLISHED_YEAR = 2444;
    private final @Unsigned int MAX_DECIMALS_OF_RATINGS = 4;
    private final @Unsigned int MIN_NUMBER_OF_RATINGS = 1;
    private final @Unsigned int MAX_NUMBER_OF_RATINGS = 4444;

    private final @NonNull Faker faker = Faker.instance();

    {
        this.populate();
    }

    private void populate() {
        val executorService = Executors.newFixedThreadPool(2);

        executorService.submit(this::populateUserRepositoriesWithDeterministicUsers);
        executorService.submit(this::seedRepositories);

        executorService.shutdown();

        // CompletableFuture.runAsync(this::populateUserRepositoriesWithDeterministicUsers);
        // CompletableFuture.runAsync(this::seedRepositories);
    }

    private void seedRepositories() {
        val executorService = Executors.newFixedThreadPool(4);

        executorService.submit(this::seedDocumentRepository);
        executorService.submit(this::seedAdministratorRepository);
        executorService.submit(this::seedPatronRepository);
        executorService.submit(this::seedStaffRepository);

        executorService.shutdown();

        // CompletableFuture.runAsync(this::seedDocumentRepository);
        // CompletableFuture.runAsync(this::seedAdministratorRepository);
        // CompletableFuture.runAsync(this::seedPatronRepository);
        // CompletableFuture.runAsync(this::seedStaffRepository);
    }

    private void seedDocumentRepository() {
        seedRepositoryWithMockedEntity(this::getDocumentRepository, this::createMockedDocument, NUMBER_OF_DOCUMENTS);
    }

    private void seedAdministratorRepository() {
        seedRepositoryWithMockedEntity(this::getAdministratorRepository, this::createMockedAdministrator, NUMBER_OF_ADMINISTRATORS);
    }

    private void seedPatronRepository() {
        seedRepositoryWithMockedEntity(this::getPatronRepository, this::createMockedPatron, NUMBER_OF_PATRONS);
    }

    private void seedStaffRepository() {
        seedRepositoryWithMockedEntity(this::getStaffRepository, this::createMockedStaff, NUMBER_OF_STAFF);
    }

    private void populateUserRepositoriesWithDeterministicUsers() {
        val administratorRepository = this.getAdministratorRepository();
        val patronRepository = this.getPatronRepository();
        val staffRepository = this.getStaffRepository();

        val deterministicAdministrator = this.createMockedAdministrator("admin_123", "Root_123");
        val deterministicPatron = this.createMockedPatron("patron_123", "Root_123");
        val deterministicStaff = this.createMockedStaff("staff_123", "Root_123");

        administratorRepository.save(deterministicAdministrator);
        patronRepository.save(deterministicPatron);
        staffRepository.save(deterministicStaff);
    }

    /**
     * Prone to race conditions.
     */
    private static <Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> void seedRepositoryWithMockedEntity(@NonNull Supplier<BaseRepository<Entity, EntityId>> repositorySupplier, @NonNull Supplier<Entity> mockedEntitySupplier, @Unsigned int numberOfEntities) {
        val repository = repositorySupplier.get();

        for (var i = 0; i < numberOfEntities; i++)
            CompletableFuture.runAsync(() -> {
                Entity mockedEntity;

                do {
                    mockedEntity = mockedEntitySupplier.get();
                } while (repository.contains(mockedEntity.getId()));

                repository.save(mockedEntity);
            });
    }

    private @NonNull Document createMockedDocument() {
        return Document.of(
            this.createMockedDocumentId(),
            Document.Audit.of(
                Document.Audit.Timestamps.of(this.faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant()),
                this.createMockedUserId()
            ),
            Document.Metadata.of(this.faker.book().title(), this.faker.lorem().paragraph(),
                Stream.generate(() -> this.faker.book().author())
                    .limit(this.faker.number().numberBetween(MIN_NUMBER_OF_AUTHORS, MAX_NUMBER_OF_AUTHORS))
                    .collect(Collectors.toUnmodifiableList()),
                Stream.generate(() -> this.faker.book().genre())
                    .limit(this.faker.number().numberBetween(MIN_NUMBER_OF_GENRES, MAX_NUMBER_OF_GENRES))
                    .collect(Collectors.toUnmodifiableList()),
                Year.of(this.faker.number().numberBetween(MIN_PUBLISHED_YEAR, MAX_PUBLISHED_YEAR)),
                this.faker.book().publisher()
            ),
            Document.Rating.of(
                this.faker.number().randomDouble(MAX_DECIMALS_OF_RATINGS, AddDocumentReviewUseCase.MIN_RATING, AddDocumentReviewUseCase.MAX_RATING),
                this.faker.number().numberBetween(MIN_NUMBER_OF_RATINGS, MAX_NUMBER_OF_RATINGS)
            ),
            null
        );
    }

    private @NonNull Administrator createMockedAdministrator(@NonNull String username, @NonNull CharSequence password) {
        val passwordEncoder = this.getPasswordEncoder();

        return Administrator.of(
            this.createMockedUserId(),
            Administrator.Audit.of(Administrator.Audit.Timestamps.of(this.faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant())),
            Administrator.Credentials.of(username, passwordEncoder.encode(SecureString.of(password)))
        );
    }

    private @NonNull Patron createMockedPatron(@NonNull String username, @NonNull CharSequence password) {
        val passwordEncoder = this.getPasswordEncoder();

        return Patron.of(
            this.createMockedUserId(),
            Patron.Audit.of(Patron.Audit.Timestamps.of(this.faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant())),
            Patron.Credentials.of(username, passwordEncoder.encode(SecureString.of(password))),
            Patron.Metadata.of(
                Patron.Metadata.Name.of(this.faker.name().firstName(), this.faker.name().lastName()),
                this.faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                this.faker.phoneNumber().phoneNumber(),
                Patron.Metadata.Address.of(this.faker.address().city(), this.faker.address().country()),
                this.faker.internet().emailAddress()
            )
        );
    }

    private @NonNull Staff createMockedStaff(@NonNull String username, @NonNull CharSequence password) {
        val passwordEncoder = this.getPasswordEncoder();

        return Staff.of(
            this.createMockedUserId(),
            Staff.Audit.of(
                Staff.Audit.Timestamps.of(this.faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant()),
                this.createMockedUserId()
            ),
            Staff.Credentials.of(username, passwordEncoder.encode(SecureString.of(password)))
        );
    }

    private Document.@NonNull Id createMockedDocumentId() {
        val documentId = Document.Id.of(this.faker.code().isbn10());
        assert documentId != null;

        return documentId;
    }

    private BaseUser.@NonNull Id createMockedUserId() {
        return BaseUser.Id.of(UUID.fromString(this.faker.internet().uuid()));
    }

    private @NonNull Administrator createMockedAdministrator() {
        return this.createMockedAdministrator(this.faker.name().username(), this.faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }

    private @NonNull Patron createMockedPatron() {
        return this.createMockedPatron(this.faker.name().username(), this.faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }

    private @NonNull Staff createMockedStaff() {
        return this.createMockedStaff(this.faker.name().username(), this.faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }
}
