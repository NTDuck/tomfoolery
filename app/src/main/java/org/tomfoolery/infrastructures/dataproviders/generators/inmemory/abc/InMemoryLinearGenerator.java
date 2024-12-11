package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.utils.contracts.ddd;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class InMemoryLinearGenerator<Entity extends ddd.Entity<EntityId>, EntityId extends ddd.EntityId> implements BaseSynchronizedGenerator<Entity, EntityId> {
    protected final @NonNull Set<Entity> cachedEntities;

    protected InMemoryLinearGenerator(@NonNull Comparator<Entity> comparator) {
        this.cachedEntities = new ConcurrentSkipListSet<>(comparator);
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Entity savedEntity) {
        this.cachedEntities.add(savedEntity);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Entity deletedEntity) {
        this.cachedEntities.remove(deletedEntity);
    }

    protected static boolean isSubsequence(@NonNull CharSequence subsequence, @NonNull CharSequence sequence) {
        val lengthOfSubsequence = subsequence.length();
        val lengthOfSequence = sequence.length();

        var indexOfSubsequence = 0;
        var indexOfSequence = 0;

        while (indexOfSubsequence < lengthOfSubsequence && indexOfSequence < lengthOfSequence) {
            if (subsequence.charAt(indexOfSubsequence) == sequence.charAt(indexOfSequence))
                indexOfSubsequence++;

            indexOfSequence++;
        }

        return indexOfSubsequence == lengthOfSubsequence;
    }

    protected static boolean isSubsequence(@NonNull CharSequence subsequence, @NonNull Collection<? extends CharSequence> sequences) {
        return sequences.parallelStream()
            .anyMatch(sequence -> isSubsequence(subsequence, sequence));
    }
}
