package org.tomfoolery.core.utils.id;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

@NoArgsConstructor(staticName = "of")
public class Signature<ID extends CompactID> implements Collection<ID> {
    private final BitSet bitSet = new BitSet();

    private int getBitIndexFromId(@NonNull ID id) {
        return id.getValue();
    }

//    protected ID generateIdFromBitIndex(int bitIndex);

    @Override
    public int size() {
        return this.bitSet.cardinality();
    }

    @Override
    public boolean isEmpty() {
        return this.bitSet.isEmpty();
    }

    @Override
    public boolean contains(@NonNull Object object) {
        if (!(object instanceof CompactID))
            return false;

        @SuppressWarnings("unchecked")
        val id = (ID) object;
        return this.contains(id);
    }

    public boolean contains(@NonNull ID id) {
        val bitIndex = this.getBitIndexFromId(id);
        return this.bitSet.get(bitIndex);
    }

    @Override
    public @NonNull Iterator<ID> iterator() {
//        return new Iterator<>() {
//            private int currentBitIndex = bitSet.nextSetBit(0);
//
//            @Override
//            public boolean hasNext() {
//                return this.currentBitIndex != -1;
//            }
//
//            @Override
//            public ID next() {
//                if (!this.hasNext())
//                    throw new NoSuchElementException();
//
//                val id = generateIdFromBitIndex(currentBitIndex);
//                currentBitIndex = bitSet.nextSetBit(currentBitIndex + 1);
//                return id;
//            }
//        };
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Object[] toArray() {
        return this.toArray(Object[]::new);
    }

    @Override
    public <T> @NonNull T[] toArray(@NonNull T[] array) {
//        val ids = new ArrayList<>();
//
//        for (var index = this.bitSet.nextSetBit(0); index != -1; index = this.bitSet.nextSetBit(index + 1))
//            ids.add(this.generateIdFromBitIndex(index));
//
//        return ids.toArray(array);

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(@NonNull ID id) {
        val bitIndex = this.getBitIndexFromId(id);

        if (this.bitSet.get(bitIndex))
            return false;

        this.bitSet.set(bitIndex);
        return true;
    }

    @Override
    public boolean remove(@NonNull Object object) {
        if (!(object instanceof CompactID))
            return false;

        @SuppressWarnings("unchecked")
        val id = (ID) object;
        return this.remove(id);
    }

    public boolean remove(@NonNull ID id) {
        val bitIndex = this.getBitIndexFromId(id);

        if (!this.bitSet.get(bitIndex))
            return false;

        this.bitSet.clear(bitIndex);
        return true;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        for (Object object : collection)
            if (!this.contains(object))
                return false;

        return true;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends ID> collection) {
        var flag = false;

        for (ID id : collection)
            if (this.add(id))
                flag = true;

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        var flag = false;

        for (Object object : collection)
            if (this.remove(object))
                flag = true;

        return flag;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        Signature<ID> other = Signature.of();

        for (Object object : collection) {
            if (!(object instanceof CompactID))
                continue;

            @SuppressWarnings("unchecked")
            val id = (ID) object;
            other.add(id);
        }

        val flag = !this.bitSet.equals(other.bitSet);
        this.bitSet.and(other.bitSet);

        return flag;
    }

    @Override
    public void clear() {
        this.bitSet.clear();
    }
}
