package org.tomfoolery.infrastructures.dataproviders.jjwt.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.LocalDateTime;

@NoArgsConstructor(staticName = "of")
public class JJWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {
    private static final @NonNull String USER_ID_CLAIM_LABEL = "uid";
    private static final @NonNull String USER_CLASS_CLAIM_LABEL = "type";
    private static final @NonNull String EXPIRATION_CLAIM_LABEL = "exp";

    @Override
    public @NonNull AuthenticationToken generateToken(ReadonlyUser.@NonNull Id userId, @NonNull Class<? extends ReadonlyUser> userClass, @NonNull LocalDateTime expiryTimestamp) {
        val serializedPayload = Jwts.builder()
            .claim(USER_ID_CLAIM_LABEL, userId)
            .claim(USER_CLASS_CLAIM_LABEL, userClass)
            .claim(EXPIRATION_CLAIM_LABEL, expiryTimestamp)

            .compact();

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

        val tokenExpiryTimestamp = (LocalDateTime) payload.get(EXPIRATION_CLAIM_LABEL);
        return LocalDateTime.now().isBefore(tokenExpiryTimestamp);
    }

    @Override
    public ReadonlyUser.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token) {
        val payload = getPayloadFromAuthenticationToken(token);

        return payload != null
             ? (ReadonlyUser.Id) payload.get(USER_ID_CLAIM_LABEL)
             : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Class<? extends ReadonlyUser> getUserClassFromToken(@NonNull AuthenticationToken token) {
        val payload = getPayloadFromAuthenticationToken(token);

        return payload != null
             ? (Class<? extends ReadonlyUser>) payload.get(USER_CLASS_CLAIM_LABEL)
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
