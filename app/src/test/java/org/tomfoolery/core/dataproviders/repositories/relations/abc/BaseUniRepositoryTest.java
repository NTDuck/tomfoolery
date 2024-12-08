package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.utils.contracts.ddd;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "uni-relation" })
public abstract class BaseUniRepositoryTest<UniRelation extends ddd.UniRelation<UniRelationId, EntityId>, UniRelationId extends ddd.UniRelationId<EntityId>, EntityId extends ddd.EntityId> extends BaseRepositoryTest<UniRelation, UniRelationId> {
    @Override
    protected abstract @NonNull BaseUniRepository<UniRelation, UniRelationId, EntityId> createTestSubject();
}
