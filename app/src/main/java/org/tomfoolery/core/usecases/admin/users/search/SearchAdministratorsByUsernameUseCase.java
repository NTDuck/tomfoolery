package org.tomfoolery.core.usecases.admin.users.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.admin.users.search.abc.SearchUsersByUsernameUseCase;

public final class SearchAdministratorsByUsernameUseCase extends SearchUsersByUsernameUseCase<Administrator> {
    public static @NonNull SearchAdministratorsByUsernameUseCase of(@NonNull UserSearchGenerator<Administrator> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchAdministratorsByUsernameUseCase(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchAdministratorsByUsernameUseCase(@NonNull UserSearchGenerator<Administrator> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
