package org.tomfoolery.core.usecases.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor(staticName = "of")
public final class CreatePatronAccountUseCase implements ThrowableConsumer<CreatePatronAccountUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public void accept(@NonNull Request request) throws PatronCredentialsInvalidException, PatronAlreadyExistsException {
        val rawPatronCredentials = request.getRawPatronCredentials();
        val patronMetadata = request.getPatronMetadata();

        this.ensurePatronCredentialsAreValid(rawPatronCredentials);
        this.ensurePatronDoesNotExist(rawPatronCredentials);
        val encodedPatronCredentials = this.passwordEncoder.encodeCredentials(rawPatronCredentials);

        val patron = this.createPatron(encodedPatronCredentials, patronMetadata);

        this.patronRepository.save(patron);
    }

    private void ensurePatronCredentialsAreValid(Patron.@NonNull Credentials rawPatronCredentials) throws PatronCredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(rawPatronCredentials))
            throw new PatronCredentialsInvalidException();
    }

    private void ensurePatronDoesNotExist(Patron.@NonNull Credentials patronCredentials) throws PatronAlreadyExistsException {
        val patronUsername = patronCredentials.getUsername();

        if (this.patronRepository.contains(patronUsername))
            throw new PatronAlreadyExistsException();
    }

    private @NonNull Patron createPatron(Patron.@NonNull Credentials encodedPatronCredentials, Patron.@NonNull Metadata patronMetadata) {
        val patronId = Patron.Id.of(UUID.randomUUID());
        val patronAuditTimestamps = Patron.Audit.Timestamps.of(Instant.now());
        val patronAudit = Patron.Audit.of(false, patronAuditTimestamps);

        return Patron.of(patronId, encodedPatronCredentials, patronAudit, patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Credentials rawPatronCredentials;
        Patron.@NonNull Metadata patronMetadata;
    }

    public static class PatronCredentialsInvalidException extends Exception {}
    public static class PatronAlreadyExistsException extends Exception {}
}
