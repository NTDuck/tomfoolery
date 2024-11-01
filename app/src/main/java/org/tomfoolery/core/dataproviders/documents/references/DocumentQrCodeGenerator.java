package org.tomfoolery.core.dataproviders.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

public interface DocumentQrCodeGenerator {
    Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String url);
    @NonNull String generateUrlFromQrCode(Document.@NonNull QrCode qrCode);
}
