package org.tomfoolery.core.utils.dataclasses.auth.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.util.Arrays;

@Value(staticConstructor = "of")
public class SecureString implements CharSequence, AutoCloseable {
    @Getter(AccessLevel.NONE)
    transient char @NonNull [] value;

    private SecureString(char @NonNull [] value) {
        this.value = value.clone();
    }

    @Override
    public int length() {
        return this.value.length;
    }

    @Override
    public char charAt(@Unsigned int index) {
        if (index < 0 || index >= this.value.length)
            throw new IndexOutOfBoundsException();

        return this.value[index];
    }

    @Override
    public @NonNull SecureString subSequence(@Unsigned int start, @Unsigned int end) {
        if (start < 0 || end > this.value.length || start > end)
            throw new IndexOutOfBoundsException();

        val subArray = new char[end - start];
        System.arraycopy(value, start, subArray, 0, end - start);

        // More readable, but less performant
        // val subArray = Arrays.copyOfRange(this.value, start, end);

        return new SecureString(subArray);
    }

    @Override
    public void close() {
        Arrays.fill(this.value, '\0');
    }

    @Override
    public @NonNull String toString() {
        return "";
    }
}
