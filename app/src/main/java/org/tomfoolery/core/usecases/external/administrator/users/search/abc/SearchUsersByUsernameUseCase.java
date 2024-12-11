package org.tomfoolery.core.usecases.external.administrator.users.search.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.abc.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public class SearchUsersByUsernameUseCase<User extends BaseUser> extends AuthenticatedUserUseCase implements ThrowableFunction<SearchUsersByUsernameUseCase.Request, SearchUsersByUsernameUseCase.Response<User>> {
    private final @NonNull UserSearchGenerator<User> userSearchGenerator;

    protected SearchUsersByUsernameUseCase(@NonNull UserSearchGenerator<User> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.userSearchGenerator = userSearchGenerator;
    }

    @Override
    public @NonNull Response<User> apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val searchTerm = request.getSearchTerm();
        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedUsers = this.getPaginatedUsers(searchTerm, pageIndex, maxPageSize);
        return Response.of(paginatedUsers);
    }

    private @NonNull Page<User> getPaginatedUsers(@NonNull String searchTerm, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedUsers = this.userSearchGenerator.searchPaginatedByUsername(searchTerm, pageIndex, maxPageSize);

        if (paginatedUsers == null)
            throw new PaginationInvalidException();

        return paginatedUsers;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends BaseUser> {
        @NonNull Page<User> paginatedUsers;
    }

    public static class PaginationInvalidException extends Exception {}
}
