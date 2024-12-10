package org.tomfoolery.infrastructures.dataproviders.providers.io.file.apache.tika;

import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.tika.Tika;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;

@NoArgsConstructor(staticName = "of")
public class ApacheTikaFileVerifier implements FileVerifier {
    protected final @NonNull Tika tika = new Tika();

    @Override
    public boolean isDocument(byte @NonNull [] bytes) {
        val mimeType = this.tika.detect(bytes);
        return isDocumentMimeType(mimeType);
    }

    @Override
    public boolean isImage(byte @NonNull [] bytes) {
        val mimeType = this.tika.detect(bytes);
        return isImageMimeType(mimeType);
    }

    protected static boolean isDocumentMimeType(@NonNull String mimeType) {
        return mimeType.startsWith("application/")
            || mimeType.startsWith("text/");
    }

    protected static boolean isImageMimeType(@NonNull String mimeType) {
        return mimeType.startsWith("image/");
    }
}
