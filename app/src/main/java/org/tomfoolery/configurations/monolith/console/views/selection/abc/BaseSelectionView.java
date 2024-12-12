package org.tomfoolery.configurations.monolith.console.views.selection.abc;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.adapters.controllers.selection.SelectionController;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.FontFormatter;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;

import java.util.List;

public abstract class BaseSelectionView extends BaseView {
    private static final @NonNull String TITLE = "tomfoolery";

    private final @NonNull GetStatisticsController getStatisticsController;
    private final @NonNull SelectionController selectionController;

    protected BaseSelectionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository, @NonNull List<SelectionItem> selectionItems) {
        super(ioProvider);

        this.getStatisticsController = GetStatisticsController.of(documentRepository, administratorRepository, patronRepository, staffRepository);
        this.selectionController = SelectionController.of(selectionItems);
    }

    @Override
    public final void run() {
        this.displayTitle();

        val statisticsViewModel = this.getStatisticsController.get();
        this.displayStatisticsViewModel(statisticsViewModel);

        this.displayPrompt();

        val selectionViewModel = this.selectionController.get();
        this.displaySelectionViewModel(selectionViewModel);

        try {
            val requestObject = this.collectRequestObject();
            val responseModel = this.selectionController.apply(requestObject);
            this.onSuccess(responseModel);

        } catch (SelectionItemIndexInvalidException | SelectionController.SelectionItemNotFoundException exception) {
            this.onException(exception, this.getClass());
        }
    }

    @SneakyThrows
    private void displayTitle() {
        this.ioProvider.writeLine("");
        val formattedTitle = FontFormatter.format(TITLE);
        // val formattedTitle = FontFormatter.format(TITLE, FontFormatter.Font.ANSI_SHADOW);
        this.ioProvider.writeLine(formattedTitle);
    }

    private void displayStatisticsViewModel(GetStatisticsController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("D[%d] A[%d] P[%d] S[%d]",
            viewModel.getNumberOfDocuments(), viewModel.getNumberOfAdministrators(), viewModel.getNumberOfPatrons(), viewModel.getNumberOfStaff());
    }

    private void displaySelectionViewModel(SelectionController.@NonNull ViewModel viewModel) {
        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getSelectionItems(), List.of(
                new Column()
                    .dataAlign(HorizontalAlign.RIGHT)
                    .with(SelectionController.ViewableSelectionItem::getIndex),
                new Column()
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(SelectionController.ViewableSelectionItem::getLabel)
            ))
            .asString();

        this.ioProvider.writeLine(table);
    }

    private SelectionController.@NonNull RequestObject collectRequestObject() throws SelectionItemIndexInvalidException {
        val rawSelectionItemIndex = this.ioProvider.readLine(" ");

        try {
            val selectionItemIndex = Integer.parseInt(rawSelectionItemIndex);
            return SelectionController.RequestObject.of(selectionItemIndex);

        } catch (NumberFormatException exception) {
            throw new SelectionItemIndexInvalidException();
        }
    }

    private void onSuccess(SelectionController.@NonNull ResponseModel responseModel) {
        this.nextViewClass = responseModel.getNextViewClass();

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Redirecting ...");
        this.ioProvider.writeLine("");
    }

    private void displayPrompt() {
        val prompt = this.getPrompt();

        if (prompt.isBlank())
            return;

        this.ioProvider.writeLine(prompt);
    }

    protected @NonNull String getPrompt() {
        return String.format("Please select something%s",
            loggedInUsername != null ? String.format(", %s", loggedInUsername) : "");
    }

    private static class SelectionItemIndexInvalidException extends Exception {}
}
