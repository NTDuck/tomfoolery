package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.HashMap;
import java.util.Map;

public abstract class InMemoryUserRepository<User extends ReadonlyUser> implements UserRepository<User> {
    private final @NonNull Map<User.Id, User> userIdToUserMap = new HashMap<>();
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
        val userId = user.getId();
        val username = user.getCredentials().getUsername();

        this.userIdToUserMap.put(userId, user);
        this.userNameToUserMap.put(username, user);
    }

    @Override
    public void delete(User.@NonNull Id userId) {
        val deletedUser = this.userIdToUserMap.remove(userId);
        val usernameOfDeletedUser = deletedUser.getCredentials().getUsername();
        this.userNameToUserMap.remove(usernameOfDeletedUser);
    }

    @Override
    public @Nullable User getById(User.@NonNull Id userId) {
        return this.userIdToUserMap.get(userId);
    }

    @Override
    public @NonNull Page<User> showPaginatedEntities(int pageIndex, int pageSize) {
        val pageOffset = Page.getOffset(pageIndex, pageSize);

        val paginatedUsers = this.userIdToUserMap.values().stream()
                .skip(pageOffset)
                .limit(pageSize)
                .toList();

        return Page.of(pageIndex, paginatedUsers);
    }

    @Override
    public boolean contains(ReadonlyUser.@NonNull Id id) {
        return this.userIdToUserMap.containsKey(id);
    }
}
