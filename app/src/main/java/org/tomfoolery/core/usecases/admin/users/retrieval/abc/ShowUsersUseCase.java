package org.tomfoolery.core.usecases.admin.users.retrieval.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
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

        val paginatedUsers = this.getPaginatedUsers(pageIndex, maxPageSize);
        return Response.of(paginatedUsers);
    }

    private @NonNull Page<User> getPaginatedUsers(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedUsers = this.userRepository.showPaginated(pageIndex, maxPageSize);

        if (paginatedUsers == null)
            throw new PaginationInvalidException();

        return paginatedUsers;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends BaseUser> {
        @NonNull Page<User> paginatedUsers;
    }

    public static class PaginationInvalidException extends Exception {}
}
