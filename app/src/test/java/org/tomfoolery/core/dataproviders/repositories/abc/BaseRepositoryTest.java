package org.tomfoolery.core.dataproviders.repositories.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository" }, singleThreaded = true)
public abstract class BaseRepositoryTest<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> extends BaseUnitTest<BaseRepository<Entity, EntityId>> {
    private static final @Unsigned int NUMBERS_OF_ENTITIES_HIGH_TRAFFIC = 4444;

    private @NonNull EntityId entityId;
    private @NonNull Entity entity;

    protected abstract @NonNull EntityId createRandomMockEntityId();
    protected abstract @NonNull Entity createMockEntityFromId(@NonNull EntityId entityId);

    @BeforeClass
    @Override
    protected void setUp() {
        super.setUp();

        this.entityId = this.createRandomMockEntityId();
        this.entity = this.createMockEntityFromId(this.entityId);
    }

    @Test
    public void WhenSavingEntity_ExpectPresentAndMatchingEntity() {
        this.testSubject.save(this.entity);
        assertTrue(this.testSubject.contains(this.entityId));

        val retrievedEntity = this.testSubject.getById(this.entityId);
        assertNotNull(retrievedEntity);
        assertEquals(retrievedEntity, this.entity);
    }

    @Test
    public void GivenExistingEntity_WhenRemovingEntity_ExpectAbsentEntity() {
        this.testSubject.delete(this.entityId);

        assertFalse(this.testSubject.contains(this.entityId));
        assertNull(this.testSubject.getById(this.entityId));
    }

    @Test
    public void GivenAbsentEntity_WhenRemovingEntity_ExpectAbsentEntity() {
        this.testSubject.delete(this.entityId);

        assertFalse(this.testSubject.contains(this.entityId));
        assertNull(this.testSubject.getById(this.entityId));
    }

    @Test
    public void WhenSavingEntity_ExpectIncrementedSize() {
        val previousNumberOfEntities = this.testSubject.size();

        this.testSubject.save(this.entity);
        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities + 1);
        assertEquals(this.testSubject.show().size(), currentNumberOfEntities);
    }

    @Test
    public void WhenRemovingEntity_ExpectDecrementedSize() {
        val previousNumberOfEntities = this.testSubject.size();

        this.testSubject.delete(this.entityId);
        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities - 1);
        assertEquals(this.testSubject.show().size(), currentNumberOfEntities);
    }

    @Test
    public void GivenHighTraffic_WhenSavingEntities_ExpectCorrectSize() {
        val previousNumberOfEntities = this.testSubject.size();

        IntStream.range(0, NUMBERS_OF_ENTITIES_HIGH_TRAFFIC)
            .parallel()
            .forEach(index -> {
                EntityId entityId;

                do {
                    entityId = this.createRandomMockEntityId();
                } while (this.testSubject.contains(entityId));

                val entity = this.createMockEntityFromId(entityId);
                this.testSubject.save(entity);
            });

        val currentNumberOfEntities = this.testSubject.size();

        assertEquals(currentNumberOfEntities, previousNumberOfEntities + NUMBERS_OF_ENTITIES_HIGH_TRAFFIC);
    }
}
