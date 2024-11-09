package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.HashMap;
import java.util.Map;

public abstract class InMemoryUserRepository<User extends BaseUser> extends BaseInMemoryRepository<User, BaseUser.Id> implements UserRepository<User> {
    private final @NonNull Map<String, User> userNameToUserMap = new HashMap<>();

    @Override
    public @Nullable User getByUsername(@NonNull String username) {
        return this.userNameToUserMap.get(username);
    }

    @Override
    public boolean contains(@NonNull String username) {
        return this.userNameToUserMap.containsKey(username);
    }

    @Override
    public void save(@NonNull User user) {
        super.save(user);

        val username = user.getCredentials().getUsername();
        this.userNameToUserMap.put(username, user);
    }

    @Override
    public void delete(User.@NonNull Id userId) {
        val deletedUser = this.entityIdToEntityMap.remove(userId);
        val usernameOfDeletedUser = deletedUser.getCredentials().getUsername();
        this.userNameToUserMap.remove(usernameOfDeletedUser);
    }
}
