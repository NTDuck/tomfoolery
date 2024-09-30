package org.tomfoolery.core.dataproviders;

import lombok.NonNull;

import java.util.Collection;

public interface Repository<Entity, EntityID> {
    void save(@NonNull Entity entity);
    void delete(@NonNull EntityID entityId);

    Entity get(@NonNull EntityID entityId);
    Collection<Entity> show();

    boolean contains(@NonNull EntityID entityId);
}
