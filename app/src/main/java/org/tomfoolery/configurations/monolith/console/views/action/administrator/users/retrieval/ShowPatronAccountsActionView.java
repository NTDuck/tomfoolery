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
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowPatronAccountsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetPatronByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.ShowPatronAccountsController;

import java.util.List;

public final class ShowPatronAccountsActionView extends UserActionView {
    private final @NonNull ShowPatronAccountsController showPatronAccountsController;

    public static @NonNull ShowPatronAccountsActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowPatronAccountsActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowPatronAccountsActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.showPatronAccountsController = ShowPatronAccountsController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.showPatronAccountsController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ShowPatronAccountsUseCase.AuthenticationTokenNotFoundException | ShowPatronAccountsUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | ShowPatronAccountsUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private ShowPatronAccountsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val pageIndex = this.collectPageIndex();

        return ShowPatronAccountsController.RequestObject.of(pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(ShowPatronAccountsController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Displaying patron accounts, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getPaginatedPatrons(), List.of(
                new Column()
                    .header("UUID")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetPatronByIdController.ViewModel::getPatronUuid),
                new Column()
                    .header("username")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetPatronByIdController.ViewModel::getPatronUsername)
            ))
            .asString();

        this.ioProvider.writeLine(table);
    }

    private void onSuccess(ShowPatronAccountsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
