package org.tomfoolery.core.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Expresses explicitly pseudo-DDD concepts.
 * @see <a href="https://github.com/xmolecules/jmolecules">jmolecules-ddd</a>
 */
public interface ddd {
    interface EntityId {}

    interface Entity<EntityId extends ddd.EntityId> {
        @NonNull EntityId getId();
    }

    interface BiRelationId<FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends ddd.EntityId {
        @NonNull FirstEntityId getFirstId();
        @NonNull SecondEntityId getSecondId();
    }

    interface BiRelation<BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends ddd.Entity<BiRelationId> {}
}
