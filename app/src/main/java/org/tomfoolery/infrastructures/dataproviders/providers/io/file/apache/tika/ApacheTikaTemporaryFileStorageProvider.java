package org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@NoArgsConstructor(staticName = "of")
public class ApacheTikaTemporaryFileStorageProvider implements FileStorageProvider {
    private final @NonNull Tika tika = new Tika();

    public @NonNull String save(@NonNull String baseFileName, @NonNull String fileExtension, byte @NonNull [] bytes) throws IOException {
        val normalizedFileExtension = normalizeFileExtension(fileExtension);

        val filePath = Files.createTempFile(baseFileName, normalizedFileExtension).toAbsolutePath();
        Files.write(filePath, bytes);

        val file = filePath.toFile();
        file.deleteOnExit();

        return filePath.toAbsolutePath().toString();
    }

    @Override
    public @NonNull String save(byte @NonNull [] bytes) throws IOException {
        val baseFileName = getRandomBaseFileName();
        val fileExtension = this.detectFileExtension(bytes);

        return this.save(baseFileName, fileExtension, bytes);
    }

    public void open(@NonNull String filePathName) throws IOException {
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

    public byte @NonNull [] read(@NonNull String filePathName) throws IOException {
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

    private static @NonNull String normalizeFileExtension(@NonNull String fileExtension) {
        return fileExtension.startsWith(".") ? fileExtension : String.format(".%s", fileExtension);
    }

    @SneakyThrows
    private @NonNull String detectFileExtension(byte @NonNull [] bytes) {
        val mimeType = this.tika.detect(bytes);

        val mimeTypes = MimeTypes.getDefaultMimeTypes();
        val mediaType = mimeTypes.forName(mimeType);

        return mediaType.getExtension();
    }
}
