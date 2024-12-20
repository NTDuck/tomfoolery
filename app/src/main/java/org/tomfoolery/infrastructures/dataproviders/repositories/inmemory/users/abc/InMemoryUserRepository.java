package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.abc;

import lombok.Locked;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.UserComparator;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryUserRepository<User extends BaseUser> extends BaseInMemoryRepository<User, BaseUser.Id> implements UserRepository<User> {
    private final @NonNull Map<String, User> userByUsername = new ConcurrentHashMap<>();

    @Override
    protected @NonNull Comparator<User.Id> getEntityIdComparator() {
        return UserComparator.compareId();
    }

    @Override
    @Locked.Read
    public @Nullable User getByUsername(@NonNull String username) {
        return this.userByUsername.get(username);
    }

    @Override
    @Locked.Read
    public boolean contains(@NonNull String username) {
        return this.userByUsername.containsKey(username);
    }

    @Override
    @Locked.Write
    public void save(@NonNull User user) {
        super.save(user);

        val username = user.getCredentials().getUsername();
        this.userByUsername.put(username, user);
    }

    @Override
    @Locked.Write
    public void delete(User.@NonNull Id userId) {
        val deletedUser = this.entitiesByIds.remove(userId);

        if (deletedUser == null)
            return;

        val usernameOfDeletedUser = deletedUser.getCredentials().getUsername();
        this.userByUsername.remove(usernameOfDeletedUser);
    }
}
