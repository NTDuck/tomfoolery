package org.tomfoolery.infrastructures.dataproviders.base64.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.infrastructures.utils.helpers.Base64Encoder;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(staticName = "of")
public class Base64AuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    @Override
    @SneakyThrows
    public @NonNull AuthenticationToken generateToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull LocalDateTime expiryTimestamp) {
        val payload = Payload.of(userId, userClass, expiryTimestamp);
        val serializedPayload = Base64Encoder.encode(payload);
        return AuthenticationToken.of(serializedPayload);
    }

    @Override
    public void invalidateAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {

    }

    @Override
    public boolean verifyAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        if (payload == null)
            return false;

        val tokenExpiryTimestamp = payload.getExpiryTimestamp();
        return LocalDateTime.now().isBefore(tokenExpiryTimestamp);
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
            return (Payload) Base64Encoder.decode(serializedPayload);
        } catch (Exception exception) {
            return null;
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Payload implements Serializable {
        BaseUser.@NonNull Id userId;
        Class<? extends BaseUser> userClass;
        @NonNull LocalDateTime expiryTimestamp;
    }
}
