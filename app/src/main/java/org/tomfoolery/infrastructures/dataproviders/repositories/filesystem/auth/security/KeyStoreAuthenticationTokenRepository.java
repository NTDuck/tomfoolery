package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class KeyStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private static final @NonNull String KEYSTORE_NAME = ".tomfoolery.p12";
    private static final @NonNull String KEYSTORE_TYPE = "pkcs12";
    private static final @NonNull String KEYSTORE_PASSWORD = "lethality-yorick";
    private static final @NonNull String KEYSTORE_ENTRY_ALIAS = "auth-token";

    private static final @NonNull String SECRET_KEY_ALGORITHM = "AES";

    private final @NonNull KeyStore keyStore;

    public static @NonNull KeyStoreAuthenticationTokenRepository of() {
        return new KeyStoreAuthenticationTokenRepository();
    }

    @SneakyThrows
    private KeyStoreAuthenticationTokenRepository() {
        this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        try (val fileInputStream = new FileInputStream(KEYSTORE_NAME)) {
            loadKeystoreFromInputStream(fileInputStream);
        } catch (FileNotFoundException exception) {
            createEmptyKeystore();
        }
    }

    @Override
    @SneakyThrows
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val secretKeyEntry = getSecretKeyEntryFromToken(authenticationToken);
        val protectionParameter = getProtectionParameter();

        this.keyStore.setEntry(KEYSTORE_ENTRY_ALIAS, secretKeyEntry, protectionParameter);

        saveToFile();
    }

    @Override
    @SneakyThrows
    public void removeAuthenticationToken() {
        this.keyStore.deleteEntry(KEYSTORE_ENTRY_ALIAS);

        saveToFile();
    }

    @Override
    @SneakyThrows
    public @Nullable AuthenticationToken getAuthenticationToken() {
        val protectionParameter = getProtectionParameter();
        val entry = (KeyStore.SecretKeyEntry) this.keyStore.getEntry(KEYSTORE_ENTRY_ALIAS, protectionParameter);

        if (entry == null)
            return null;

        return getAuthenticationTokenFromEntry(entry);
    }

    @Override
    @SneakyThrows
    public boolean containsAuthenticationToken() {
        return this.keyStore.containsAlias(KEYSTORE_ENTRY_ALIAS);
    }

    @SneakyThrows
    private void loadKeystoreFromInputStream(@NonNull InputStream inputStream) {
        val passwordCharArray = getPasswordCharArray();
        this.keyStore.load(inputStream, passwordCharArray);
    }

    @SneakyThrows
    private void createEmptyKeystore() {
        val passwordCharArray = getPasswordCharArray();
        this.keyStore.load(null, passwordCharArray);
    }

    private static char @NonNull [] getPasswordCharArray() {
        return KEYSTORE_PASSWORD.toCharArray();
    }

    private static KeyStore.@NonNull ProtectionParameter getProtectionParameter() {
        val passwordCharArray = getPasswordCharArray();
        return new KeyStore.PasswordProtection(passwordCharArray);
    }

    private static KeyStore.@NonNull SecretKeyEntry getSecretKeyEntryFromToken(@NonNull AuthenticationToken token) {
        val serializedPayload = token.getSerializedPayload();
        val secretKey = new SecretKeySpec(serializedPayload.getBytes(), SECRET_KEY_ALGORITHM);
        return new KeyStore.SecretKeyEntry(secretKey);
    }

    private static @NonNull AuthenticationToken getAuthenticationTokenFromEntry(KeyStore.@NonNull SecretKeyEntry secretKeyEntry) {
        val secretKey = secretKeyEntry.getSecretKey();
        val serializedPayload = new String(secretKey.getEncoded());
        return AuthenticationToken.of(serializedPayload);
    }

    @SneakyThrows
    private void saveToFile() {
        val passwordCharArray = getPasswordCharArray();

        try (val fileOutputStream = new FileOutputStream(KEYSTORE_NAME)) {
            this.keyStore.store(fileOutputStream, passwordCharArray);
        }
    }
}
