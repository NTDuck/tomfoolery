package org.tomfoolery.core.usecases.admin.users.retrieval;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.admin.users.retrieval.abc.ShowUsersUseCase;

public final class ShowAdministratorAccountsUseCase extends ShowUsersUseCase<Administrator> {
    public static @NonNull ShowAdministratorAccountsUseCase of(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowAdministratorAccountsUseCase(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowAdministratorAccountsUseCase(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
