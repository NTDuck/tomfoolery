package org.tomfoolery.core.dataproviders.generators.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

@FunctionalInterface
public interface DocumentQrCodeGenerator {
    Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String documentUrl);
}
