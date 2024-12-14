package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.GetPatronByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetPatronByIdController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetPatronByIdActionView extends UserActionView {
    private final @NonNull GetPatronByIdController getPatronByIdController;

    public static @NonNull GetPatronByIdActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetPatronByIdActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetPatronByIdActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getPatronByIdController = GetPatronByIdController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getPatronByIdController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetPatronByIdUseCase.AuthenticationTokenInvalidException | GetPatronByIdUseCase.AuthenticationTokenNotFoundException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UserIdBiAdapter.UserUuidInvalidException | GetPatronByIdUseCase.UserNotFoundException exception) {
            this.onException(exception);
        }
    }

    private GetPatronByIdController.@NonNull RequestObject collectRequestObject() {
        val uuid = this.ioProvider.readLine(Message.Format.PROMPT, "patron UUID");

        return GetPatronByIdController.RequestObject.of(uuid);
    }

    private void displayViewModel(GetPatronByIdController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("""
            Patron Details:
            - UUID: %s
            - Username: %s
            - Name: %s %s
            - DoB: %d/%d/%d
            - Phone number: %s
            - Address: %s, %s
            - Email: %s
            - Created at: %s
            - Last modified: %s
            - Last login: %s
            - Last logout: %s""",
            viewModel.getPatronUuid(),
            viewModel.getPatronUsername(),
            viewModel.getPatronFirstName(), viewModel.getPatronLastName(),
            viewModel.getPatronDayOfBirth(), viewModel.getPatronMonthOfBirth(), viewModel.getPatronYearOfBirth(),
            viewModel.getPatronPhoneNumber(),
            viewModel.getPatronCity(), viewModel.getPatronCountry(),
            viewModel.getPatronEmail(),
            viewModel.getCreationTimestamp(),
            viewModel.getLastModifiedTimestamp(),
            viewModel.getLastLoginTimestamp(),
            viewModel.getLastLogoutTimestamp()
        );
    }

    private void onSuccess(GetPatronByIdController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }
}
