package org.tomfoolery.infrastructures.dataproviders.generators.base64.users.authentication.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.base64.Base64Codec;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor(staticName = "of")
public class Base64AuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    @Override
    @SneakyThrows
    public @NonNull AuthenticationToken generate(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp) {
        val payload = Payload.of(userId, userClass, expiryTimestamp);
        val serializedPayload = Base64Codec.encode(payload);
        val wrappedSerializedPayload = SecureString.of(serializedPayload);

        return AuthenticationToken.of(wrappedSerializedPayload);
    }

    @Override
    public void invalidate(@NonNull AuthenticationToken authenticationToken) {

    }

    @Override
    public boolean verify(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        if (payload == null)
            return false;

        val tokenExpiryTimestamp = payload.getExpiryTimestamp();
        return Instant.now().isBefore(tokenExpiryTimestamp);
    }

    @Override
    public BaseUser.@Nullable Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        return payload != null
             ? payload.getUserId()
             : null;
    }

    @Override
    public @Nullable Class<? extends BaseUser> getUserClassFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        return payload != null
             ? payload.getUserClass()
             : null;
    }

    private @Nullable Payload getPayloadFromAuthenticationToken(@NonNull AuthenticationToken token) {
        val serializedPayload = token.getSerializedPayload();

        try {
            return (Payload) Base64Codec.decode(serializedPayload);
        } catch (Exception exception) {
            return null;
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Payload implements Serializable {
        BaseUser.@NonNull Id userId;
        Class<? extends BaseUser> userClass;
        @NonNull Instant expiryTimestamp;
    }
}
