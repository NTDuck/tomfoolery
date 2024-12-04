package org.tomfoolery.configurations.monolith.console.utils.resources;

import com.github.javafaker.Faker;
import lombok.Cleanup;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PopulatedApplicationResources extends ApplicationResources {
    private final @Unsigned int NUMBER_OF_ADMINISTRATORS = 4;
    private final @Unsigned int NUMBER_OF_PATRONS = 44;
    private final @Unsigned int NUMBER_OF_STAFF = 4;

    private final @NonNull Faker faker = Faker.instance();

    public static @NonNull PopulatedApplicationResources of() {
        return new PopulatedApplicationResources();
    }

    protected PopulatedApplicationResources() {
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
            IntStream.range(0, NUMBER_OF_ADMINISTRATORS)
                .parallel()
                .forEach(_ -> super.administratorRepository.save(this.createFakeAdministrator()));
        });

        executorService.submit(() -> {
            IntStream.range(0, NUMBER_OF_PATRONS)
                .parallel()
                .forEach(_ -> super.patronRepository.save(this.createFakePatron()));
        });

        executorService.submit(() -> {
            IntStream.range(0, NUMBER_OF_STAFF)
                .parallel()
                .forEach(_ -> super.staffRepository.save(this.createFakeStaff()));
        });

        executorService.shutdown();
    }

    public void populateDocumentRepositories() {

    }

    private @NonNull Administrator createFakeAdministrator(@NonNull String username, @NonNull CharSequence password) {
        return Administrator.of(
            Administrator.Id.of(UUID.fromString(faker.internet().uuid())),
            Administrator.Audit.of(Administrator.Audit.Timestamps.of(faker.date().past(4444, TimeUnit.DAYS).toInstant())),
            Administrator.Credentials.of(username, super.passwordEncoder.encode(SecureString.of(password)))
        );
    }

    private @NonNull Patron createFakePatron(@NonNull String username, @NonNull CharSequence password) {
        return Patron.of(
            Patron.Id.of(UUID.fromString(faker.internet().uuid())),
            Patron.Audit.of(Patron.Audit.Timestamps.of(faker.date().past(4444, TimeUnit.DAYS).toInstant())),
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
                Staff.Audit.Timestamps.of(faker.date().past(4444, TimeUnit.DAYS).toInstant()),
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
