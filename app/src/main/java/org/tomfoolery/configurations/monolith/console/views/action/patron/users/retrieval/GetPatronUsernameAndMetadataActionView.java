package org.tomfoolery.configurations.monolith.console.views.action.patron.users.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.users.retrieval.GetPatronUsernameAndMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.users.retrieval.GetPatronUsernameAndMetadataController;

public final class GetPatronUsernameAndMetadataActionView extends UserActionView {
    private final @NonNull GetPatronUsernameAndMetadataController getPatronUsernameAndMetadataController;

    public static @NonNull GetPatronUsernameAndMetadataActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronUsernameAndMetadataActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronUsernameAndMetadataActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getPatronUsernameAndMetadataController = GetPatronUsernameAndMetadataController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val viewModel = this.getPatronUsernameAndMetadataController.get();
            this.onSuccess(viewModel);

        } catch (GetPatronUsernameAndMetadataUseCase.AuthenticationTokenNotFoundException | GetPatronUsernameAndMetadataUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (GetPatronUsernameAndMetadataUseCase.PatronNotFoundException exception) {
            this.onException(exception);
        }
    }

    private void displayViewModel(GetPatronUsernameAndMetadataController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("""
            Patron Details:
            - Username: %s
            - Name: %s %s
            - DoB: %d/%d/%d
            - Phone number: %s
            - Address: %s, %s
            - Email: %s""",
            viewModel.getPatronUsername(),
            viewModel.getPatronFirstName(), viewModel.getPatronLastName(),
            viewModel.getPatronDayOfBirth(), viewModel.getPatronMonthOfBirth(), viewModel.getPatronYearOfBirth(),
            viewModel.getPatronPhoneNumber(),
            viewModel.getPatronCity(), viewModel.getPatronCountry(),
            viewModel.getPatronEmail()
        );
    }

    private void onSuccess(GetPatronUsernameAndMetadataController.@NonNull ViewModel viewModel) {
        this.nextViewClass = PatronSelectionView.class;

        this.displayViewModel(viewModel);
    }
}
