package org.tomfoolery.infrastructures.dataproviders.base64.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.infrastructures.utils.helpers.Base64Encoder;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(staticName = "of")
public class Base64AuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    @Override
    @SneakyThrows
    public @NonNull AuthenticationToken generateToken(ReadonlyUser.@NonNull Id userId, @NonNull Class<? extends ReadonlyUser> userClass, @NonNull LocalDateTime expiryTimestamp) {
        val payload = Payload.of(userId, userClass, expiryTimestamp);
        val serializedPayload = Base64Encoder.encode(payload);
        return AuthenticationToken.of(serializedPayload);
    }

    @Override
    public void invalidateToken(@NonNull AuthenticationToken token) {

    }

    @Override
    public boolean verifyToken(@NonNull AuthenticationToken token) {
        val payload = getPayloadFromAuthenticationToken(token);

        if (payload == null)
            return false;

        val tokenExpiryTimestamp = payload.getExpiryTimestamp();
        return LocalDateTime.now().isBefore(tokenExpiryTimestamp);
    }

    @Override
    public ReadonlyUser.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token) {
        val payload = getPayloadFromAuthenticationToken(token);

        return payload != null
             ? payload.getUserId()
             : null;
    }

    @Override
    public @Nullable Class<? extends ReadonlyUser> getUserClassFromToken(@NonNull AuthenticationToken token) {
        val payload = getPayloadFromAuthenticationToken(token);

        return payload != null
             ? payload.getUserClass()
             : null;
    }

    private @Nullable Payload getPayloadFromAuthenticationToken(@NonNull AuthenticationToken token) {
        val serializedPayload = token.getSerializedPayload();

        try {
            return (Payload) Base64Encoder.decode(serializedPayload);
        } catch (Exception exception) {
            return null;
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Payload implements Serializable {
        ReadonlyUser.@NonNull Id userId;
        Class<? extends ReadonlyUser> userClass;
        @NonNull LocalDateTime expiryTimestamp;
    }
}
