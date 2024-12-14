package org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.users.abc;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.containers.relations.BiRelationRepositories;
import org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.abc.BaseSynchronizedRepository;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.containers.relations.UniRelationRepositories;

import java.util.List;

public class SynchronizedUserRepository<User extends BaseUser> extends BaseSynchronizedRepository<User, BaseUser.Id> implements UserRepository<User> {
    public static <User extends BaseUser> @NonNull SynchronizedUserRepository<User> of(@NonNull UserRepository<User> userRepository, @NonNull List<? extends BaseSynchronizedGenerator<User, BaseUser.Id>> synchronizedUserGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedUserRepository<>(userRepository, synchronizedUserGenerators, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedUserRepository(@NonNull UserRepository<User> userRepository, @NonNull List<? extends BaseSynchronizedGenerator<User, BaseUser.Id>> synchronizedUserGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(
            userRepository, synchronizedUserGenerators,
            UniRelationRepositories.of(List.of()),
            BiRelationRepositories.of(List.of(), List.of(borrowingSessionRepository, reviewRepository))
        );
    }

    @Override
    @Locked.Read
    public @Nullable User getByUsername(@NonNull String username) {
        return ((UserRepository<User>) this.repository).getByUsername(username);
    }

    @Override
    @Locked.Read
    public boolean contains(@NonNull String username) {
        return ((UserRepository<User>) this.repository).contains(username);
    }
}
