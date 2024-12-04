package org.tomfoolery.core.usecases.administrator.users.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.search.abc.SearchUsersByUsernameUseCase;

public final class SearchStaffByUsernameUseCase extends SearchUsersByUsernameUseCase<Staff> {
    public static @NonNull SearchStaffByUsernameUseCase of(@NonNull UserSearchGenerator<Staff> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchStaffByUsernameUseCase(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchStaffByUsernameUseCase(@NonNull UserSearchGenerator<Staff> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
