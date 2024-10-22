package org.tomfoolery.infrastructures.dataproviders.hash.base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.infrastructures.utils.services.Base64Service;

import java.time.LocalDateTime;

@NoArgsConstructor(staticName = "of")
public class Base64AuthenticationTokenService implements AuthenticationTokenService {
    @Override
    public @NonNull <User extends ReadonlyUser> AuthenticationToken generateToken(User.@NonNull Id userId, @NonNull Class<User> userClass, @NonNull LocalDateTime expiryTimestamp) {
        val payload = Payload.of(userId, userClass, expiryTimestamp);
        return AuthenticationToken.of(payload.toString());
    }

    @Override
    public void invalidateToken(@NonNull AuthenticationToken token) {
        val payload = Payload.of(token);

        if (payload == null)
            return;
        
        payload.setExpiryTimestamp(LocalDateTime.now());
        token = AuthenticationToken.of(payload.toString());
    }

    @Override
    public boolean verifyToken(@NonNull AuthenticationToken token) {
        val payload = Payload.of(token);

        if (payload == null)
            return false;

        val tokenExpiryTimestamp = payload.getExpiryTimestamp();
        return LocalDateTime.now().isBefore(tokenExpiryTimestamp);
    }

    @Override
    public <User extends ReadonlyUser> User.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token) {
        val payload = Payload.of(token);

        if (payload == null)
            return null;

        return payload.getUserId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <User extends ReadonlyUser> @Nullable Class<User> getUserClassFromToken(@NonNull AuthenticationToken token) {
        val payload = Payload.of(token);

        if (payload == null)
            return null;

        return (Class<User>) payload.getUserClass();
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Payload {
        private static final @NonNull String SIGNATURE = Base64Service.convertNormalStringToBase64String("tomfoolery");
        private static final char DELIMITER = '.';

        ReadonlyUser.@NonNull Id userId;
        Class<?> userClass;
        @NonNull LocalDateTime expiryTimestamp;

        @Override
        @SneakyThrows
        public @NonNull String toString() {
            return String.join(".",
                Base64Service.convertObjectToBase64String(userId),
                Base64Service.convertObjectToBase64String(userClass),
                Base64Service.convertObjectToBase64String(expiryTimestamp),
                SIGNATURE
            );
        }

        private Payload(@NonNull AuthenticationToken token) throws AuthenticationTokenFormatInvalidException {
            val content = token.getContent();
            val base64Strings = content.split(String.valueOf(DELIMITER));

            try {
                this.userId = (ReadonlyUser.Id) Base64Service.convertBase64StringToObject(base64Strings[0]);
                this.userClass = (Class<?>) Base64Service.convertBase64StringToObject(base64Strings[1]);
                this.expiryTimestamp = (LocalDateTime) Base64Service.convertBase64StringToObject(base64Strings[2]);

            } catch (Exception exception) {
                throw new AuthenticationTokenFormatInvalidException();
            }
        }

        public static @Nullable Payload of(@NonNull AuthenticationToken token) {
            try {
                return new Payload(token);
            } catch (AuthenticationTokenFormatInvalidException exception) {
                return null;
            }
        }

        private static class AuthenticationTokenFormatInvalidException extends Exception {}
    }
}
