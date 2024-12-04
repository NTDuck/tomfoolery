package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.administrator.users.retrieval.GetStaffByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.retrieval.GetStaffByIdController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetStaffByIdActionView extends UserActionView {
    private final @NonNull GetStaffByIdController getStaffByIdController;

    public static @NonNull GetStaffByIdActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetStaffByIdActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetStaffByIdActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getStaffByIdController = GetStaffByIdController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getStaffByIdController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetStaffByIdUseCase.AuthenticationTokenInvalidException | GetStaffByIdUseCase.AuthenticationTokenNotFoundException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UserIdBiAdapter.UserUuidInvalidException | GetStaffByIdUseCase.UserNotFoundException exception) {
            this.onException(exception);
        }
    }

    private GetStaffByIdController.@NonNull RequestObject collectRequestObject() {
        val uuid = this.ioProvider.readLine(Message.Format.PROMPT, "administrator UUID");

        return GetStaffByIdController.RequestObject.of(uuid);
    }

    private void displayViewModel(GetStaffByIdController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("""
            Staff Details:
            - UUID: %s
            - Username: %s
            - Created At: %s
            - Last Login: %s
            - Last Logout: %s""",
            viewModel.getStaffUuid(),
            viewModel.getStaffUsername(),
            viewModel.getCreationTimestamp(),
            viewModel.getLastLoginTimestamp(),
            viewModel.getLastLogoutTimestamp()
        );
    }

    private void onSuccess(GetStaffByIdController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }
}
