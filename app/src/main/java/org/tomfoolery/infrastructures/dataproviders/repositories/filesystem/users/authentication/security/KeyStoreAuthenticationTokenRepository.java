package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security;

import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.core.utils.helpers.adapters.Codec;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.CompletableFuture;

public class KeyStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private static final @NonNull String KEYSTORE_TYPE = "pkcs12";
    private static final @NonNull String KEYSTORE_NAME = "tomfoolery.p12";
    private static final @NonNull String KEYSTORE_PASSWORD_DOTENV_KEY = "KEYSTORE_PASSWORD";
    private static final @NonNull String KEYSTORE_ENTRY_ALIAS = "auth-token";
    private static final @NonNull String SECRET_KEY_ALGORITHM = "AES";

    private final @NonNull DotenvProvider dotenvProvider;
    private final @NonNull KeyStore keyStore;

    public static @NonNull KeyStoreAuthenticationTokenRepository of(@NonNull DotenvProvider dotenvProvider) {
        return new KeyStoreAuthenticationTokenRepository(dotenvProvider);
    }

    @SneakyThrows
    private KeyStoreAuthenticationTokenRepository(@NonNull DotenvProvider dotenvProvider) {
        this.dotenvProvider = dotenvProvider;
        this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        try (val fileInputStream = new FileInputStream(KEYSTORE_NAME)) {
            this.loadKeystoreFromInputStream(fileInputStream);
        } catch (Exception exception) {
            this.createEmptyKeystore();
        }
    }

    @Override
    @SneakyThrows
    public void save(@NonNull AuthenticationToken authenticationToken) {
        val secretKeyEntry = getSecretKeyEntryFromToken(authenticationToken);
        val protectionParameter = this.getProtectionParameter();

        this.keyStore.setEntry(KEYSTORE_ENTRY_ALIAS, secretKeyEntry, protectionParameter);

        CompletableFuture.runAsync(this::saveToFile);
    }

    @Override
    @SneakyThrows
    public void remove() {
        this.keyStore.deleteEntry(KEYSTORE_ENTRY_ALIAS);

        CompletableFuture.runAsync(this::saveToFile);
    }

    @Override
    @SneakyThrows
    public @Nullable AuthenticationToken get() {
        val protectionParameter = this.getProtectionParameter();
        val entry = (KeyStore.SecretKeyEntry) this.keyStore.getEntry(KEYSTORE_ENTRY_ALIAS, protectionParameter);

        if (entry == null)
            return null;

        return getAuthenticationTokenFromEntry(entry);
    }

    @Override
    @SneakyThrows
    public boolean contains() {
        return this.keyStore.containsAlias(KEYSTORE_ENTRY_ALIAS);
    }

    @SneakyThrows
    private void loadKeystoreFromInputStream(@NonNull InputStream inputStream) {
        val passwordCharArray = this.getPasswordCharArray();
        this.keyStore.load(inputStream, passwordCharArray);
    }

    @SneakyThrows
    private void createEmptyKeystore() {
        val passwordCharArray = this.getPasswordCharArray();
        this.keyStore.load(null, passwordCharArray);
    }

    private char @NonNull [] getPasswordCharArray() {
        val passwordCharSequence = this.dotenvProvider.get(KEYSTORE_PASSWORD_DOTENV_KEY);
        assert passwordCharSequence != null;

        return Codec.charsFromCharSequence(passwordCharSequence);
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

        try (val fileOutputStream = new FileOutputStream(KEYSTORE_NAME)) {
            this.keyStore.store(fileOutputStream, passwordCharArray);
        }
    }
}
