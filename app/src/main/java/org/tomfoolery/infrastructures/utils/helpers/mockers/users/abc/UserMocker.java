package org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc;

import com.github.javafaker.Faker;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;

import java.util.UUID;

public abstract class UserMocker<User extends BaseUser> implements EntityMocker<User, BaseUser.Id> {
    private static final @Unsigned int MIN_PASSWORD_LENGTH = 8;
    private static final @Unsigned int MAX_PASSWORD_LENGTH = 32;

    protected final @NonNull Faker faker = Faker.instance();

    @Override
    public User.@NonNull Id createMockEntityId() {
        val rawUuid = this.faker.internet().uuid();
        val uuid = UUID.fromString(rawUuid);

        return User.Id.of(uuid);
    }

    @Override
    public @NonNull User createMockEntityWithId(User.@NonNull Id userId) {
        val mockUsername = this.createMockUsername();
        val mockPassword = this.createMockPassword();

        val mockCredentials = User.Credentials.of(mockUsername, mockPassword);

        return this.createMockUserWithIdAndCredentials(userId, mockCredentials);
    }

    public abstract @NonNull User createMockUserWithIdAndCredentials(User.@NonNull Id userId, User.@NonNull Credentials credentials);

    private @NonNull String createMockUsername() {
        return String.format("%s.%s",
            this.faker.leagueOfLegends().champion(),
            this.faker.leagueOfLegends().rank()
        ).replace(" ", "");
    }

    private @NonNull SecureString createMockPassword() {
        val rawPassword = this.faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
        return SecureString.of(rawPassword.toCharArray());
    }
}
