package org.tomfoolery.core.usecases.external.patron.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.core.utils.helpers.verifiers.users.authentication.security.PasswordVerifier;

import java.util.Set;

public final class DeletePatronAccountUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<DeletePatronAccountUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull DeletePatronAccountUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private DeletePatronAccountUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, PasswordInvalidException, PasswordMismatchException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val rawPatronPassword = request.getRawPatronPassword();
        this.ensurePasswordIsValid(rawPatronPassword);
        this.ensurePasswordsMatch(rawPatronPassword, patron);

        val patronId = patron.getId();
        this.patronRepository.delete(patronId);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensurePasswordIsValid(@NonNull SecureString rawPatronPassword) throws PasswordInvalidException {
        if (!PasswordVerifier.verify(rawPatronPassword))
            throw new PasswordInvalidException();
    }

    private void ensurePasswordsMatch(@NonNull SecureString rawPatronPassword, @NonNull Patron patron) throws PasswordMismatchException {
        val patronEncodedCredentials = patron.getCredentials();
        val patronEncodedPassword = patronEncodedCredentials.getPassword();

        if (!this.passwordEncoder.verify(rawPatronPassword, patronEncodedPassword))
            throw new PasswordMismatchException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull SecureString rawPatronPassword;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PasswordInvalidException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
}
