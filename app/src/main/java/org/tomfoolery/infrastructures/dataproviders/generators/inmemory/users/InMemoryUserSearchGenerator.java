package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.InMemoryLinearGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryUserSearchGenerator<User extends BaseUser> extends InMemoryLinearGenerator<User, BaseUser.Id> implements UserSearchGenerator<User> {
    public static <User extends BaseUser> @NonNull InMemoryUserSearchGenerator<User> of() {
        return new InMemoryUserSearchGenerator<>();
    }

    protected InMemoryUserSearchGenerator() {
        super(Comparator.comparing(user -> user.getId().getUuid()));
    }

    @Override
    public @NonNull List<User> searchByUsername(@NonNull String username) {
        return super.cachedEntities.parallelStream()
            .filter(user -> isSubsequence(username, user.getCredentials().getUsername()))
            .collect(Collectors.toUnmodifiableList());
    }
}
