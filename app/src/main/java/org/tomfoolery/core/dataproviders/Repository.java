package org.tomfoolery.core.dataproviders;

import javax.annotation.Nullable;
import java.util.Collection;

public interface Repository<Entity, EntityID> {
    void save(Entity entity);
    void delete(EntityID entityId);

    @Nullable Entity getById(EntityID entityId);
    Collection<Entity> show();

    default boolean contains(EntityID entityId) {
        return this.getById(entityId) != null;
    }
}
