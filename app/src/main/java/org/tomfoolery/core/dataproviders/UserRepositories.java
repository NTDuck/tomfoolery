package org.tomfoolery.core.dataproviders;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.util.SequencedCollection;
import java.util.function.Predicate;

public interface UserRepositories extends SequencedCollection<UserRepository<?>> {
    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUser(@NonNull User user) {
        return (UserRepository<User>) this.getUserRepositoryByUserClass(user.getClass());
    }

    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUserClass(@NonNull Class<User> userClass) {
        return (UserRepository<User>) this.getUserRepositoryByPredicate(userRepository -> userRepository.getUserClass().equals(userClass));
    }

    @SuppressWarnings("unchecked")
    default <User extends ReadonlyUser> @Nullable UserRepository<User> getUserRepositoryByUsername(@NonNull String username) {
        return (UserRepository<User>) this.getUserRepositoryByPredicate(userRepository -> userRepository.getByUsername(username) != null);
    }

    private @Nullable UserRepository<?> getUserRepositoryByPredicate(@NonNull Predicate<UserRepository<?>> predicate) {
        for (val userRepository : this)
            if (predicate.test(userRepository))
                return userRepository;

        return null;
    }
}
