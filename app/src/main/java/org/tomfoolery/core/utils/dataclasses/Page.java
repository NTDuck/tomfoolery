package org.tomfoolery.core.utils.dataclasses;

import lombok.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class Page<T> implements Iterable<T> {
    private final static @Unsigned int MIN_PAGE_INDEX = 1;

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

    public static <T> @Nullable Page<T> fromUnpaginated(@NonNull List<T> unpaginatedItems, int pageIndex, int maxPageSize) {
        if (pageIndex < 0)
            return null;

        val pageOffset = calculatePageOffset(pageIndex, maxPageSize);
        val maxPageIndex = calculateMaxPageIndex(unpaginatedItems.size(), maxPageSize);

        if (pageOffset < 0 || maxPageIndex < pageIndex)
            return null;

        val paginatedItems = unpaginatedItems.parallelStream()
            .skip(pageOffset)
            .limit(maxPageSize)
            .collect(Collectors.toUnmodifiableList());

        return Page.fromPaginated(paginatedItems, pageIndex, maxPageIndex);
    }

    public <U> @NonNull Page<U> map(@NonNull Function<T, U> mapper) {
        val paginatedItems = this.paginatedItems.stream()
            .map(mapper)
            .toList();

        return new Page<>(paginatedItems, this.pageIndex, this.maxPageIndex);
    }

    public @NonNull List<T> toPaginatedList() {
        return this.paginatedItems;
    }

    public static @Unsigned int calculatePageOffset(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        return (pageIndex - MIN_PAGE_INDEX) * maxPageSize;
    }

    public static @Unsigned int calculateMaxPageIndex(@Unsigned int unpaginatedItemsSize, @Unsigned int maxPageSize) {
        return MIN_PAGE_INDEX + Math.ceilDiv(unpaginatedItemsSize, maxPageSize);
    }

    public int getPageSize() {
        return this.paginatedItems.size();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.paginatedItems.iterator();
    }
}
