package org.tomfoolery.infrastructures.utils.helpers.mockers.users;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.infrastructures.utils.helpers.mockers.common.TimestampsMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

@NoArgsConstructor(staticName = "of")
public class AdministratorMocker extends UserMocker<Administrator> {
    private final @NonNull TimestampsMocker timestampsMocker = TimestampsMocker.of();

    @Override
    public @NonNull Administrator createMockUserWithIdAndCredentials(@NotNull Administrator.Id administratorId, @NotNull Administrator.Credentials credentials) {
        return Administrator.of(
            administratorId,
            Administrator.Audit.of(
                Administrator.Audit.Timestamps.of(this.timestampsMocker.createMockTimestamps())
            ),
            credentials
        );
    }
}
