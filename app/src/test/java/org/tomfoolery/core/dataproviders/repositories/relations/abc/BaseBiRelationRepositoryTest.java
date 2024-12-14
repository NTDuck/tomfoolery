package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.utils.contracts.ddd;

@Test(groups = { "unit", "repository", "bi-relation" })
public abstract class BaseBiRelationRepositoryTest<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseRepositoryTest<BiRelation, BiRelationId> {
    @Override
    protected abstract @NonNull BaseBiRelationRepository<BiRelation, BiRelationId, FirstEntityId, SecondEntityId> createTestSubject();
}
