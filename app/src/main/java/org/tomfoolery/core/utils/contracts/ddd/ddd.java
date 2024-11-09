package org.tomfoolery.core.utils.contracts.ddd;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;

/**
 * Explicitly express pseudo-DDD concepts,
 * not as strict as <a href="https://github.com/xmolecules/jmolecules">jmolecules-ddd</a>.
 *
 * @see <a href="https://github.com/xmolecules/jmolecules">jmolecules-ddd</a>
 */
public interface ddd {
    interface EntityId extends Serializable {}

    interface Entity<EntityId extends ddd.EntityId> extends Serializable {
        @NonNull EntityId getId();
    }

    interface ValueObject {}

    interface Repository<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> {}
}
