package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.ConsoleIOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.DefaultIOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.View;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.Views;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.generators.apache.document.references.ApacheDocumentUrlGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security.BCryptPasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security.JJWTAuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.qrgen.documents.references.QrgenDocumentQrCodeGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.recommendation.InMemoryDocumentRecommendationRepository;

import java.util.function.Supplier;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();

    private final @NonNull DocumentRecommendationGenerator documentRecommendationGenerator = InMemoryDocumentRecommendationGenerator.of(documentRepository);
    private final @NonNull DocumentRecommendationRepository documentRecommendationRepository = InMemoryDocumentRecommendationRepository.of();

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator = QrgenDocumentQrCodeGenerator.of();
    private final @NonNull DocumentUrlGenerator documentUrlGenerator = ApacheDocumentUrlGenerator.of();

    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();

    private final @NonNull UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = JJWTAuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = KeyStoreAuthenticationTokenRepository.of();
    private final @NonNull PasswordEncoder passwordEncoder = BCryptPasswordEncoder.of();

    private final @NonNull IOHandler ioHandler = selectFirstAvailableResource(ConsoleIOHandler::of, DefaultIOHandler::of);

    private final @NonNull Views views = Views.of(
        GuestSelectionView.of(ioHandler),

        AdministratorSelectionView.of(ioHandler),
        PatronSelectionView.of(ioHandler),
        StaffSelectionView.of(ioHandler),

        CreatePatronAccountActionView.of(ioHandler, patronRepository, passwordEncoder),
        LogUserInActionView.of(ioHandler, userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository)
    );

    @Override
    public void run() {
        assert (ioHandler instanceof ConsoleIOHandler);

        Class<? extends View> viewClass = GuestSelectionView.class;
        View view;

        do {
            view = this.views.getViewByClass(viewClass);
            assert view != null;   // Expected

            view.run();

            viewClass = view.getNextViewClass();
        } while (viewClass != null);
    }

    @Override
    @SneakyThrows
    public void close() {
        for (val field : this.getClass().getDeclaredFields()) {
            val resource = field.get(this);

            if (resource instanceof AutoCloseable autoCloseableResource)
                autoCloseableResource.close();
        }
    }

    @SafeVarargs
    @SneakyThrows
    private static <T> @NonNull T selectFirstAvailableResource(@NonNull Supplier<T>... resourceSuppliers) {
        for (val resourceSupplier : resourceSuppliers) {
            try {
                val resource = resourceSupplier.get();

                if (resource != null)
                    return resource;

            } catch (Exception exception) {}
        }

        throw new InstantiationException();
    }

    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
