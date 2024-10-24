package org.tomfoolery.configurations.monolith.terminal.utils.contract;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;

import java.util.InputMismatchException;
import java.util.List;

public abstract class SelectionView implements View {
    private final @NonNull SelectionAdapter selectionAdapter;
    private @Nullable Class<? extends View> nextViewClass = this.getClass();

    protected SelectionView(@NonNull List<SelectionAdapter.Item> items) {
        this.selectionAdapter = SelectionAdapter.of(items);
    }

    @Override
    public void run() {
        displayPrompt();

        val viewModel = this.selectionAdapter.get();
        displayViewModel(viewModel);

        try {
            val requestObject = getRequestObject();
            val responseModel = this.selectionAdapter.apply(requestObject);

            this.nextViewClass = responseModel.getNextViewClass();
            displayMessageOnSuccess();

        } catch (InputMismatchException exception) {
            this.nextViewClass = this.getClass();
            displayMessageOnInputMismatchException();

        } catch (SelectionAdapter.ItemNotFoundException exception) {
            this.nextViewClass = this.getClass();
            displayMessageOnItemNotFoundException();
        }
    }

    @Override
    public final @Nullable Class<? extends View> getNextViewClass() {
        return this.nextViewClass;
    }

    private void displayPrompt() {
        System.out.println(this.getPrompt());
    }

    protected @NonNull String getPrompt() {
        return "Welcome to My Application!";
    }

    private static void displayViewModel(SelectionAdapter.@NonNull ViewModel viewModel) {
        val viewonlyItems = viewModel.getViewonlyItems();

        for (val viewonlyItem : viewonlyItems)
            displayViewonlyItem(viewonlyItem);
    }

    private static void displayViewonlyItem(SelectionAdapter.@NonNull ViewonlyItem viewonlyItem) {
        val index = viewonlyItem.getIndex();
        val label = viewonlyItem.getLabel();

        System.out.println("[" + index + "] " + label);
    }

    private static SelectionAdapter.@NonNull RequestObject getRequestObject() throws InputMismatchException {
        val scanner = ScannerService.getScanner();

        val index = scanner.nextInt();
        scanner.nextLine();

        return SelectionAdapter.RequestObject.of(index);
    }

    private void displayMessageOnSuccess() {
        System.out.println(this.getMessageOnSuccess());
    }

    protected @NonNull String getMessageOnSuccess() {
        return "Success: Redirecting ...";
    }

    private void displayMessageOnInputMismatchException() {
        System.out.println(this.getMessageOnInputMismatchException());
    }

    protected @NonNull String getMessageOnInputMismatchException() {
        return "Error: Input format invalid.";
    }

    private void displayMessageOnItemNotFoundException() {
        System.out.println(this.getMessageOnItemNotFoundException());
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