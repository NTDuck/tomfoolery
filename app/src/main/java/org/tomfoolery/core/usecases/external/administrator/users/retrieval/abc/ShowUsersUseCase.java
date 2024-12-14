package org.tomfoolery.core.usecases.external.administrator.users.retrieval.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public class ShowUsersUseCase<User extends BaseUser> extends AuthenticatedUserUseCase implements ThrowableFunction<ShowUsersUseCase.Request, ShowUsersUseCase.Response<User>> {
    private final @NonNull UserRepository<User> userRepository;

    protected ShowUsersUseCase(@NonNull UserRepository<User> userRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.userRepository = userRepository;
    }

    @Override
    public @NonNull Response<User> apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedUsers = this.getUsersPage(pageIndex, maxPageSize);
        return Response.of(paginatedUsers);
    }

    private @NonNull Page<User> getUsersPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val usersPage = this.userRepository.showPage(pageIndex, maxPageSize);

        if (usersPage == null)
            throw new PaginationInvalidException();

        return usersPage;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends BaseUser> {
        @NonNull Page<User> usersPage;
    }

    public static class PaginationInvalidException extends Exception {}
}
