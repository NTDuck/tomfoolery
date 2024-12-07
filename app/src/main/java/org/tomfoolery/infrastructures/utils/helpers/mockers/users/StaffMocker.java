package org.tomfoolery.infrastructures.utils.helpers.mockers.users;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.utils.helpers.mockers.common.TimestampsMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

@NoArgsConstructor(staticName = "of")
public class StaffMocker extends UserMocker<Staff> {
    private final @NonNull AdministratorMocker administratorMocker = AdministratorMocker.of();
    private final @NonNull TimestampsMocker timestampsMocker = TimestampsMocker.of();

    @Override
    public @NonNull Staff createMockUserWithIdAndCredentials(@NotNull Staff.Id staffId, @NotNull Staff.Credentials credentials) {
        return Staff.of(
            staffId,
            Staff.Audit.of(
                Staff.Audit.Timestamps.of(this.timestampsMocker.createMockTimestamps()),
                this.administratorMocker.createMockEntityId()
            ),
            credentials
        );
    }
}
