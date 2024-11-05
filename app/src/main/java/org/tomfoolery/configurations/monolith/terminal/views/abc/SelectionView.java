package org.tomfoolery.configurations.monolith.terminal.views.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.adapters.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;

import java.util.InputMismatchException;
import java.util.List;

public abstract class SelectionView implements View {
    private final @NonNull IOHandler ioHandler;
    private final @NonNull SelectionAdapter selectionAdapter;

    private @Nullable Class<? extends View> nextViewClass = this.getClass();

    protected SelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> selectionItems) {
        this.ioHandler = ioHandler;
        this.selectionAdapter = SelectionAdapter.of(selectionItems);
    }

    @Override
    public void run() {
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

    @Override
    public final @Nullable Class<? extends View> getNextViewClass() {
        return this.nextViewClass;
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

        this.ioHandler.writeLine("[%d] %s", index, label);
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

        val message = getMessageOnInputMismatchException();
        this.ioHandler.writeLine(message);
    }

    protected @NonNull String getPrompt() {
        return "Welcome to My Application!";
    }

    protected @NonNull String getMessageOnSuccess() {
        return "Success: Redirecting ...";
    }

    protected @NonNull String getMessageOnInputMismatchException() {
        return "Error: Input format invalid.";
    }

    protected @NonNull String getMessageOnItemNotFoundException() {
        return "Error: Item not found.";
    }
}
