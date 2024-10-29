package org.tomfoolery.infrastructures.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class Base64Encoder {
    public static @NonNull String encode(@NonNull Serializable object) throws IOException {
        val byteArrayOutputStream = new ByteArrayOutputStream();
        val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(object);

        val encoder = Base64.getEncoder();
        val byteArray = byteArrayOutputStream.toByteArray();

        return encoder.encodeToString(byteArray);
    }

    public static @NonNull Object decode(@NonNull String base64String) throws IOException, ClassNotFoundException {
        val decoder = Base64.getDecoder();
        val byteArray = decoder.decode(base64String);

        val byteArrayInputStream = new ByteArrayInputStream(byteArray);
        val objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return objectInputStream.readObject();
    }
}
