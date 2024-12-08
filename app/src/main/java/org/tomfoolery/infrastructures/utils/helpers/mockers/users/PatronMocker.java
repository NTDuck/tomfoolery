package org.tomfoolery.infrastructures.utils.helpers.mockers.users;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.utils.helpers.mockers.common.TimestampsMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import java.time.ZoneId;

@NoArgsConstructor(staticName = "of")
public class PatronMocker extends UserMocker<Patron> {
    private final @NonNull TimestampsMocker timestampsMocker = TimestampsMocker.of();

    @Override
    public @NonNull Patron createMockUserWithIdAndCredentials(@NotNull Patron.Id patronId, @NotNull Patron.Credentials credentials) {
        val firstName = this.faker.leagueOfLegends().champion();
        val lastName = this.faker.leagueOfLegends().summonerSpell();
        val dateOfBirth = this.faker.date().birthday()
            .toInstant().atZone(ZoneId.systemDefault())
            .toLocalDate();
        val phoneNumber = this.faker.phoneNumber().phoneNumber();
        val city = this.faker.address().city();
        val country = this.faker.address().country();
        val email = this.faker.internet().emailAddress();

        return Patron.of(
            patronId,
            Patron.Audit.of(
                Patron.Audit.Timestamps.of(this.timestampsMocker.createMockTimestamps())
            ),
            credentials,
            Patron.Metadata.of(
                Patron.Metadata.Name.of(firstName, lastName),
                dateOfBirth,
                phoneNumber,
                Patron.Metadata.Address.of(city, country),
                email
            )
        );
    }
}
