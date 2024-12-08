package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.utils.contracts.ddd;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "bi-relation" })
public abstract class BaseBiRepositoryTest<BiRelation extends ddd.BiRelation<BiRelationId, FirstEntityId, SecondEntityId>, BiRelationId extends ddd.BiRelationId<FirstEntityId, SecondEntityId>, FirstEntityId extends ddd.EntityId, SecondEntityId extends ddd.EntityId> extends BaseRepositoryTest<BiRelation, BiRelationId> {
    @Override
    protected abstract @NonNull BaseBiRepository<BiRelation, BiRelationId, FirstEntityId, SecondEntityId> createTestSubject();
}
