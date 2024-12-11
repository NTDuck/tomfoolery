package org.tomfoolery.core.usecases.external.administrator.users.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.external.administrator.users.search.abc.SearchUsersByUsernameUseCase;

public final class SearchStaffByUsernameUseCase extends SearchUsersByUsernameUseCase<Staff> {
    public static @NonNull SearchStaffByUsernameUseCase of(@NonNull StaffSearchGenerator staffSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchStaffByUsernameUseCase(staffSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchStaffByUsernameUseCase(@NonNull StaffSearchGenerator staffSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(staffSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
