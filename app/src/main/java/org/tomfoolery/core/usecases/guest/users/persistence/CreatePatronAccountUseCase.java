package org.tomfoolery.core.usecases.guest.users.persistence;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.verifiers.users.patron.AddressVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.users.patron.DateOfBirthVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.users.patron.EmailVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.users.patron.PhoneNumberVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.users.authentication.security.CredentialsVerifier;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "of")
public final class CreatePatronAccountUseCase implements ThrowableConsumer<CreatePatronAccountUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public void accept(@NonNull Request request) throws PatronCredentialsInvalidException, PatronAlreadyExistsException, AddressInvalidException, DateOfBirthInvalidException, EmailInvalidException, PhoneNumberInvalidException {
        val rawPatronCredentials = request.getRawPatronCredentials();
        val patronMetadata = request.getPatronMetadata();

        this.ensurePatronCredentialsAreValid(rawPatronCredentials);
        this.ensurePatronMetadataIsValid(patronMetadata);

        this.ensurePatronDoesNotExist(rawPatronCredentials);
        val encodedPatronCredentials = this.encodePatronCredentials(rawPatronCredentials);

        val patron = this.createPatron(encodedPatronCredentials, patronMetadata);

        this.patronRepository.save(patron);
    }

    private void ensurePatronCredentialsAreValid(Patron.@NonNull Credentials rawPatronCredentials) throws PatronCredentialsInvalidException {
        if (!CredentialsVerifier.verify(rawPatronCredentials))
            throw new PatronCredentialsInvalidException();
    }

    private void ensurePatronMetadataIsValid(Patron.@NonNull Metadata newPatronMetadata) throws AddressInvalidException, DateOfBirthInvalidException, EmailInvalidException, PhoneNumberInvalidException {
        val futureOfAddressVerification = CompletableFuture.runAsync(() -> {
            if (!AddressVerifier.verify(newPatronMetadata.getAddress()))
                throw new AddressInvalidException();
        });

        val futureOfDateOfBirthVerification = CompletableFuture.runAsync(() -> {
            if (!DateOfBirthVerifier.verify(newPatronMetadata.getDateOfBirth()))
                throw new DateOfBirthInvalidException();
        });

        val futureOfEmailVerification = CompletableFuture.runAsync(() -> {
            if (!EmailVerifier.verify(newPatronMetadata.getEmail()))
                throw new EmailInvalidException();
        });

        val futureOfPhoneNumberVerification = CompletableFuture.runAsync(() -> {
            if (!PhoneNumberVerifier.verify(newPatronMetadata.getPhoneNumber()))
                throw new PhoneNumberInvalidException();
        });

        CompletableFuture.allOf(futureOfAddressVerification, futureOfDateOfBirthVerification, futureOfEmailVerification, futureOfPhoneNumberVerification).join();
    }

    private void ensurePatronDoesNotExist(Patron.@NonNull Credentials patronCredentials) throws PatronAlreadyExistsException {
        val patronUsername = patronCredentials.getUsername();

        if (this.patronRepository.contains(patronUsername))
            throw new PatronAlreadyExistsException();
    }

    private Patron.@NonNull Credentials encodePatronCredentials(Staff.@NonNull Credentials rawPatronCredentials) {
        val rawPatronPassword = rawPatronCredentials.getPassword();
        val encodedPatronPassword = this.passwordEncoder.encode(rawPatronPassword);

        return rawPatronCredentials.withPassword(encodedPatronPassword);
    }

    private @NonNull Patron createPatron(Patron.@NonNull Credentials encodedPatronCredentials, Patron.@NonNull Metadata patronMetadata) {
        val patronId = Patron.Id.of(UUID.randomUUID());
        val patronAuditTimestamps = Patron.Audit.Timestamps.of(Instant.now());
        val patronAudit = Patron.Audit.of(patronAuditTimestamps);

        return Patron.of(patronId, patronAudit, encodedPatronCredentials, patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Credentials rawPatronCredentials;
        Patron.@NonNull Metadata patronMetadata;
    }

    public static class AddressInvalidException extends PatronMetadataInvalidException {}
    public static class DateOfBirthInvalidException extends PatronMetadataInvalidException {}
    public static class EmailInvalidException extends PatronMetadataInvalidException {}
    public static class PhoneNumberInvalidException extends PatronMetadataInvalidException {}

    public static class PatronMetadataInvalidException extends RuntimeException {}
    public static class PatronCredentialsInvalidException extends Exception {}
    public static class PatronAlreadyExistsException extends Exception {}
}
