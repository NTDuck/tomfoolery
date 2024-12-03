package org.tomfoolery.core.usecases.admin.users.retrieval.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public class GetUserByIdUseCase<User extends BaseUser> extends AuthenticatedUserUseCase implements ThrowableFunction<GetUserByIdUseCase.Request, GetUserByIdUseCase.Response<User>> {
    private final @NonNull UserRepository<User> userRepository;

    protected GetUserByIdUseCase(@NonNull UserRepository<User> userRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.userRepository = userRepository;
    }

    @Override
    public @NonNull Response<User> apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, UserNotFoundException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val userId = request.getUserId();
        val user = this.getUserById(userId);

        return Response.of(user);
    }

    private @NonNull User getUserById( BaseUser.@NonNull Id userId) throws UserNotFoundException {
        val user = this.userRepository.getById(userId);

        if (user == null)
            throw new UserNotFoundException();

        return user;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        BaseUser.@NonNull Id userId;
    }

    @Value(staticConstructor = "of")
    public static class Response<User extends BaseUser> {
        @NonNull User user;
    }

    public static class UserNotFoundException extends Exception {}
}
