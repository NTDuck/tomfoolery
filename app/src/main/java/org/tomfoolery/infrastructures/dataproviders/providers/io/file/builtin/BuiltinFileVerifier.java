package org.tomfoolery.infrastructures.dataproviders.providers.io.file.builtin;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;

import java.util.Locale;

@NoArgsConstructor(staticName = "of")
public class BuiltinFileVerifier implements FileVerifier {
    private static final @Unsigned int HEADER_LENGTH = 4;

    @Override
    public boolean isDocument(byte @NonNull [] bytes) {
        if (bytes.length < HEADER_LENGTH)
            return false;

        val header = calculateDocumentHeader(bytes);

        return header.startsWith("%pdf")            // PDF
            || header.startsWith("{\\rt")           // RTF
            || header.startsWith("<htm")            // HTML
            || header.startsWith("<?xm");           // XML
    }

    @Override
    public boolean isImage(byte @NonNull [] bytes) {
        if (bytes.length < HEADER_LENGTH)
            return false;

        val header = calculateImageHeader(bytes);

        return header == 0x89504E47                 // PNG
            || (header & 0xFFFF0000) == 0xFFD80000  // JPEG
            || header == 0x47494638                 // GIF
            || header == 0x424D0000;                // BMP
    }

    private static @NonNull String calculateDocumentHeader(byte @NonNull [] bytes) {
        return new String(bytes, 0, HEADER_LENGTH)
            .toLowerCase(Locale.ROOT);
    }

    private static @Unsigned int calculateImageHeader(byte @NonNull [] bytes) {
        return (bytes[0] & 0xFF) << 24
             | (bytes[1] & 0xFF) << 16
             | (bytes[2] & 0xFF) << 8
             | (bytes[3] & 0xFF);
    }
}
