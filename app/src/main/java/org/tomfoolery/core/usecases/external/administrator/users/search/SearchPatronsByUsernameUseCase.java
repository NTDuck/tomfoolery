package org.tomfoolery.core.usecases.external.administrator.users.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.search.abc.SearchUsersByUsernameUseCase;

public final class SearchPatronsByUsernameUseCase extends SearchUsersByUsernameUseCase<Patron> {
    public static @NonNull SearchPatronsByUsernameUseCase of(@NonNull PatronSearchGenerator patronSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchPatronsByUsernameUseCase(patronSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchPatronsByUsernameUseCase(@NonNull PatronSearchGenerator patronSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(patronSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
