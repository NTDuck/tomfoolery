package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

import java.util.InputMismatchException;
import java.util.List;

public abstract class BaseSelectionView extends BaseView {
    private final @NonNull SelectionAdapter selectionAdapter;

    protected BaseSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> selectionItems) {
        super(ioHandler);
        this.selectionAdapter = SelectionAdapter.of(selectionItems);
    }

    @Override
    public final void run() {
        displayPrompt();

        val viewModel = this.selectionAdapter.get();
        displayViewModel(viewModel);

        try {
            val requestObject = getRequestObject();
            val responseModel = this.selectionAdapter.apply(requestObject);

            onSuccess(responseModel);

        } catch (InputMismatchException exception) {
            onInputMismatchException();
        } catch (SelectionAdapter.SelectionItemNotFoundException exception) {
            onSelectionItemNotFoundException();
        }
    }

    private void displayPrompt() {
        val prompt = getPrompt();

        if (prompt.isEmpty() || prompt.isBlank())
            return;

        this.ioHandler.writeLine(prompt);
    }

    private void displayViewModel(SelectionAdapter.@NonNull ViewModel viewModel) {
        val viewonlySelectionItems = viewModel.getViewonlySelectionItems();

        for (val viewonlySelectionItem : viewonlySelectionItems)
            displayViewonlySelectionItem(viewonlySelectionItem);
    }

    private void displayViewonlySelectionItem(SelectionAdapter.@NonNull ViewonlySelectionItem viewonlySelectionItem) {
        val index = viewonlySelectionItem.getIndex();
        val label = viewonlySelectionItem.getLabel();

        this.ioHandler.writeLine("[%2d] %s", index, label);
    }

    private SelectionAdapter.@NonNull RequestObject getRequestObject() throws InputMismatchException {
        val rawItemIndex = this.ioHandler.readLine();
        val itemIndex = Integer.parseInt(rawItemIndex);

        return SelectionAdapter.RequestObject.of(itemIndex);
    }

    private void onSuccess(SelectionAdapter.@NonNull ResponseModel responseModel) {
        this.nextViewClass = responseModel.getNextViewClass();

        val message = getMessageOnSuccess();
        this.ioHandler.writeLine(message);
    }

    private void onInputMismatchException() {
        this.nextViewClass = this.getClass();

        val message = getMessageOnInputMismatchException();
        this.ioHandler.writeLine(message);
    }

    private void onSelectionItemNotFoundException() {
        this.nextViewClass = this.getClass();

        val message = getMessageOnItemNotFoundException();
        this.ioHandler.writeLine(message);
    }

    protected @NonNull String getPrompt() {
        return "Please select something";
    }

    protected @NonNull String getMessageOnSuccess() {
        return String.format(SUCCESS_MESSAGE_FORMAT, "Redirecting (●'◡'●)");
    }

    protected @NonNull String getMessageOnInputMismatchException() {
        return String.format(ERROR_MESSAGE_FORMAT, "Invalid input format");
    }

    protected @NonNull String getMessageOnItemNotFoundException() {
        return String.format(ERROR_MESSAGE_FORMAT, "Selection not found");
    }
}