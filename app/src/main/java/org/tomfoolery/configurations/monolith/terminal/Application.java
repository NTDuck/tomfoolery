package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.View;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.Views;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64AuthenticationTokenService;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64PasswordService;
import org.tomfoolery.infrastructures.dataproviders.inmemory.*;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private final @NonNull StaffRepository staffRepository = InMemoryStaffRepository.of();
    private final @NonNull PatronRepository patronRepository = InMemoryPatronRepository.of();

    private final @NonNull DocumentRepository documentRepository = InMemoryDocumentRepository.of();
    private final @NonNull UserRepositories userRepositories = UserRepositories.of(this.administratorRepository, this.staffRepository, this.patronRepository);

    private final @NonNull AuthenticationTokenService authenticationTokenService = Base64AuthenticationTokenService.of();
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository.of();
    private final @NonNull PasswordService passwordService = Base64PasswordService.of();

    private final @NonNull Views views = Views.of(
        GuestSelectionView.of(),
        LogUserInActionView.of(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository),
        CreatePatronAccountActionView.of(patronRepository, passwordService),

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
        ScannerService.close();
    }

    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
