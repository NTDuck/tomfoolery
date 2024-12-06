package org.tomfoolery.core.utils.dataclasses.users.authentication.security;

import lombok.Getter;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.utils.helpers.adapters.Codec;

import java.util.Arrays;

@Getter
public final class SecureString implements CharSequence, Comparable<SecureString>, AutoCloseable {
    private final transient char @NonNull [] chars;

    public static @NonNull SecureString of(char @NonNull [] chars) {
        return new SecureString(chars);
    }

    public static @NonNull SecureString of(@NonNull CharSequence charSequence) {
        return new SecureString(charSequence);
    }

    private SecureString(char @NonNull [] chars) {
        this.chars = chars.clone();
    }

    private SecureString(@NonNull CharSequence charSequence) {
        this.chars = Codec.charsFromCharSequence(charSequence);
    }

    @Override
    public int length() {
        return this.chars.length;
    }

    @Override
    public char charAt(@Unsigned int index) {
        if (index < 0 || index >= this.chars.length)
            throw new IndexOutOfBoundsException();

        return this.chars[index];
    }

    @Override
    public @NonNull SecureString subSequence(@Unsigned int start, @Unsigned int end) {
        if (start < 0 || end > this.chars.length || start > end)
            throw new IndexOutOfBoundsException();

        val subArray = new char[end - start];
        System.arraycopy(chars, start, subArray, 0, end - start);

        // More readable, but less performant
        // val subArray = Arrays.copyOfRange(this.value, start, end);

        return new SecureString(subArray);
    }

    @Override
    public int compareTo(@NonNull SecureString other) {
        return CharSequence.compare(this, other);
    }

    @Override
    public void close() {
        Arrays.fill(this.chars, '\0');
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other == null)
            return false;

        if (!(other instanceof SecureString otherSecureString))
            return false;

        return this.compareTo(otherSecureString) == 0;
    }

    @Override
    public @NonNull String toString() {
        return "";
    }
}
