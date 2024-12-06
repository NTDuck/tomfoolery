package org.tomfoolery.configurations.monolith.gui.utils;

import com.github.javafaker.Faker;
import lombok.Cleanup;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.aggregates.BaseHybridRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PopulatedAppResources extends AppResources {
    private final @Unsigned int NUMBER_OF_DOCUMENTS = 444;
    private final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private final @Unsigned int NUMBER_OF_PATRONS = 44;
    private final @Unsigned int NUMBER_OF_STAFF = 4;

    private final @Unsigned int DAYS_CREATED_WITHIN = 4444;
    private final @Unsigned int MIN_NUMBER_OF_AUTHORS = 1;
    private final @Unsigned int MAX_NUMBER_OF_AUTHORS = 14;
    private final @Unsigned int MIN_NUMBER_OF_GENRES = 1;
    private final @Unsigned int MAX_NUMBER_OF_GENRES = 14;
    private final @Unsigned int MIN_PUBLISHED_YEAR = 444;
    private final @Unsigned int MAX_PUBLISHED_YEAR = 2444;
    private final @Unsigned int MIN_NUMBER_OF_RATINGS = 1;
    private final @Unsigned int MAX_NUMBER_OF_RATINGS = 4444;

    private final @NonNull Faker faker = Faker.instance();

    public static @NonNull PopulatedAppResources of() {
        return new PopulatedAppResources();
    }

    protected PopulatedAppResources() {
        super();

        this.populate();
    }

    public void populate() {
        @Cleanup val executorService = Executors.newFixedThreadPool(2);

        executorService.submit(this::populateDocumentRepositories);
        executorService.submit(this::populateUserRepositories);

        executorService.close();
    }

    public void populateUserRepositories() {
        @Cleanup val executorService = Executors.newFixedThreadPool(4);

        executorService.submit(() -> {
            super.administratorRepository.save(this.createFakeAdministrator("admin_123", "Root_123"));
            super.patronRepository.save(this.createFakePatron("patron_123", "Root_123"));
            super.staffRepository.save(this.createFakeStaff("staff_123", "Root_123"));
        });

        executorService.submit(() -> {
            IntStream.range(0, NUMBER_OF_ADMINISTRATORS).parallel()
                    .forEach(_ -> super.administratorRepository.save(this.createFakeAdministrator()));
        });

        executorService.submit(() -> {
            IntStream.range(0, NUMBER_OF_PATRONS).parallel()
                    .forEach(_ -> super.patronRepository.save(this.createFakePatron()));
        });

        executorService.submit(() -> {
            IntStream.range(0, NUMBER_OF_STAFF).parallel()
                    .forEach(_ -> super.staffRepository.save(this.createFakeStaff()));
        });

        executorService.shutdown();
    }

    public void populateDocumentRepositories() {
        IntStream.range(0, NUMBER_OF_DOCUMENTS).parallel()
                .forEach(_ -> this.documentRepository.save(this.addDocument()));
    }

    private @NonNull Document addDocument() {
        val documentId = Document.Id.of(faker.code().isbn10());
        assert documentId != null;

        // Hybrid Repository might support
        if (this.documentRepository instanceof BaseHybridRepository<?, ?>) {
            val document = this.documentRepository.getById(documentId);

            if (document != null)
                return document;
        }

        return Document.of(
                documentId,
                Document.Audit.of(
                        Document.Audit.Timestamps.of(faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant()),
                        Staff.Id.of(UUID.fromString(faker.internet().uuid()))
                ),
                Document.Metadata.of(faker.book().title(), faker.lorem().paragraph(),
                        Stream.generate(() -> faker.book().author())
                                .limit(faker.number().numberBetween(MIN_NUMBER_OF_AUTHORS, MAX_NUMBER_OF_AUTHORS))
                                .collect(Collectors.toUnmodifiableList()),
                        Stream.generate(() -> faker.book().genre())
                                .limit(faker.number().numberBetween(MIN_NUMBER_OF_GENRES, MAX_NUMBER_OF_GENRES))
                                .collect(Collectors.toUnmodifiableList()),
                        Year.of(faker.number().numberBetween(MIN_PUBLISHED_YEAR, MAX_PUBLISHED_YEAR)),
                        faker.book().publisher()
                ),
                Document.Rating.of(
                        faker.number().randomDouble(4, AddDocumentReviewUseCase.MIN_RATING, AddDocumentReviewUseCase.MAX_RATING),
                        faker.number().numberBetween(MIN_NUMBER_OF_RATINGS, MAX_NUMBER_OF_RATINGS)
                ),
                null
        );
    }

    private @NonNull Administrator createFakeAdministrator(@NonNull String username, @NonNull CharSequence password) {
        return Administrator.of(
                Administrator.Id.of(UUID.fromString(faker.internet().uuid())),
                Administrator.Audit.of(Administrator.Audit.Timestamps.of(faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant())),
                Administrator.Credentials.of(username, super.passwordEncoder.encode(SecureString.of(password)))
        );
    }

    private @NonNull Patron createFakePatron(@NonNull String username, @NonNull CharSequence password) {
        return Patron.of(
                Patron.Id.of(UUID.fromString(faker.internet().uuid())),
                Patron.Audit.of(Patron.Audit.Timestamps.of(faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant())),
                Patron.Credentials.of(username, super.passwordEncoder.encode(SecureString.of(password))),
                Patron.Metadata.of(
                        Patron.Metadata.Name.of(faker.name().firstName(), faker.name().lastName()),
                        faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        faker.phoneNumber().phoneNumber(),
                        Patron.Metadata.Address.of(faker.address().city(), faker.address().country()),
                        faker.internet().emailAddress()
                )
        );
    }

    private @NonNull Staff createFakeStaff(@NonNull String username, @NonNull CharSequence password) {
        return Staff.of(
                Staff.Id.of(UUID.fromString(faker.internet().uuid())),
                Staff.Audit.of(
                        Staff.Audit.Timestamps.of(faker.date().past(DAYS_CREATED_WITHIN, TimeUnit.DAYS).toInstant()),
                        Administrator.Id.of(UUID.fromString(faker.internet().uuid()))
                ),
                Staff.Credentials.of(username, super.passwordEncoder.encode(SecureString.of(password)))
        );
    }

    private @NonNull Administrator createFakeAdministrator() {
        return this.createFakeAdministrator(faker.name().username(), faker.internet().password(8, 32));
    }

    private @NonNull Patron createFakePatron() {
        return this.createFakePatron(faker.name().username(), faker.internet().password(8, 32));
    }

    private @NonNull Staff createFakeStaff() {
        return this.createFakeStaff(faker.name().username(), faker.internet().password(8, 32));
    }
}
