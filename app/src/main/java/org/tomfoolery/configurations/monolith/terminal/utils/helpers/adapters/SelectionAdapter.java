package org.tomfoolery.configurations.monolith.terminal.utils.helpers.adapters;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.SelectionItems;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.views.abc.View;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SelectionAdapter implements Supplier<SelectionAdapter.ViewModel>, ThrowableFunction<SelectionAdapter.RequestObject, SelectionAdapter.ResponseModel> {
    private final @NonNull SelectionItems selectionItems;

    public static @NonNull SelectionAdapter of(@NonNull List<SelectionItem> selectionItems) {
        return new SelectionAdapter(selectionItems);
    }

    private SelectionAdapter(@NonNull List<SelectionItem> selectionItems) {
        this.selectionItems = SelectionItems.of(selectionItems);
    }

    @Override
    public @NonNull ViewModel get() {
        val viewonlySelectionItems = getViewonlySelectionItems();
        return ViewModel.of(viewonlySelectionItems);
    }

    @Override
    public @NonNull ResponseModel apply(@NonNull RequestObject requestObject) throws SelectionItemNotFoundException {
        val selectionItemIndex = requestObject.getSelectionItemIndex();
        val selectionItem = this.selectionItems.getItemByIndex(selectionItemIndex);

        if (selectionItem == null)
            throw new SelectionItemNotFoundException();

        val viewClass = selectionItem.getViewClass();
        return ResponseModel.of(viewClass);
    }

    private @NonNull List<ViewonlySelectionItem> getViewonlySelectionItems() {
        return this.selectionItems.showItems().stream()
            .map(ViewonlySelectionItem::of)
            .collect(Collectors.toUnmodifiableList());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        int selectionItemIndex;
    }

    @Value(staticConstructor = "of")
    public static class ResponseModel {
        @Nullable Class<? extends View> nextViewClass;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<ViewonlySelectionItem> viewonlySelectionItems;
    }

    public static class SelectionItemNotFoundException extends Exception {}

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ViewonlySelectionItem {
        int index;
        @NonNull String label;

        public static @NonNull ViewonlySelectionItem of(@NonNull SelectionItem selectionItem) {
            val index = selectionItem.getIndex();
            val label = selectionItem.getLabel();

            return new ViewonlySelectionItem(index, label);
        }
    }
}
