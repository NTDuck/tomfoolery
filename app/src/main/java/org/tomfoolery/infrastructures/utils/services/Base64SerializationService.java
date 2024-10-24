package org.tomfoolery.infrastructures.utils.services;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.util.Base64;

public interface Base64SerializationService {
    static @NonNull String serialize(@NonNull Serializable object) throws IOException {
        val byteArrayOutputStream = new ByteArrayOutputStream();
        val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(object);

        val encoder = Base64.getEncoder();
        val byteArray = byteArrayOutputStream.toByteArray();

        return encoder.encodeToString(byteArray);
    }

    static @NonNull Object deserialize(@NonNull String base64String) throws IOException, ClassNotFoundException {
        val decoder = Base64.getDecoder();
        val byteArray = decoder.decode(base64String);

        val byteArrayInputStream = new ByteArrayInputStream(byteArray);
        val objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return objectInputStream.readObject();
    }
}
