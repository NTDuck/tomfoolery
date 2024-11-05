package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.View;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.Views;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.generators.base64.auth.security.Base64AuthenticationTokenGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.base64.auth.security.Base64PasswordEncoder;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.security.InMemoryAuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.InMemoryStaffRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();

    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();
    private final @NonNull UserRepositories userRepositories = UserRepositories.of(this.administratorRepository, this.staffRepository, this.patronRepository);

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator = Base64AuthenticationTokenGenerator.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository.of();
    private final @NonNull PasswordEncoder passwordEncoder = Base64PasswordEncoder.of();

    private final @NonNull Views views = Views.of(
        GuestSelectionView.of(),
        LogUserInActionView.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository),
        CreatePatronAccountActionView.of(patronRepository, passwordEncoder),

        AdministratorSelectionView.of(),
        StaffSelectionView.of(),
        PatronSelectionView.of()
    );

    @Override
    public void run() {
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
    public void close() {
        ScannerManager.close();
    }

    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
