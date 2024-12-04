package org.tomfoolery.infrastructures.utils.helpers.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class UserIdBiAdapter {
    public static BaseUser.@NonNull Id parse(@NonNull String userUuid) throws UserUuidInvalidException {
        try {
            val uuid = UUID.fromString(userUuid);
            return BaseUser.Id.of(uuid);

        } catch (IllegalArgumentException exception) {
            throw new UserUuidInvalidException();
        }
    }

    public static @NonNull String serialize(BaseUser.@NonNull Id userId) {
        return userId.getUuid().toString();
    }

    public static class UserUuidInvalidException extends Exception {}
}
