package org.tomfoolery.configurations.monolith.terminal.views.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.adapters.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;

import java.util.InputMismatchException;
import java.util.List;

public abstract class SelectionView implements View {
    private final @NonNull IOHandler ioHandler;
    private final @NonNull SelectionAdapter selectionAdapter;

    private @Nullable Class<? extends View> nextViewClass = this.getClass();

    protected SelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionAdapter.Item> items) {
        this.ioHandler = ioHandler;
        this.selectionAdapter = SelectionAdapter.of(items);
    }

    @Override
    public void run() {
        this.ioHandler.writeLine(getPrompt());

        val viewModel = this.selectionAdapter.get();
        displayViewModel(viewModel);

        try {
            val requestObject = getRequestObject();
            val responseModel = this.selectionAdapter.apply(requestObject);

            this.nextViewClass = responseModel.getNextViewClass();
            this.ioHandler.writeLine(getMessageOnSuccess());

        } catch (InputMismatchException exception) {
            this.nextViewClass = this.getClass();
            this.ioHandler.writeLine(getMessageOnInputMismatchException());

        } catch (SelectionAdapter.ItemNotFoundException exception) {
            this.nextViewClass = this.getClass();
            this.ioHandler.writeLine(getMessageOnItemNotFoundException());
        }
    }

    @Override
    public final @Nullable Class<? extends View> getNextViewClass() {
        return this.nextViewClass;
    }

    protected @NonNull String getPrompt() {
        return "Welcome to My Application!";
    }

    private void displayViewModel(SelectionAdapter.@NonNull ViewModel viewModel) {
        val viewonlyItems = viewModel.getViewonlyItems();

        for (val viewonlyItem : viewonlyItems)
            displayViewonlyItem(viewonlyItem);
    }

    private void displayViewonlyItem(SelectionAdapter.@NonNull ViewonlyItem viewonlyItem) {
        val index = viewonlyItem.getIndex();
        val label = viewonlyItem.getLabel();

        this.ioHandler.writeLine("[%d] %s", index, label);
    }

    private SelectionAdapter.@NonNull RequestObject getRequestObject() throws InputMismatchException {
        val rawItemIndex = this.ioHandler.readLine();
        val itemIndex = Integer.parseInt(rawItemIndex);

        return SelectionAdapter.RequestObject.of(itemIndex);
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

/*
1. View requests from Controller the List of something, each something contains a index (int) and label (String)
2. View get player input (int) and pass it to Controller (throws FormatInvalidException)
3. Controller return result (nextViewClass) and pass it to View (throws ItemNotFoundException)
4. View display message based on success/failure
5. redirection

 */