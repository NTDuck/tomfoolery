package org.tomfoolery.core.dataproviders.generators.users.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public interface UserSearchGenerator<User extends BaseUser> extends BaseGenerator {
    @NonNull List<User> searchByUsername(@NonNull String username);

    default @Nullable Page<User> searchPaginatedByUsername(@NonNull String username, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedUsers = this.searchByUsername(username);
        return Page.fromUnpaginated(unpaginatedUsers, pageIndex, maxPageSize);
    }
}
