package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;

import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository" })
public abstract class BaseRepositoryTest<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseUnitTest<BaseRepository<Entity, EntityId>> {
    private static final @Unsigned int NUMBERS_OF_ENTITIES_WHEN_HIGH_TRAFFIC = 4444;

    protected abstract @NonNull EntityMocker<Entity, EntityId> createEntityMocker();

    private final @NonNull EntityMocker<Entity, EntityId> entityMocker = this.createEntityMocker();

    private final @NonNull EntityId entityId = this.entityMocker.createMockEntityId();
    private final @NonNull Entity entity = this.entityMocker.createMockEntityWithId(this.entityId);

    @Test
    public void WhenSavingEntity_ExpectPresentAndMatchingEntity() {
        this.testSubject.save(this.entity);
        assertTrue(this.testSubject.contains(this.entityId));

        val retrievedEntity = this.testSubject.getById(this.entityId);
        assertNotNull(retrievedEntity);
        assertEquals(retrievedEntity, this.entity);
    }

    @Test(dependsOnMethods = { "WhenSavingEntity_ExpectPresentAndMatchingEntity" })
    public void GivenExistingEntity_WhenRemovingEntity_ExpectAbsentEntity() {
        this.testSubject.delete(this.entityId);

        assertFalse(this.testSubject.contains(this.entityId));
        assertNull(this.testSubject.getById(this.entityId));
    }

    @Test(dependsOnMethods = { "GivenExistingEntity_WhenRemovingEntity_ExpectAbsentEntity" })
    public void GivenAbsentEntity_WhenRemovingEntity_ExpectAbsentEntity() {
        this.testSubject.delete(this.entityId);

        assertFalse(this.testSubject.contains(this.entityId));
        assertNull(this.testSubject.getById(this.entityId));
    }

    @Test(dependsOnMethods = { "GivenAbsentEntity_WhenRemovingEntity_ExpectAbsentEntity" })
    public void WhenSavingEntity_ExpectIncrementedSize() {
        val previousNumberOfEntities = this.testSubject.size();

        this.testSubject.save(this.entity);
        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities + 1);
        assertEquals(this.testSubject.show().size(), currentNumberOfEntities);
    }

    @Test(dependsOnMethods = { "WhenSavingEntity_ExpectIncrementedSize" })
    public void WhenRemovingEntity_ExpectDecrementedSize() {
        val previousNumberOfEntities = this.testSubject.size();

        this.testSubject.delete(this.entityId);
        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities - 1);
        assertEquals(this.testSubject.show().size(), currentNumberOfEntities);
    }

    @Test(dependsOnMethods = { "WhenSavingEntity_ExpectIncrementedSize" })
    public void GivenHighTraffic_WhenSavingEntities_ExpectCorrectSize() {
        val previousNumberOfEntities = this.testSubject.size();

        IntStream.range(0, NUMBERS_OF_ENTITIES_WHEN_HIGH_TRAFFIC)
            .parallel()
            .forEach(index -> {
                EntityId entityId;

                do {
                    entityId = this.entityMocker.createMockEntityId();
                } while (this.testSubject.contains(entityId));

                val entity = this.entityMocker.createMockEntityWithId(entityId);
                this.testSubject.save(entity);
            });

        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities + NUMBERS_OF_ENTITIES_WHEN_HIGH_TRAFFIC);
    }
}
