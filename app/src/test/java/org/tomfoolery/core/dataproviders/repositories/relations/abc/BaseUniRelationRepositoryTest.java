package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.utils.contracts.ddd;

@Test(groups = { "unit", "repository", "uni-relation" })
public abstract class BaseUniRelationRepositoryTest<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseRepositoryTest<UniRelation, UniRelationId> {
    @Override
    protected abstract @NonNull BaseUniRelationRepository<UniRelation, UniRelationId, EntityId> createTestSubject();
}
