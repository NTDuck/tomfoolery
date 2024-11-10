package org.tomfoolery.infrastructures.utils.helpers.base64;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class Base64Encoder {
    private static final Base64.@NonNull Encoder encoder = Base64.getEncoder();
    private static final Base64.@NonNull Decoder decoder = Base64.getDecoder();

    public static @NonNull CharSequence encode(@NonNull CharSequence rawCharSequence) {
        val rawBytes = getByteArrayFromCharSequence(rawCharSequence);

        val encodedBytes = encoder.encode(rawBytes);
        val encodedChars = getCharArrayFromByteArray(encodedBytes);

        return CharBuffer.wrap(encodedChars);
    }

    public static @NonNull CharSequence encode(@NonNull Serializable object) throws IOException {
        @Cleanup val byteArrayOutputStream = new ByteArrayOutputStream();
        @Cleanup val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);

        val encodedBytes = encoder.encode(byteArrayOutputStream.toByteArray());
        val encodedChars = getCharArrayFromByteArray(encodedBytes);

        return CharBuffer.wrap(encodedChars);
    }

    public static @NonNull Object decode(@NonNull CharSequence encodedCharSequence) throws IOException, ClassNotFoundException {
        val encodedBytes = getByteArrayFromCharSequence(encodedCharSequence);
        val decodedBytes = decoder.decode(encodedBytes);

        @Cleanup val byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
        @Cleanup val objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return objectInputStream.readObject();
    }

    private static char @NonNull [] getCharArrayFromByteArray(byte @NonNull [] bytes) {
        val chars = new char[bytes.length];

        for (int index = 0; index < bytes.length; index++)
            chars[index] = (char) (bytes[index] & 0xFF);   // Mask to treat byte as unsigned char

        return chars;
    }

    private static byte @NonNull [] getByteArrayFromCharSequence(@NonNull CharSequence charSequence) {
        val bytes = new byte[charSequence.length()];

        for (int index = 0; index < charSequence.length(); index++)
            bytes[index] = (byte) charSequence.charAt(index);

        return bytes;
    }
}
