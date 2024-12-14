package org.tomfoolery.core.usecases.external.administrator.users.retrieval;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.abc.GetUserByIdUseCase;

public final class GetPatronByIdUseCase extends GetUserByIdUseCase<Patron> {
    public static @NonNull GetPatronByIdUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronByIdUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronByIdUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
