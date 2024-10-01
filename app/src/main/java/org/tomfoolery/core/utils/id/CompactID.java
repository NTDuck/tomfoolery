package org.tomfoolery.core.utils.id;

public interface CompactID {
    default int getValue() {
        return this.hashCode();
    }
}
