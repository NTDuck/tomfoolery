package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.core.utils.helpers.adapters.Codec;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.UUID;

public class KeyStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private static final @NonNull String KEYSTORE_TYPE = "pkcs12";
    private static final @NonNull String KEYSTORE_EXTENSION = ".p12";
    private static final @NonNull String KEYSTORE_ENTRY_ALIAS = "auth-token";
    private static final @NonNull String SECRET_KEY_ALGORITHM = "AES";

    private final @NonNull String keyStoreName;
    private final @NonNull String keyStorePassword = UUID.randomUUID().toString();   // KeyStore terminated when application ends

    private final @NonNull KeyStore keyStore;

    public static @NonNull KeyStoreAuthenticationTokenRepository of() {
        return new KeyStoreAuthenticationTokenRepository();
    }

    @SneakyThrows
    private KeyStoreAuthenticationTokenRepository() {
        this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        this.keyStoreName = FileManager.save(KEYSTORE_EXTENSION, new byte[0]);

        try (val fileInputStream = new FileInputStream(this.keyStoreName)) {
            this.loadKeystoreFromInputStream(fileInputStream);
        } catch (Exception exception) {
            this.createEmptyKeystore();
        }
    }

    @Override
    @SneakyThrows
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        val secretKeyEntry = getSecretKeyEntryFromToken(authenticationToken);
        val protectionParameter = this.getProtectionParameter();

        this.keyStore.setEntry(KEYSTORE_ENTRY_ALIAS, secretKeyEntry, protectionParameter);

        this.saveToFile();
    }

    @Override
    @SneakyThrows
    public void removeAuthenticationToken() {
        this.keyStore.deleteEntry(KEYSTORE_ENTRY_ALIAS);

        this.saveToFile();
    }

    @Override
    @SneakyThrows
    public @Nullable AuthenticationToken getAuthenticationToken() {
        val protectionParameter = this.getProtectionParameter();
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

    private char @NonNull [] getPasswordCharArray() {
        return this.keyStorePassword.toCharArray();
    }

    private KeyStore.@NonNull ProtectionParameter getProtectionParameter() {
        val passwordCharArray = this.getPasswordCharArray();
        return new KeyStore.PasswordProtection(passwordCharArray);
    }

    private static KeyStore.@NonNull SecretKeyEntry getSecretKeyEntryFromToken(@NonNull AuthenticationToken token) {
        val wrappedSerializedPayload = token.getSerializedPayload();
        val serializedPayload = wrappedSerializedPayload.getChars();
        val serializedPayloadBytes = Codec.bytesFromChars(serializedPayload);

        val secretKey = new SecretKeySpec(serializedPayloadBytes, SECRET_KEY_ALGORITHM);

        return new KeyStore.SecretKeyEntry(secretKey);
    }

    private static @NonNull AuthenticationToken getAuthenticationTokenFromEntry(KeyStore.@NonNull SecretKeyEntry secretKeyEntry) {
        val secretKey = secretKeyEntry.getSecretKey();

        val serializedPayloadBytes = secretKey.getEncoded();
        val serializedPayload = Codec.charsFromBytes(serializedPayloadBytes);
        val wrappedSerializedPayload = SecureString.of(serializedPayload);

        return AuthenticationToken.of(wrappedSerializedPayload);
    }

    @SneakyThrows
    private void saveToFile() {
        val passwordCharArray = this.getPasswordCharArray();

        try (val fileOutputStream = new FileOutputStream(this.keyStoreName)) {
            this.keyStore.store(fileOutputStream, passwordCharArray);
        }
    }
}
