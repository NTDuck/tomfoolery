package org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor(staticName = "of")
public class JJWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    private static final @NonNull String USER_ID_CLAIM_LABEL = "uid";
    private static final @NonNull String USER_CLASS_CLAIM_LABEL = "type";
    private static final @NonNull String EXPIRATION_CLAIM_LABEL = "expl";

    @Override
    public @NonNull AuthenticationToken generateAuthenticationToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp) {
        val serializedPayload = Jwts.builder()
            .claim(USER_ID_CLAIM_LABEL, userId)   // BaseUser.Id is serializable by default
            .claim(USER_CLASS_CLAIM_LABEL, generateSerializableFromUserClass(userClass))
            .claim(EXPIRATION_CLAIM_LABEL, generateSerializableFromExpiryTimestamp(expiryTimestamp))

            .compact();

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

        val authenticationTokenExpiryTimestamp = getExpiryTimestampFromSerializable(payload.get(EXPIRATION_CLAIM_LABEL));
        return Instant.now().isBefore(authenticationTokenExpiryTimestamp);
    }

    @Override
    public BaseUser.@Nullable Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        if (payload == null)
            return null;

        return (BaseUser.Id) payload.get(USER_ID_CLAIM_LABEL);
    }

    @Override
    @SneakyThrows
    public @Nullable Class<? extends BaseUser> getUserClassFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        if (payload == null)
            return null;

        return getUserClassFromSerializable(payload.get(USER_CLASS_CLAIM_LABEL));
    }

    private static @Nullable Claims getPayloadFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val serializedPayload = authenticationToken.getSerializedPayload();

        return Jwts.parser()
            .unsecured()
            .build()
            .parseUnsecuredClaims(serializedPayload)
            .getPayload();
    }

    private static @NonNull Serializable generateSerializableFromUserClass(@NonNull Class<? extends BaseUser> userClass) {
        return userClass.getName();
    }

    @SneakyThrows
    private static @NonNull Class<? extends BaseUser> getUserClassFromSerializable(@NonNull Object serializable) {
        return Class.forName((String) serializable).asSubclass(BaseUser.class);
    }

    private static @NonNull Serializable generateSerializableFromExpiryTimestamp(@NonNull Instant expiryTimestamp) {
        return expiryTimestamp.toEpochMilli();
    }

    private static @NonNull Instant getExpiryTimestampFromSerializable(@NonNull Object serializable) {
        return Instant.ofEpochMilli((long) serializable);
    }
}
