package org.tomfoolery.core.utils.containers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.UserAndRepository;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor(staticName = "of")
public class UserRepositories implements Iterable<UserRepository<?>> {
    private final @NonNull Set<UserRepository<?>> userRepositories;
    @Override
    public @NonNull Iterator<UserRepository<?>> iterator() {
        return this.userRepositories.iterator();
    }

    @SuppressWarnings("unchecked")
    public <User extends BaseUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) {
        return (UserAndRepository<User>) this.getUserAndRepositoryByUserFunction(userRepository -> userRepository.getByUsername(username));
    }

    @SuppressWarnings("unchecked")
    public <User extends BaseUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUserId(User.@NonNull Id userId) {
        return (UserAndRepository<User>) this.getUserAndRepositoryByUserFunction(userRepository -> userRepository.getById(userId));
    }

    private @Nullable UserRepository<?> getUserRepositoryByPredicate(@NonNull Predicate<UserRepository<?>> predicate) {
        for (val userRepository : this)
            if (predicate.test(userRepository))
                return userRepository;

        return null;
    }

    @SuppressWarnings("unchecked")
    private <User extends BaseUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUserFunction(@NonNull Function<@NonNull UserRepository<?>, @Nullable User> userRepositoryToUser) {
        for (val userRepository : this) {
            val user = userRepositoryToUser.apply(userRepository);

            if (user != null)
                return UserAndRepository.of(user, (UserRepository<User>) userRepository);
        }

        return null;
    }
}
