package org.tomfoolery.core.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

@Value(staticConstructor = "of")
public class Page<Item> {
    int pageIndex;   // 1-indexed
    @NonNull List<Item> paginatedItems;

    public static int getOffset(int pageIndex, int pageSize) {
        return (pageIndex - 1) * pageSize;
    }

    public int getPageSize() {
        return this.paginatedItems.size();
    }

    public int getOffset() {
        return Page.getOffset(this.pageIndex, this.getPageSize());
    }
}
