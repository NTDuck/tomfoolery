package org.tomfoolery.infrastructures.utils.helpers.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.NONE)
public class UserIdAdapter {
    public static @NonNull String getStringFromUserId(BaseUser.@NonNull Id userId) {
        return userId.getValue().toString();
    }

    public static BaseUser.@NonNull Id generateUserIdFromString(@NonNull String rawUserId) {
        return BaseUser.Id.of(UUID.fromString(rawUserId));
    }
}
