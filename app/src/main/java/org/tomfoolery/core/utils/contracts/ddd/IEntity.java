package org.tomfoolery.core.utils.contracts.ddd;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface IEntity<EntityId> {
    @NonNull EntityId getId();
}
