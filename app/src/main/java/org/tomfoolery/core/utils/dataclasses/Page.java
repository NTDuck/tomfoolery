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
    @NonNull List<T> paginatedTs;

    int pageIndex;   // 1-indexed
    int maxPageIndex;

    public static <T> @Nullable Page<T> of(@NonNull List<T> paginatedTs, int pageIndex, int maxPageIndex) {
        if (pageIndex < 0 || maxPageIndex < pageIndex)
            return null;

        return new Page<>(paginatedTs, pageIndex, maxPageIndex);
    }

    public static <T> @Nullable Page<T> of(@NonNull Collection<T> unpaginatedTs, int pageIndex, int maxPageSize) {
        if (pageIndex < 0)
            return null;

        val pageOffset = getOffset(pageIndex, maxPageSize);
        val maxPageIndex = unpaginatedTs.size() / maxPageSize;

        if (maxPageIndex < pageIndex)
            return null;

        val paginatedTs = unpaginatedTs.stream()
            .skip(pageOffset)
            .limit(maxPageSize)
            .toList();

        return Page.of(paginatedTs, pageIndex, maxPageIndex);
    }

    public static <T, U> @NonNull Page<U> of(@NonNull Page<T> pageOfT, @NonNull Function<T, U> mapper) {
        val paginatedUs = StreamSupport.stream(pageOfT.spliterator(), true)
                .map(mapper)
                .toList();

        val pageIndex = pageOfT.getPageIndex();
        val maxPageIndex = pageOfT.getMaxPageIndex();

        val pageOfU = Page.of(paginatedUs, pageIndex, maxPageIndex);
        assert pageOfU != null;

        return pageOfU;
    }

    public static int getOffset(int pageIndex, int maxPageSize) {
        return (pageIndex - 1) * maxPageSize;
    }

    public int getPageSize() {
        return this.paginatedTs.size();
    }

    public int getOffset() {
        return Page.getOffset(this.pageIndex, this.getPageSize());
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return this.paginatedTs.iterator();
    }
}
