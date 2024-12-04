package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.search.SearchStaffByUsernameUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.search.SearchStaffByUsernameController;

public final class SearchStaffByUsernameActionView extends UserActionView {
    private final @NonNull SearchStaffByUsernameController searchStaffByUsernameController;

    public static @NonNull SearchStaffByUsernameActionView of(@NonNull IOProvider ioProvider, @NonNull UserSearchGenerator<Staff> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchStaffByUsernameActionView(ioProvider, userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchStaffByUsernameActionView(@NonNull IOProvider ioProvider, @NonNull UserSearchGenerator<Staff> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.searchStaffByUsernameController = SearchStaffByUsernameController.of(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.searchStaffByUsernameController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (SearchStaffByUsernameUseCase.AuthenticationTokenNotFoundException | SearchStaffByUsernameUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | SearchStaffByUsernameUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private SearchStaffByUsernameController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val searchTerm = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val pageIndex = this.collectPageIndex();

        return SearchStaffByUsernameController.RequestObject.of(searchTerm, pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(SearchStaffByUsernameController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Displaying patron accounts, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        viewModel.getStaff()
            .forEach(patron -> {
                this.ioProvider.writeLine("- [%s] %s", patron.getStaffUuid(), patron.getStaffUsername());
            });
    }

    private void onSuccess(SearchStaffByUsernameController.@NonNull ViewModel viewModel) {
        this.nextViewClass = AdministratorSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
