package org.tomfoolery.infrastructures.dataproviders.jjwt.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.Instant;

@NoArgsConstructor(staticName = "of")
public class JJWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    private static final @NonNull String USER_ID_CLAIM_LABEL = "uid";
    private static final @NonNull String USER_CLASS_CLAIM_LABEL = "type";
    private static final @NonNull String EXPIRATION_CLAIM_LABEL = "exp";

    @Override
    public @NonNull AuthenticationToken generateAuthenticationToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp) {
        val serializedPayload = Jwts.builder()
                .claim(USER_ID_CLAIM_LABEL, userId)
                .claim(USER_CLASS_CLAIM_LABEL, userClass)
                .claim(EXPIRATION_CLAIM_LABEL, expiryTimestamp)

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

        val tokenExpiryTimestamp = (Instant) payload.get(EXPIRATION_CLAIM_LABEL);
        return Instant.now().isBefore(tokenExpiryTimestamp);
    }

    @Override
    public BaseUser.@Nullable Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        return payload != null
             ? (BaseUser.Id) payload.get(USER_ID_CLAIM_LABEL)
             : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Class<? extends BaseUser> getUserClassFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val payload = getPayloadFromAuthenticationToken(authenticationToken);

        return payload != null
             ? (Class<? extends BaseUser>) payload.get(USER_CLASS_CLAIM_LABEL)
             : null;
    }

    private @Nullable Claims getPayloadFromAuthenticationToken(@NonNull AuthenticationToken token) {
        val serializedPayload = token.getSerializedPayload();

        try {
            return Jwts.parser()
                .build()
                .parseUnsecuredClaims(serializedPayload)
                .getPayload();

        } catch (Exception exception) {
            return null;
        }
    }
}
