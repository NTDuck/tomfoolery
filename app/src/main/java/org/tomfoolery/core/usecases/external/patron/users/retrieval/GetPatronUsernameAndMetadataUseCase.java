package org.tomfoolery.core.usecases.external.patron.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

import java.util.Set;

public final class GetPatronUsernameAndMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableSupplier<GetPatronUsernameAndMetadataUseCase.Response> {
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull GetPatronUsernameAndMetadataUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronUsernameAndMetadataUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronUsernameAndMetadataUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val patronUsername = patron.getCredentials().getUsername();
        val patronMetadata = patron.getMetadata();

        return Response.of(patronUsername, patronMetadata);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull String patronUsername;
        Patron.@NonNull Metadata patronMetadata;
    }

    public static class PatronNotFoundException extends Exception {}
}
