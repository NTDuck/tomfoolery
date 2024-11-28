package org.tomfoolery.infrastructures.utils.helpers.io.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class FileManager {
    public static @NonNull String save(@NonNull String fileExtension, byte @NonNull [] content) throws IOException {
        val baseFileName = getRandomBaseFileName();
        val normalizedFileExtension = getNormalizedFileExtension(fileExtension);

        val filePath = Files.createTempFile(baseFileName, normalizedFileExtension).toAbsolutePath();
        Files.write(filePath, content);

        val file = filePath.toFile();
        file.deleteOnExit();

        return filePath.toAbsolutePath().toString();
    }

    public static void open(@NonNull String filePathName) throws IOException {
        if (!Desktop.isDesktopSupported())
            throw new IOException();

        val desktop = Desktop.getDesktop();

        if (!desktop.isSupported(Desktop.Action.OPEN))
            throw new IOException();

        val filePath = Path.of(filePathName).toAbsolutePath();

        if (!Files.exists(filePath))
            throw new IOException();

        val file = filePath.toFile();
        desktop.open(file);
    }

    public static byte @NonNull [] read(@NonNull String filePathName) throws IOException {
        val filePath = Path.of(filePathName).toAbsolutePath();

        try {
            return Files.readAllBytes(filePath);

        } catch (Exception exception) {
            throw new IOException();
        }
    }

    private static @NonNull String getRandomBaseFileName() {
        return UUID.randomUUID().toString();
    }

    private static @NonNull String getNormalizedFileExtension(@NonNull String fileExtension) {
        return fileExtension.startsWith(".") ? fileExtension : String.format(".%s", fileExtension);
    }
}
