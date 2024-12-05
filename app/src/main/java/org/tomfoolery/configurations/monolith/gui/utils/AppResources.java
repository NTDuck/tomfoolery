package org.tomfoolery.configurations.monolith.gui.utils;

import lombok.Getter;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.ConsoleIOProvider;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.ApacheHttpClientDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.users.authentication.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates.InMemoryReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.InMemoryUserSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references.ZxingDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.builtin.BuiltinHttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents.SynchronizedDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.SynchronizedStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

import java.util.List;
import java.util.Set;

@Getter
public class AppResources implements AutoCloseable {
    protected final @NonNull IOProvider ioProvider = ConsoleIOProvider.of();
    protected final @NonNull HttpClientProvider httpClientProvider = BuiltinHttpClientProvider.of();

    protected final @NonNull DocumentSearchGenerator documentSearchGenerator = InMemoryIndexedDocumentSearchGenerator.of();
    protected final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryIndexedDocumentRecommendationGenerator.of();

    protected final @NonNull UserSearchGenerator<Administrator> administratorSearchGenerator = InMemoryUserSearchGenerator.of();
    protected final @NonNull UserSearchGenerator<Patron> patronSearchGenerator = InMemoryUserSearchGenerator.of();
    protected final @NonNull UserSearchGenerator<Staff> staffSearchGenerator = InMemoryUserSearchGenerator.of();

    protected final @NonNull DocumentContentRepository documentContentRepository = InMemoryDocumentContentRepository.of();
    protected final @NonNull ReviewRepository reviewRepository = InMemoryReviewRepository.of();
    protected final @NonNull BorrowingSessionRepository borrowingSessionRepository = InMemoryBorrowingSessionRepository.of();

    protected final @NonNull DocumentRepository documentRepository = HybridDocumentRepository.of(
            List.of(
                    SynchronizedDocumentRepository.of(
                            InMemoryDocumentRepository.of(),
                            List.of(documentSearchGenerator, documentRecommendationGenerator),
                            documentContentRepository,
                            borrowingSessionRepository,
                            reviewRepository
                    )
            ),
            List.of(
                    GoogleApiDocumentRepository.of(httpClientProvider)
            )
    );

    protected final @NonNull AdministratorRepository administratorRepository = SynchronizedAdministratorRepository.of(
            InMemoryAdministratorRepository.of(),
            List.of(administratorSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );
    protected final @NonNull PatronRepository patronRepository = SynchronizedPatronRepository.of(
            InMemoryPatronRepository.of(),
            List.of(patronSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );
    protected final @NonNull StaffRepository staffRepository = SynchronizedStaffRepository.of(
            InMemoryStaffRepository.of(),
            List.of(staffSearchGenerator),
            borrowingSessionRepository,
            reviewRepository
    );

    protected final @NonNull UserRepositories userRepositories = UserRepositories.of(Set.of(
            administratorRepository, patronRepository, staffRepository
    ));

    // Others
    protected final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = ZxingDocumentQrCodeGenerator.of();
    protected final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheHttpClientDocumentUrlGenerator.of();

    protected final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    protected final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();

    protected final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    public static @NonNull AppResources of() {
        return new AppResources();
    }

    protected AppResources() {}

    @Override
    public void close() throws Exception {
        for (val field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            val resource = field.get(this);

            if (resource instanceof AutoCloseable autoCloseableResource)
                autoCloseableResource.close();
        }
    }
}
