package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search;

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
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchAdministratorsByUsernameUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetAdministratorByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search.SearchAdministratorsByUsernameController;

import java.util.List;

public final class SearchAdministratorsByUsernameActionView extends UserActionView {
    private final @NonNull SearchAdministratorsByUsernameController searchAdministratorsByUsernameController;

    public static @NonNull SearchAdministratorsByUsernameActionView of(@NonNull IOProvider ioProvider, @NonNull AdministratorSearchGenerator administratorSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchAdministratorsByUsernameActionView(ioProvider, administratorSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchAdministratorsByUsernameActionView(@NonNull IOProvider ioProvider, @NonNull AdministratorSearchGenerator administratorSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.searchAdministratorsByUsernameController = SearchAdministratorsByUsernameController.of(administratorSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.searchAdministratorsByUsernameController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (SearchAdministratorsByUsernameUseCase.AuthenticationTokenNotFoundException | SearchAdministratorsByUsernameUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | SearchAdministratorsByUsernameUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private SearchAdministratorsByUsernameController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val searchTerm = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val pageIndex = this.collectPageIndex();

        return SearchAdministratorsByUsernameController.RequestObject.of(searchTerm, pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(SearchAdministratorsByUsernameController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Displaying administrator accounts, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getAdministrators(), List.of(
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

    private void onSuccess(SearchAdministratorsByUsernameController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
