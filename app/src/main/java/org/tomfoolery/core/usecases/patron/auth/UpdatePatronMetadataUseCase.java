package org.tomfoolery.core.usecases.patron.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class UpdatePatronMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdatePatronMetadataUseCase.Request> {
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull UpdatePatronMetadataUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository) {
        return new UpdatePatronMetadataUseCase(authenticationTokenGenerator, authenticationTokenRepository, patronRepository);
    }

    private UpdatePatronMetadataUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PatronRepository patronRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);

        val newPatronMetadata = request.getNewPatronMetadata();
        updatePatronMetadata(patron, newPatronMetadata);

        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private static void updatePatronMetadata(@NonNull Patron patron, Patron.@NonNull Metadata newPatronMetadata) {
        patron.setMetadata(newPatronMetadata);

        val patronAudit = patron.getAudit();
        val patronAuditTimestamps = patronAudit.getTimestamps();
        patronAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Patron.@NonNull Metadata newPatronMetadata;
    }

    public static class PatronNotFoundException extends Exception {}
}
