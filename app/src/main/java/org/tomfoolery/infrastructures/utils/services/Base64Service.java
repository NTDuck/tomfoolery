package org.tomfoolery.infrastructures.utils.services;

import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;

public interface Base64Service {
    static @NonNull String convertNormalStringToBase64String(@NonNull String normalString) {
        return Base64.getEncoder().encodeToString(normalString.getBytes());
    }

    static @NonNull String convertBase64StringToNormalString(@NonNull String base64String) {
        return Arrays.toString(Base64.getDecoder().decode(base64String));
    }

    @SneakyThrows
    static @NonNull String convertObjectToBase64String(@NonNull Object object) throws IOException {
        val byteArrayOutputStream = new ByteArrayOutputStream();

        val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    @SneakyThrows
    static @NonNull Object convertBase64StringToObject(@NonNull String base64String) {
        val byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64String));

        val objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }
}
