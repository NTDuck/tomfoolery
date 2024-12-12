package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredToken;
import com.microsoft.credentialstorage.model.StoredTokenType;
import lombok.Locked;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

@NoArgsConstructor(staticName = "of")
public class SecretStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    public static final boolean PERSIST = true;

    private static final @NonNull String ENTRY_ALIAS = "auth-token";
    private static final @NonNull StoredTokenType STORED_TOKEN_TYPE = StoredTokenType.UNKNOWN;
    private static final StorageProvider.SecureOption SECURE_OPTION = StorageProvider.SecureOption.REQUIRED;

    private final @NonNull SecretStore<StoredToken> secretStore = StorageProvider.getTokenStorage(PERSIST, SECURE_OPTION);

    @Override
    @Locked.Write
    public void save(@NonNull AuthenticationToken authenticationToken) {
        val storedToken = getStoredTokenFromAuthenticationToken(authenticationToken);
        this.secretStore.add(ENTRY_ALIAS, storedToken);
        storedToken.clear();
    }

    @Override
    @Locked.Write
    public void remove() {
        this.secretStore.delete(ENTRY_ALIAS);
    }

    @Override
    @Locked.Read
    public @Nullable AuthenticationToken get() {
        val storedToken = this.secretStore.get(ENTRY_ALIAS);

        if (storedToken == null)
            return null;

        return getAuthenticationTokenFromStoredToken(storedToken);
    }

    @Override
    @Locked.Read
    public boolean contains() {
        return AuthenticationTokenRepository.super.contains();
    }

    private static @NonNull StoredToken getStoredTokenFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val wrappedSerializedPayload = authenticationToken.getSerializedPayload();
        val serializedPayload = wrappedSerializedPayload.getChars();

        return new StoredToken(serializedPayload, STORED_TOKEN_TYPE);
    }

    private static @NonNull AuthenticationToken getAuthenticationTokenFromStoredToken(@NonNull StoredToken storedToken) {
        val serializedPayload = storedToken.getValue();
        val wrappedSerializedPayload = SecureString.of(serializedPayload);

        return AuthenticationToken.of(wrappedSerializedPayload);
    }
}
