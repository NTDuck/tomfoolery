package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;

import java.util.Iterator;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository" })
public abstract class BaseRepositoryTest<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseUnitTest<BaseRepository<Entity, EntityId>> {
    private static final @Unsigned int NUMBER_OF_MOCK_ENTITIES = 1;
    private static final @Unsigned int NUMBERS_OF_ENTITIES_WHEN_HIGH_TRAFFIC = 4444;

    protected abstract @NonNull EntityMocker<Entity, EntityId> createEntityMocker();

    private final @NonNull EntityMocker<Entity, EntityId> entityMocker = this.createEntityMocker();

    @Test(dataProvider = "BaseRepositoryTestDataProvider")
    void WhenSavingEntity_ExpectPresentAndMatchingEntity(@NonNull Entity entity) {
        this.testSubject.save(entity);
        this.assertPresentAndMatchingEntity(entity);
    }

    @Test(dataProvider = "BaseRepositoryTestDataProvider", dependsOnMethods = { "WhenSavingEntity_ExpectPresentAndMatchingEntity" })
    void GivenPresentEntity_WhenSavingEntity_ExpectPresentAndMatchingEntity(@NonNull Entity entity) {
        this.testSubject.save(entity);
        this.assertPresentAndMatchingEntity(entity);
    }

    @Test(dataProvider = "BaseRepositoryTestDataProvider", dependsOnMethods = { "GivenPresentEntity_WhenSavingEntity_ExpectPresentAndMatchingEntity" })
    void GivenPresentEntity_WhenRemovingEntity_ExpectAbsentEntity(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.testSubject.delete(entityId);
        this.assertAbsentEntity(entityId);
    }

    @Test(dataProvider = "BaseRepositoryTestDataProvider", dependsOnMethods = { "GivenPresentEntity_WhenRemovingEntity_ExpectAbsentEntity" })
    void GivenAbsentEntity_WhenRemovingEntity_ExpectAbsentEntity(@NonNull Entity entity) {
        val entityId = entity.getId();
        this.testSubject.delete(entityId);
        this.assertAbsentEntity(entityId);
    }

    @Test(dependsOnMethods = { "GivenAbsentEntity_WhenRemovingEntity_ExpectAbsentEntity" }, enabled = false)
    void GivenHighTraffic_WhenSavingEntities_ExpectCorrectSize() {
        val previousNumberOfEntities = this.testSubject.size();

        IntStream.range(0, NUMBERS_OF_ENTITIES_WHEN_HIGH_TRAFFIC).parallel()
            .forEach(_ -> {
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

    private void assertAbsentEntity(@NonNull EntityId entityId) {
        assertFalse(this.testSubject.contains(entityId));
        assertNull(this.testSubject.getById(entityId));
    }

    private void assertPresentAndMatchingEntity(@NonNull Entity expectedEntity) {
        val expectedEntityId = expectedEntity.getId();
        assertTrue(this.testSubject.contains(expectedEntityId));

        val retrievedEntity = this.testSubject.getById(expectedEntityId);
        assertNotNull(retrievedEntity);

        val retrievedEntityId = retrievedEntity.getId();
        assertEquals(retrievedEntityId, expectedEntityId);
        assertEquals(retrievedEntity, expectedEntity);
    }

    @DataProvider(name = "BaseRepositoryTestDataProvider")
    public @NonNull Iterator<Object[]> createData() {
        return IntStream.range(0, NUMBER_OF_MOCK_ENTITIES).parallel()
            .mapToObj(_ -> this.entityMocker.createMockEntity())
            .map(entity -> new Object[] { entity })
            .iterator();
    }
}
