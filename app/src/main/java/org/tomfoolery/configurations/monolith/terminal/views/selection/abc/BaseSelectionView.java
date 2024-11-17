package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.adapters.controllers.selection.SelectionController;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

import java.util.InputMismatchException;
import java.util.List;

public abstract class BaseSelectionView extends BaseView {
    private final @NonNull SelectionController controller;

    protected BaseSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> selectionItems) {
        super(ioHandler);

        this.controller = SelectionController.of(selectionItems);
    }

    @Override
    public final void run() {
        this.displayPrompt();

        val viewModel = this.controller.get();
        this.displayViewModel(viewModel);

        try {
            val requestObject = this.collectRequestObject();
            val responseModel = this.controller.apply(requestObject);

            this.onSuccess(responseModel);

        } catch (InputMismatchException exception) {
            this.onInputMismatchException();
        } catch (SelectionController.SelectionItemNotFoundException exception) {
            this.onSelectionItemNotFoundException();
        }
    }

    private void displayPrompt() {
        val prompt = this.getPrompt();

        if (prompt.isBlank())
            return;

        this.ioHandler.writeLine(prompt);
    }

    private void displayViewModel(SelectionController.@NonNull ViewModel viewModel) {
        viewModel.getViewableSelectionItems()
            .forEach(this::displayViewableSelectionItem);
    }

    private void displayViewableSelectionItem(SelectionController.@NonNull ViewableSelectionItem viewableSelectionItem) {
        val index = viewableSelectionItem.getIndex();
        val label = viewableSelectionItem.getLabel();

        this.ioHandler.writeLine("[%2d] %s", index, label);
    }

    private SelectionController.@NonNull RequestObject collectRequestObject() throws InputMismatchException {
        val rawSelectionItemIndex = this.ioHandler.readLine();
        val selectionItemIndex = Integer.parseInt(rawSelectionItemIndex);

        return SelectionController.RequestObject.of(selectionItemIndex);
    }

    private void onSuccess(SelectionController.@NonNull ResponseModel responseModel) {
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
        return String.format(Message.Format.SUCCESS, "Redirecting (●'◡'●)");
    }

    protected @NonNull String getMessageOnInputMismatchException() {
        return String.format(Message.Format.ERROR, "Invalid input format");
    }

    protected @NonNull String getMessageOnItemNotFoundException() {
        return String.format(Message.Format.ERROR, "Selection not found");
    }
}
