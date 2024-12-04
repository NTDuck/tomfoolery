package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.administrator.users.retrieval.GetAdministratorByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.retrieval.GetAdministratorByIdController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetAdministratorByIdActionView extends UserActionView {
    private final @NonNull GetAdministratorByIdController getAdministratorByIdController;

    public static @NonNull GetAdministratorByIdActionView of(@NonNull IOProvider ioProvider, @NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetAdministratorByIdActionView(ioProvider, administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetAdministratorByIdActionView(@NonNull IOProvider ioProvider, @NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getAdministratorByIdController = GetAdministratorByIdController.of(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getAdministratorByIdController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetAdministratorByIdUseCase.AuthenticationTokenInvalidException | GetAdministratorByIdUseCase.AuthenticationTokenNotFoundException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UserIdBiAdapter.UserUuidInvalidException | GetAdministratorByIdUseCase.UserNotFoundException exception) {
            this.onException(exception);
        }
    }

    private GetAdministratorByIdController.@NonNull RequestObject collectRequestObject() {
        val uuid = this.ioProvider.readLine(Message.Format.PROMPT, "administrator UUID");

        return GetAdministratorByIdController.RequestObject.of(uuid);
    }

    private void displayViewModel(GetAdministratorByIdController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("""
            Administrator Details:
            - UUID: %s
            - Username: %s
            - Created At: %s
            - Last Login: %s
            - Last Logout: %s""",
            viewModel.getAdministratorUuid(),
            viewModel.getAdministratorUsername(),
            viewModel.getCreationTimestamp(),
            viewModel.getLastLoginTimestamp(),
            viewModel.getLastLogoutTimestamp()
        );
    }

    private void onSuccess(GetAdministratorByIdController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }
}
