package org.tomfoolery.core.dataproviders.providers.io.file;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface FileVerifier {
    boolean isDocument(byte @NonNull [] bytes);
    boolean isImage(byte @NonNull [] bytes);
}
