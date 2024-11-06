package org.tomfoolery.configurations.monolith.terminal.utils.containers;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

@NoArgsConstructor(staticName = "of")
public class SelectionItems implements Iterable<SelectionItem> {
    private final @NonNull SequencedMap<Integer, SelectionItem> itemIndexToItemMap = new LinkedHashMap<>();

    public static @NonNull SelectionItems of(@NonNull Iterable<SelectionItem> selectionItems) {
        return new SelectionItems(selectionItems);
    }

    private SelectionItems(@NonNull Iterable<SelectionItem> selectionItems) {
        for (val selectionItem : selectionItems)
            this.addItem(selectionItem);
    }

    private void addItem(@NonNull SelectionItem selectionItem) {
        this.itemIndexToItemMap.put(selectionItem.getIndex(), selectionItem);
    }

    public @Nullable SelectionItem getItemByIndex(int selectionItemIndex) {
        return this.itemIndexToItemMap.get(selectionItemIndex);
    }

    @Override
    public @NonNull Iterator<SelectionItem> iterator() {
        return this.itemIndexToItemMap.sequencedValues().iterator();
    }

    public @NonNull Collection<SelectionItem> showItems() {
        return this.itemIndexToItemMap.sequencedValues();
    }
}