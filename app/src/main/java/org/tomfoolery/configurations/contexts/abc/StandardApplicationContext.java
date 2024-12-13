package org.tomfoolery.configurations.contexts.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.core.dataproviders.repositories.documents.RetrievalDocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.LibraryThingEasyLinkingDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.users.authentication.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.okhttp.OkHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika.ApacheTikaFileVerifier;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika.ApacheTikaTemporaryFileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleBooksApiRetrievalDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.openlibrary.documents.OpenLibraryBooksApiRetrievalDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;

import java.util.List;

public abstract class StandardApplicationContext extends ApplicationContext {
    @Override
    protected @NonNull List<RetrievalDocumentRepository> createRetrievalDocumentRepositories() {
        val httpClientProvider = this.getHttpClientProvider();

        return List.of(
            GoogleBooksApiRetrievalDocumentRepository.of(httpClientProvider),
            OpenLibraryBooksApiRetrievalDocumentRepository.of(httpClientProvider)
        );
    }

    @Override
    protected @NonNull DocumentQrCodeGenerator createDocumentQrCodeGenerator() {
        return ZxingDocumentQrCodeGenerator.of();
    }

    @Override
    protected @NonNull DocumentUrlGenerator createDocumentUrlGenerator() {
        return LibraryThingEasyLinkingDocumentUrlGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenGenerator createAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }

    @Override
    protected @NonNull AuthenticationTokenRepository createAuthenticationTokenRepository() {
        val dotenvProvider = this.getDotenvProvider();
        return KeyStoreAuthenticationTokenRepository.of(dotenvProvider);
    }

    @Override
    protected @NonNull PasswordEncoder createPasswordEncoder() {
        return BCryptPasswordEncoder.of();
    }

    @Override
    protected @NonNull DotenvProvider createDotenvProvider() {
        return CdimascioDotenvProvider.of();
    }

    @Override
    protected @NonNull HttpClientProvider createHttpClientProvider() {
        return OkHttpClientProvider.of();
    }

    @Override
    protected @NonNull FileVerifier createFileVerifier() {
        return ApacheTikaFileVerifier.of();
    }

    @Override
    protected @NonNull FileStorageProvider createFileStorageProvider() {
        return ApacheTikaTemporaryFileStorageProvider.of();
    }
}
