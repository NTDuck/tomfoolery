package org.tomfoolery.core.usecases.external.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.abc.User;
import org.tomfoolery.core.utils.services.CredentialsVerificationService;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class CreatePatronAccountUseCase implements ThrowableConsumer<CreatePatronAccountUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordService passwordService;

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
        if (!CredentialsVerificationService.verifyCredentials(patronCredentials))
            throw new PatronCredentialsInvalidException();
    }

    private void ensurePatronDoesNotExist(Patron.@NonNull Credentials patronCredentials) throws PatronAlreadyExistsException {
        val patronUsername = patronCredentials.getUsername();

        if (this.patronRepository.contains(patronUsername))
            throw new PatronAlreadyExistsException();
    }

    private void encodePatronPassword(Patron.@NonNull Credentials patronCredentials) {
        val password = patronCredentials.getPassword();
        val encodedPassword = this.passwordService.encodePassword(password);
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
