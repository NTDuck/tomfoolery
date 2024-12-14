package org.tomfoolery.core.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;

/**
 * Expresses explicitly pseudo-DDD concepts.
 * @see <a href="https://github.com/xmolecules/jmolecules">jmolecules-ddd</a>
 */
public interface ddd {
    interface EntityId extends Serializable {}

    interface Entity<EntityId extends ddd.EntityId> extends Serializable {
        @NonNull EntityId getId();
    }

    interface UniRelationId<EntityId extends ddd.EntityId> extends ddd.EntityId {
        @NonNull EntityId getEntityId();
    }

    interface UniRelation<UniRelationId extends ddd.EntityId, EntityId extends ddd.EntityId> extends ddd.Entity<UniRelationId> {}

    interface BiRelationId<FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends ddd.EntityId {
        @NonNull FirstEntityId getFirstEntityId();
        @NonNull SecondEntityId getSecondEntityId();
    }

    interface BiRelation<BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends ddd.Entity<BiRelationId> {}
}
