package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowAdministratorAccountsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetAdministratorByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.ShowAdministratorAccountsController;

import java.util.List;

public final class ShowAdministratorAccountsActionView extends UserActionView {
    private final @NonNull ShowAdministratorAccountsController showAdministratorAccountsController;

    public static @NonNull ShowAdministratorAccountsActionView of(@NonNull IOProvider ioProvider, @NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowAdministratorAccountsActionView(ioProvider, administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowAdministratorAccountsActionView(@NonNull IOProvider ioProvider, @NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.showAdministratorAccountsController = ShowAdministratorAccountsController.of(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.showAdministratorAccountsController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ShowAdministratorAccountsUseCase.AuthenticationTokenNotFoundException | ShowAdministratorAccountsUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | ShowAdministratorAccountsUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private ShowAdministratorAccountsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val pageIndex = this.collectPageIndex();

        return ShowAdministratorAccountsController.RequestObject.of(pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(ShowAdministratorAccountsController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Displaying administrator accounts, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getPaginatedAdministrators(), List.of(
                new Column()
                    .header("UUID")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetAdministratorByIdController.ViewModel::getAdministratorUuid),
                new Column()
                    .header("username")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetAdministratorByIdController.ViewModel::getAdministratorUsername)
            ))
            .asString();

        this.ioProvider.writeLine(table);
    }

    private void onSuccess(ShowAdministratorAccountsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
