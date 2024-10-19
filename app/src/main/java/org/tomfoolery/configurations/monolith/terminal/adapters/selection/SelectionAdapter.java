package org.tomfoolery.configurations.monolith.terminal.adapters.selection;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.View;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.function.Supplier;

public class SelectionAdapter implements Supplier<SelectionAdapter.ViewModel>, ThrowableFunction<SelectionAdapter.RequestObject, SelectionAdapter.ResponseModel> {
    private final @NonNull Items items;

    private SelectionAdapter(@NonNull List<Item> items) {
        this.items = Items.of(items);
    }

    public static @NonNull SelectionAdapter of(@NonNull List<Item> items) {
        return new SelectionAdapter(items);
    }

    @Override
    public @NonNull ViewModel get() {
        val viewonlyItems = getViewonlyItems();
        return ViewModel.of(viewonlyItems);
    }

    @Override
    public @NonNull ResponseModel apply(@NonNull RequestObject requestObject) throws ItemNotFoundException {
        val index = requestObject.getIndex();
        val item = this.items.getItemByIndex(index);

        if (item == null)
            throw new ItemNotFoundException();

        val viewClass = item.getViewClass();
        return ResponseModel.of(viewClass);
    }

    private @NonNull List<ViewonlyItem> getViewonlyItems() {
        List<ViewonlyItem> viewonlyItems = new ArrayList<>();

        for (val item : this.items) {
            val index = item.getIndex();
            val label = item.getLabel();
            val viewonlyItem = ViewonlyItem.of(index, label);
            viewonlyItems.add(viewonlyItem);
        }

        return viewonlyItems;
    }

    @Value(staticConstructor = "of")
    public static class Item {
        int index;
        @NonNull String label;
        @Nullable Class<? extends View> viewClass;
    }

    @Value(staticConstructor = "of")
    public static class ViewonlyItem {
        int index;
        @NonNull String label;
    }

    @NoArgsConstructor(staticName = "of")
    private static class Items implements Iterable<Item> {
        private final @NonNull SequencedMap<Integer, Item> itemIndexToItemMap = new LinkedHashMap<>();

        private Items(@NonNull Iterable<Item> items) {
            for (val item : items)
                this.addItem(item);
        }

        public static @NonNull Items of(@NonNull Iterable<Item> rows) {
            return new Items(rows);
        }

        private void addItem(@NonNull Item item) {
            this.itemIndexToItemMap.put(item.getIndex(), item);
        }

        public @Nullable Item getItemByIndex(int index) {
            return this.itemIndexToItemMap.get(index);
        }

        @Override
        public @NonNull Iterator<Item> iterator() {
            return this.itemIndexToItemMap.sequencedValues().iterator();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        int index;
    }

    @Value(staticConstructor = "of")
    public static class ResponseModel {
        @Nullable Class<? extends View> nextViewClass;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<SelectionAdapter.ViewonlyItem> viewonlyItems;
    }

    public static class ItemNotFoundException extends Exception {}
}
