package org.tomfoolery.infrastructures.utils.helpers.verifiers.apache.tika;

import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.tika.Tika;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.helpers.verifiers.FileVerifier;

@NoArgsConstructor(staticName = "of")
public class ApacheTikaFileVerifier implements FileVerifier {
    private final @NonNull Tika tika = new Tika();

    @Override
    public boolean isDocument(byte @NonNull [] content) {
        val mimeType = this.tika.detect(content);
        return isDocumentMimeType(mimeType);
    }

    @Override
    public boolean isImage(byte @NonNull [] content) {
        val mimeType = this.tika.detect(content);
        return isImageMimeType(mimeType);
    }

    private static boolean isDocumentMimeType(@NonNull String mimeType) {
        return mimeType.startsWith("application/") || mimeType.startsWith("text/");
    }

    private static boolean isImageMimeType(@NonNull String mimeType) {
        return mimeType.startsWith("image/");
    }
}
