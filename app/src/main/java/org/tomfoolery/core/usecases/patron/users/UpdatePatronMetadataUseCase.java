package org.tomfoolery.core.usecases.patron.users;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.helpers.verifiers.auth.patron.AddressVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.auth.patron.DateOfBirthVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.auth.patron.EmailVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.auth.patron.PhoneNumberVerifier;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class UpdatePatronMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdatePatronMetadataUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull UpdatePatronMetadataUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdatePatronMetadataUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdatePatronMetadataUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, AddressInvalidException, DateOfBirthInvalidException, EmailInvalidException, PhoneNumberInvalidException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val newPatronMetadata = request.getNewPatronMetadata();
        this.ensurePatronMetadataIsValid(newPatronMetadata);
        this.updatePatronMetadata(patron, newPatronMetadata);

        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
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

    private void updatePatronMetadata(@NonNull Patron patron, Patron.@NonNull Metadata newPatronMetadata) {
        patron.setMetadata(newPatronMetadata);

        val patronAuditTimestamps = patron.getAudit().getTimestamps();
        patronAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Metadata newPatronMetadata;
    }

    public static class AddressInvalidException extends PatronMetadataInvalidException {}
    public static class DateOfBirthInvalidException extends PatronMetadataInvalidException {}
    public static class EmailInvalidException extends PatronMetadataInvalidException {}
    public static class PhoneNumberInvalidException extends PatronMetadataInvalidException {}

    public static class PatronMetadataInvalidException extends RuntimeException {}
    public static class PatronNotFoundException extends Exception {}
}
