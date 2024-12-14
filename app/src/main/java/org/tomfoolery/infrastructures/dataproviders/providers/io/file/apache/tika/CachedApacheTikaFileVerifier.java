package org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(staticName = "of")
public class CachedApacheTikaFileVerifier extends ApacheTikaFileVerifier {
    private final Map<Integer, String> mimeCache = new ConcurrentHashMap<>();

    @Override
    public boolean isDocument(byte @NonNull [] bytes) {
        val mimeType = this.detectMimeType(bytes);
        return isDocumentMimeType(mimeType);
    }

    @Override
    public boolean isImage(byte @NonNull [] bytes) {
        val mimeType = this.detectMimeType(bytes);
        return isImageMimeType(mimeType);
    }

    private @NonNull String detectMimeType(byte @NonNull [] bytes) {
        val hash = Arrays.hashCode(bytes);   // Hashing is probably always faster than detecting
        return this.mimeCache.computeIfAbsent(hash, key -> this.tika.detect(bytes));
    }
}