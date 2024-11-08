package org.tomfoolery.infrastructures.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.UUID;

@Value
public class ViewableUserId {
    @NonNull String value;

    public static @NonNull ViewableUserId of(BaseUser.@NonNull Id userId) {
        return new ViewableUserId(userId);
    }

    private ViewableUserId(BaseUser.@NonNull Id userId) {
        this.value = userId.getValue().toString();
    }

    public BaseUser.@NonNull Id toUserId() {
        return BaseUser.Id.of(UUID.fromString(this.value));
    }
}
