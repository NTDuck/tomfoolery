package org.tomfoolery.core.utils.containers;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.structs.UserAndRepository;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class UserRepositories implements Iterable<UserRepository<?>> {
    private final @NonNull Iterable<UserRepository<?>> userRepositories;

    private UserRepositories(@NonNull UserRepository<?>... userRepositories) {
        this.userRepositories = List.of(userRepositories);
    }

    public static @NonNull UserRepositories of(@NonNull UserRepository<?>... userRepositories) {
        return new UserRepositories(userRepositories);
    }

    @Override
    public @NonNull Iterator<UserRepository<?>> iterator() {
        return this.userRepositories.iterator();
    }

    @SuppressWarnings("unchecked")
    public <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUser(@NonNull User user) {
        return (UserRepository<User>) this.getUserRepositoryByUserClass(user.getClass());
    }

    @SuppressWarnings("unchecked")
    public <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUserClass(@NonNull Class<User> userClass) {
        return (UserRepository<User>) this.getUserRepositoryByPredicate(userRepository -> userRepository.getUserClass().equals(userClass));
    }

    @SuppressWarnings("unchecked")
    public <User extends ReadonlyUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) {
        return (UserAndRepository<User>) this.getUserAndRepositoryByUserFunction(userRepository -> userRepository.getByUsername(username));
    }

    @SuppressWarnings("unchecked")
    public <User extends ReadonlyUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUserId(User.@NonNull Id userId) {
        return (UserAndRepository<User>) this.getUserAndRepositoryByUserFunction(userRepository -> userRepository.getById(userId));
    }

    private @Nullable UserRepository<?> getUserRepositoryByPredicate(@NonNull Predicate<UserRepository<?>> predicate) {
        for (val userRepository : this)
            if (predicate.test(userRepository))
                return userRepository;

        return null;
    }

    @SuppressWarnings("unchecked")
    private <User extends ReadonlyUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUserFunction(@NonNull Function<@NonNull UserRepository<?>, @Nullable User> userFunction) {
        for (val userRepository : this) {
            val user = userFunction.apply(userRepository);

            if (user != null)
                return UserAndRepository.of(user, (UserRepository<User>) userRepository);
        }

        return null;
    }
}
