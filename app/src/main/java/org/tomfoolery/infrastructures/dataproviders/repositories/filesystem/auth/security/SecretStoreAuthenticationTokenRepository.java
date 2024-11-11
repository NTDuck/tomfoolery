package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredToken;
import com.microsoft.credentialstorage.model.StoredTokenType;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public class SecretStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    public static final boolean PERSIST = true;

    private static final @NonNull String ENTRY_ALIAS = "auth-token";
    private static final @NonNull StoredTokenType STORED_TOKEN_TYPE = StoredTokenType.UNKNOWN;
    private static final StorageProvider.SecureOption SECURE_OPTION = StorageProvider.SecureOption.REQUIRED;

    private final @NonNull SecretStore<StoredToken> secretStore;

    public static @NonNull SecretStoreAuthenticationTokenRepository of() {
        return new SecretStoreAuthenticationTokenRepository();
    }

    private SecretStoreAuthenticationTokenRepository() {
        this.secretStore = StorageProvider.getTokenStorage(PERSIST, SECURE_OPTION);
    }

    @Override
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val storedToken = getStoredTokenFromAuthenticationToken(authenticationToken);
        this.secretStore.add(ENTRY_ALIAS, storedToken);
        storedToken.clear();
    }

    @Override
    public void removeAuthenticationToken() {
        this.secretStore.delete(ENTRY_ALIAS);
    }

    @Override
    public @Nullable AuthenticationToken getAuthenticationToken() {
        val storedToken = this.secretStore.get(ENTRY_ALIAS);

        if (storedToken == null)
            return null;

        return getAuthenticationTokenFromStoredToken(storedToken);
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
