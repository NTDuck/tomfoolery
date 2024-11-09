package org.tomfoolery.core.utils.dataclasses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Page<T> implements Iterable<T> {
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @NonNull List<T> paginatedItems;

    int pageIndex;   // 1-indexed
    int maxPageIndex;

    public static <T> @Nullable Page<T> fromPaginated(@NonNull List<T> paginatedItems, int pageIndex, int maxPageIndex) {
        if (pageIndex < 0 || maxPageIndex < pageIndex)
            return null;

        return new Page<>(paginatedItems, pageIndex, maxPageIndex);
    }

    public static <T, U> @NonNull Page<U> fromPaginated(@NonNull Page<T> sourcePage, @NonNull Function<T, U> mapper) {
        val paginatedItems = StreamSupport.stream(sourcePage.spliterator(), true)
                .map(mapper)
                .toList();

        val pageIndex = sourcePage.getPageIndex();
        val maxPageIndex = sourcePage.getMaxPageIndex();

        val page = Page.fromPaginated(paginatedItems, pageIndex, maxPageIndex);
        assert page != null;

        return page;
    }
    
    public static <T> @Nullable Page<T> fromUnpaginated(@NonNull Collection<T> unpaginatedItems, int pageIndex, int maxPageSize) {
        if (pageIndex < 0)
            return null;

        val pageOffset = getOffset(pageIndex, maxPageSize);
        val maxPageIndex = unpaginatedItems.size() / maxPageSize;

        if (maxPageIndex < pageIndex)
            return null;

        val paginatedItems = unpaginatedItems.stream()
            .skip(pageOffset)
            .limit(maxPageSize)
            .toList();

        return Page.fromPaginated(paginatedItems, pageIndex, maxPageIndex);
    }

    public static int getOffset(int pageIndex, int maxPageSize) {
        return (pageIndex - 1) * maxPageSize;
    }

    public int getPageSize() {
        return this.paginatedItems.size();
    }

    public int getOffset() {
        return Page.getOffset(this.pageIndex, this.getPageSize());
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.paginatedItems.iterator();
    }
}
