package org.tomfoolery.configurations.monolith.console.adapters.controllers.selection;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(staticName = "of")
public class SelectionController implements Supplier<SelectionController.ViewModel>, ThrowableFunction<SelectionController.RequestObject, SelectionController.ResponseModel> {
    private final @NonNull List<SelectionItem> selectionItems;

    @Override
    public @NonNull ViewModel get() {
        val viewableSelectionItems = this.getViewableSelectionItems();
        return ViewModel.of(viewableSelectionItems);
    }

    @Override
    public @NonNull ResponseModel apply(@NonNull RequestObject requestObject) throws SelectionItemNotFoundException {
        val selectionItemIndex = requestObject.getSelectionItemIndex();

        val selectionItem = this.getSelectionItemFromIndex(selectionItemIndex);
        val viewClass = selectionItem.getViewClass();

        return ResponseModel.of(viewClass);
    }

    private @NonNull List<ViewableSelectionItem> getViewableSelectionItems() {
        return IntStream.range(0, this.selectionItems.size())
            .mapToObj(selectionItemIndex -> ViewableSelectionItem.of(selectionItemIndex, this.selectionItems.get(selectionItemIndex).getLabel()))
            .collect(Collectors.toUnmodifiableList());
    }

    private @NonNull SelectionItem getSelectionItemFromIndex(@Unsigned int selectionItemIndex) throws SelectionItemNotFoundException {
        try {
            return this.selectionItems.get(selectionItemIndex);
        } catch (IndexOutOfBoundsException exception) {
            throw new SelectionItemNotFoundException();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int selectionItemIndex;
    }

    @Value(staticConstructor = "of")
    public static class ResponseModel {
        @Nullable Class<? extends BaseView> nextViewClass;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<ViewableSelectionItem> selectionItems;
    }

    public static class SelectionItemNotFoundException extends Exception {}

    @Value(staticConstructor = "of")
    public static class ViewableSelectionItem {
        @Unsigned int index;
        @NonNull String label;
    }
}
