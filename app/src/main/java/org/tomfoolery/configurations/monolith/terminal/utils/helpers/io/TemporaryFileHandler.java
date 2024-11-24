package org.tomfoolery.configurations.monolith.terminal.utils.helpers.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class TemporaryFileHandler {
    private static final @NonNull String DIRECTORY = "./.tmp/";
    private static final @NonNull String BASENAME = "tomfoolery-temp-file-that-is-tomfoolery";

    public static void saveAndOpen(byte @NonNull [] bytes, @NonNull String fileExt) throws IOException {
        val filePathName = getFilePath(fileExt).toString();

        save(bytes, filePathName);
        open(filePathName);
    }

    public static byte @NonNull [] read(@NonNull String filePathName) throws IOException {
        val filePath = Path.of(filePathName);

        return Files.readAllBytes(filePath);
    }

    private static void save(byte @NonNull [] bytes, @NonNull String filePathName) throws IOException {
        val filePath = Path.of(filePathName);
        val directoryPath = filePath.getParent();

        Files.createDirectories(directoryPath);
        Files.write(filePath, bytes);
    }

    private static void open(@NonNull String filePathName) throws IOException {
        val file = new File(filePathName);

        if (!file.exists() || !Desktop.isDesktopSupported())
            return;

        val desktop = Desktop.getDesktop();
        desktop.open(file);
    }

    private static @NonNull Path getFilePath(@NonNull String fileExt) {
        val directoryPath = Path.of(DIRECTORY);
        val fileName = String.format("%s.%s", BASENAME, fileExt);

        return directoryPath.resolve(fileName);
    }
}
