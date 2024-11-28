package org.tomfoolery.core.utils.dataclasses;

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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Value
public class Page<T> implements Iterable<T> {
    private static final @Unsigned int MIN_PAGE_INDEX = 1;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @NonNull List<T> paginatedItems;

    @Unsigned int pageIndex;   // 1-indexed
    @Unsigned int maxPageIndex;

    public static <T> @Nullable Page<T> fromPaginated(@NonNull List<T> paginatedItems, @Unsigned int pageIndex, @Unsigned int maxPageIndex) {
        if (pageIndex < MIN_PAGE_INDEX || maxPageIndex < pageIndex)
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
    
    public static <T> @Nullable Page<T> fromUnpaginated(@NonNull List<T> unpaginatedItems, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        if (pageIndex < MIN_PAGE_INDEX)
            return null;

        val pageOffset = (pageIndex - MIN_PAGE_INDEX) * maxPageSize;
        val maxPageIndex = performUpperRoundedDivision(unpaginatedItems.size(), maxPageSize);

        if (pageOffset < 0 || maxPageIndex < pageIndex)
            return null;

        val paginatedItems = unpaginatedItems.parallelStream()
            .skip(pageOffset)
            .limit(maxPageSize)
            .collect(Collectors.toUnmodifiableList());

        return Page.fromPaginated(paginatedItems, pageIndex, maxPageIndex);
    }

    private static @Unsigned int performUpperRoundedDivision(@Unsigned int numerator, @Unsigned int denominator) {
        return (numerator + denominator - 1) / denominator;
    }

    public int getPageSize() {
        return this.paginatedItems.size();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.paginatedItems.iterator();
    }
}
