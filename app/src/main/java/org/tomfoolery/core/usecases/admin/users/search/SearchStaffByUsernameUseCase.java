package org.tomfoolery.core.usecases.admin.users.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.admin.users.search.abc.SearchUsersByUsernameUseCase;

public final class SearchStaffByUsernameUseCase extends SearchUsersByUsernameUseCase {
    public static @NonNull SearchStaffByUsernameUseCase of(@NonNull UserSearchGenerator<Patron> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchStaffByUsernameUseCase(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchStaffByUsernameUseCase(@NonNull UserSearchGenerator<Patron> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
