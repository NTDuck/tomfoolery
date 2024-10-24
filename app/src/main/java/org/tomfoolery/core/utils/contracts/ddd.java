package org.tomfoolery.core.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;

public interface ddd {
    interface EntityId extends Serializable {}

    interface Entity<EntityId extends ddd.EntityId> extends Serializable {
        @NonNull EntityId getId();
    }
}
