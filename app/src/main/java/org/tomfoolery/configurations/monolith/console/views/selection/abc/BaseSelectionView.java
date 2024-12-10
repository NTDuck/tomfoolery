package org.tomfoolery.configurations.monolith.console.views.selection.abc;

import com.github.freva.asciitable.*;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.adapters.controllers.selection.SelectionController;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.FontFormatter;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;

import java.util.List;

public abstract class BaseSelectionView extends BaseView {
    private static final @NonNull String TITLE = "tomfoolery";

    private final @NonNull SelectionController controller;

    protected BaseSelectionView(@NonNull IOProvider ioProvider, @NonNull List<SelectionItem> selectionItems) {
        super(ioProvider);

        this.controller = SelectionController.of(selectionItems);
    }

    @Override
    public final void run() {
        this.displayTitle();
        this.displayPrompt();

        val viewModel = this.controller.get();
        this.displayViewModel(viewModel);

        try {
            val requestObject = this.collectRequestObject();
            val responseModel = this.controller.apply(requestObject);
            this.onSuccess(responseModel);

        } catch (SelectionItemIndexInvalidException exception) {
            this.onSelectionItemIndexInvalidException();

        } catch (SelectionController.SelectionItemNotFoundException exception) {
            this.onSelectionItemNotFoundException();
        }
    }

    @SneakyThrows
    private void displayTitle() {
        this.ioProvider.writeLine("");
        val formattedTitle = FontFormatter.format(TITLE);
        // val formattedTitle = FontFormatter.format(TITLE, FontFormatter.Font.ANSI_SHADOW);
        this.ioProvider.writeLine(formattedTitle);
    }

    private void displayPrompt() {
        val prompt = this.getPrompt();

        if (prompt.isBlank())
            return;

        this.ioProvider.writeLine(prompt);
    }

    private void displayViewModel(SelectionController.@NonNull ViewModel viewModel) {
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

        val message = getMessageOnSuccess();
        this.ioProvider.writeLine(message);
        this.ioProvider.writeLine("");
    }

    private void onSelectionItemIndexInvalidException() {
        this.nextViewClass = this.getClass();

        val message = getMessageOnInputMismatchException();
        this.ioProvider.writeLine(message);
    }

    private void onSelectionItemNotFoundException() {
        this.nextViewClass = this.getClass();

        val message = getMessageOnItemNotFoundException();
        this.ioProvider.writeLine(message);
    }

    @SneakyThrows
    protected @NonNull String getPrompt() {
        return String.format("Please select something%s",
            loggedInUsername != null ? String.format(", %s", loggedInUsername) : "");
    }

    protected @NonNull String getMessageOnSuccess() {
        return String.format(Message.Format.SUCCESS, "Redirecting ...");
    }

    protected @NonNull String getMessageOnInputMismatchException() {
        return String.format(Message.Format.ERROR, "Invalid input format");
    }

    protected @NonNull String getMessageOnItemNotFoundException() {
        return String.format(Message.Format.ERROR, "Selection not found");
    }

    private static class SelectionItemIndexInvalidException extends Exception {}
}
