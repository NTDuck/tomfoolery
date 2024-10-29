package org.tomfoolery.core.usecases.external.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

@RequiredArgsConstructor(staticName = "of")
public class CreatePatronAccountUseCase implements ThrowableConsumer<CreatePatronAccountUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public void accept(@NonNull Request request) throws PatronCredentialsInvalidException, PatronAlreadyExistsException {
        val patronCredentials = request.getPatronCredentials();
        val patronMetadata = request.getPatronMetadata();

        ensurePatronCredentialsAreValid(patronCredentials);
        ensurePatronDoesNotExist(patronCredentials);
        encodePatronPassword(patronCredentials);

        val patron = createPatron(patronCredentials, patronMetadata);
        this.patronRepository.save(patron);
    }

    private static void ensurePatronCredentialsAreValid(Patron.@NonNull Credentials patronCredentials) throws PatronCredentialsInvalidException {
        if (!CredentialsVerifier.verify(patronCredentials))
            throw new PatronCredentialsInvalidException();
    }

    private void ensurePatronDoesNotExist(Patron.@NonNull Credentials patronCredentials) throws PatronAlreadyExistsException {
        val patronUsername = patronCredentials.getUsername();

        if (this.patronRepository.contains(patronUsername))
            throw new PatronAlreadyExistsException();
    }

    private void encodePatronPassword(Patron.@NonNull Credentials patronCredentials) {
        val password = patronCredentials.getPassword();
        val encodedPassword = this.passwordEncoder.encode(password);
        patronCredentials.setPassword(encodedPassword);
    }

    private static @NonNull Patron createPatron(Patron.@NonNull Credentials patronCredentials, Patron.@NonNull Metadata patronMetadata) {
        val patronAudit = Patron.Audit.of(Patron.Audit.Timestamps.of());
        return Patron.of(patronCredentials, patronAudit, patronMetadata);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Credentials patronCredentials;
        Patron.@NonNull Metadata patronMetadata;
    }

    public static class PatronCredentialsInvalidException extends Exception {}
    public static class PatronAlreadyExistsException extends Exception {}
}
