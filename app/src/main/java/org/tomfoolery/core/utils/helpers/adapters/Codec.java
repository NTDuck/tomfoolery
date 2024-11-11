package org.tomfoolery.core.utils.helpers.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.NONE)
public class Codec {
    private static final @NonNull Charset charset = StandardCharsets.UTF_8;

    public static char @NonNull [] charsFromBytes(byte @NonNull [] bytes) {
        val byteBuffer = ByteBuffer.wrap(bytes);
        val charBuffer = charset.decode(byteBuffer);

        val chars = new char[charBuffer.remaining()];
        charBuffer.get(chars);

        return chars;
    }

    public static byte @NonNull [] bytesFromChars(char @NonNull [] chars) {
        val charBuffer = CharBuffer.wrap(chars);
        val byteBuffer = charset.encode(charBuffer);

        val bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        Arrays.fill(byteBuffer.array(), (byte) 0);   // Manually clear possibly sensitive data

        return bytes;
    }

    public static @NonNull CharSequence charSequenceFromChars(char @NonNull [] chars) {
        return CharBuffer.wrap(chars);
    }

    public static char @NonNull [] charsFromCharSequence(@NonNull CharSequence charSequence) {
        val charBuffer = CharBuffer.wrap(charSequence);

        val chars = new char[charBuffer.remaining()];
        charBuffer.get(chars);

        return chars;
    }
}
