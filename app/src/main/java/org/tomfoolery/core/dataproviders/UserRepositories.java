package org.tomfoolery.core.dataproviders;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.utils.structs.UserAndRepository;

import java.util.function.Function;
import java.util.function.Predicate;

public interface UserRepositories extends Iterable<UserRepository<?>> {
    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUser(@NonNull User user) {
        return (UserRepository<User>) this.getUserRepositoryByUserClass(user.getClass());
    }

    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUserClass(@NonNull Class<User> userClass) {
        return (UserRepository<User>) this.getUserRepositoryByPredicate(userRepository -> userRepository.getUserClass().equals(userClass));
    }

    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUsername(@NonNull String username) {
        return (UserAndRepository<User>) this.getUserAndRepositoryByUserFunction(userRepository -> userRepository.getByUsername(username));
    }

    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserAndRepository<User> getUserAndRepositoryByUserId(User.@NonNull Id userId) {
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
