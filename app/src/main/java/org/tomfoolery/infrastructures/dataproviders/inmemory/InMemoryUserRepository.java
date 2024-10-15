package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InMemoryUserRepository<User extends ReadonlyUser> implements UserRepository<User> {
    private final @NonNull Map<User.Id, User> userIdToUserMap = new HashMap<>();
    private final @NonNull Map<String, User> userNameToUserMap = new HashMap<>();

    @Override
    public void save(@NonNull User user) {
        val userId = user.getId();
        val username = getUsername(user);

        this.userIdToUserMap.put(userId, user);
        this.userNameToUserMap.put(username, user);
    }

    @Override
    public void delete(ReadonlyUser.@NonNull Id id) {
        val user = this.userIdToUserMap.remove(id);

        if (user != null)
            this.userNameToUserMap.remove(getUsername(user));
    }

    @Override
    public @Nullable User getById(ReadonlyUser.Id id) {
        return this.userIdToUserMap.get(id);
    }

    @Override
    public @NonNull Collection<User> show() {
        return this.userIdToUserMap.values();
    }

    @Override
    public boolean contains(User.@NonNull Id id) {
        return this.userIdToUserMap.containsKey(id);
    }

    @Override
    public @Nullable User getByUsername(@NonNull String username) {
        return this.userNameToUserMap.get(username);
    }

    @Override
    public boolean contains(@NonNull String username) {
        return this.userNameToUserMap.containsKey(username);
    }

    private static <User extends ReadonlyUser> @NonNull String getUsername(@NonNull User user) {
        return user.getCredentials().getUsername();
    }
}
