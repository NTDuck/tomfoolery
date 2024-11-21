package org.tomfoolery.core.utils.dataclasses.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@Value
public class Page<T> implements Iterable<T> {
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @NonNull List<T> paginatedItems;

    @Unsigned int pageIndex;   // 1-indexed
    @Unsigned int maxPageIndex;

    public static <T> @Nullable Page<T> fromPaginated(@NonNull List<T> paginatedItems, @Unsigned int pageIndex, @Unsigned int maxPageIndex) {
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
    
    public static <T> @Nullable Page<T> fromUnpaginated(@NonNull List<T> unpaginatedItems, int pageIndex, int maxPageSize) {
        if (pageIndex < 0)
            return null;

        val pageOffset = (pageIndex - 1) * maxPageSize;
        val maxPageIndex = unpaginatedItems.size() / maxPageSize;

        if (maxPageIndex < pageIndex)
            return null;

        val paginatedItems = unpaginatedItems.parallelStream()
            .skip(pageOffset)
            .limit(maxPageSize)
            .toList();

        return Page.fromPaginated(paginatedItems, pageIndex, maxPageIndex);
    }

    public int getPageSize() {
        return this.paginatedItems.size();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.paginatedItems.iterator();
    }
}
