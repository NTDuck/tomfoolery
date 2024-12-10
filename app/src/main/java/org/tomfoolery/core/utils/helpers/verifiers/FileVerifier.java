package org.tomfoolery.core.utils.helpers.verifiers;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface FileVerifier {
    boolean isDocument(byte @NonNull [] content);
    boolean isImage(byte @NonNull [] content);
}
