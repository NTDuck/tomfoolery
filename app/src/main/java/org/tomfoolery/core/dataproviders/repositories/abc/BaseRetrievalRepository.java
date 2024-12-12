package org.tomfoolery.core.dataproviders.repositories.abc;

import com.google.errorprone.annotations.DoNotCall;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.Set;

public interface BaseRetrievalRepository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseRepository<Entity, EntityId> {
    @Override
    @DoNotCall
    default void save(@NonNull Entity entity) {
        // throw new UnsupportedOperationException();
    }

    @Override
    @DoNotCall
    default void delete(@NonNull EntityId entityId) {
        // throw new UnsupportedOperationException();
    }

    @Override
    @DoNotCall
    default @NonNull Set<EntityId> showIds() {
        // throw new UnsupportedOperationException();
        return Set.of();
    }
}
