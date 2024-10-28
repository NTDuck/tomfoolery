package org.tomfoolery.infrastructures.dataproviders.filesystem;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

public class KeyStoreAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private static final @NonNull String KEYSTORE_NAME = ".tomfoolery";
    private static final @Nullable String KEYSTORE_PASSWORD = null;
    private static final @NonNull String KEYSTORE_KEY_ALIAS = "hehe";
    private static final @NonNull Charset SERIALIZATION_CHARSET = StandardCharsets.UTF_8;
    private static final @NonNull String SECRET_KEY_ALGORITHM = "AES";

    private final @NonNull KeyStore keyStore;

    @SneakyThrows
    private KeyStoreAuthenticationTokenRepository() {
        val keyStoreType = KeyStore.getDefaultType();
        val keyStorePassword = getKeyStorePassword();

        @Cleanup
        val fileInputStream = new FileInputStream(KEYSTORE_NAME);

        this.keyStore = KeyStore.getInstance(keyStoreType);
        this.keyStore.load(fileInputStream, keyStorePassword);
    }

    public static @NonNull KeyStoreAuthenticationTokenRepository of() {
        return new KeyStoreAuthenticationTokenRepository();
    }

    @Override
    @SneakyThrows
    public void saveToken(@NonNull AuthenticationToken token) {
        val serializedPayload = token.getSerializedPayload();
        val serializedPayloadBytes = serializedPayload.getBytes(SERIALIZATION_CHARSET);
        val secretKey = new SecretKeySpec(serializedPayloadBytes, SECRET_KEY_ALGORITHM);

        val keyStorePassword = getKeyStorePassword();

        this.keyStore.setKeyEntry(KEYSTORE_KEY_ALIAS, secretKey, keyStorePassword, null);
    }

    @Override
    @SneakyThrows
    public void removeToken() {
        if (this.keyStore.containsAlias(KEYSTORE_KEY_ALIAS))
            this.keyStore.deleteEntry(KEYSTORE_KEY_ALIAS);
    }

    @Override
    @SneakyThrows
    public @Nullable AuthenticationToken getToken() {
        val keyStorePassword = getKeyStorePassword();
        val secretKey = (SecretKey) this.keyStore.getKey(KEYSTORE_KEY_ALIAS, keyStorePassword);

        if (secretKey == null)
            return null;

        val serializedPayloadBytes = secretKey.getEncoded();
        val serializedPayload = new String(serializedPayloadBytes, SERIALIZATION_CHARSET);

        return AuthenticationToken.of(serializedPayload);
    }

    @Override
    @SneakyThrows
    public boolean containsToken() {
        return this.keyStore.containsAlias(KEYSTORE_KEY_ALIAS);
    }

    private static char @Nullable [] getKeyStorePassword() {
        return KEYSTORE_PASSWORD != null
            ? KEYSTORE_PASSWORD.toCharArray()
            : null;
    }

    @SneakyThrows
    private void saveKeyStore() {
        val keyStorePassword = getKeyStorePassword();

        @Cleanup
        val fileOutputStream = new FileOutputStream(KEYSTORE_NAME);

        this.keyStore.store(fileOutputStream, keyStorePassword);
    }
}
