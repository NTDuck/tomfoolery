package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.abc;

import lombok.Locked;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.search.abc.UserSearchGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.InMemoryLinearGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryLinearUserSearchGenerator<User extends BaseUser> extends InMemoryLinearGenerator<User, BaseUser.Id> implements UserSearchGenerator<User> {
    public static <User extends BaseUser> @NonNull InMemoryLinearUserSearchGenerator<User> of() {
        return new InMemoryLinearUserSearchGenerator<>();
    }

    protected InMemoryLinearUserSearchGenerator() {
        super(Comparator.comparing(user -> user.getId().getUuid()));
    }

    @Override
    @Locked.Read
    public @NonNull List<User> searchByNormalizedUsername(@NonNull String normalizedUsername) {
        return super.cachedEntities.stream()
            .filter(user -> isSubsequence(normalizedUsername, this.normalize(user.getCredentials().getUsername())))
            .collect(Collectors.toUnmodifiableList());
    }
}
