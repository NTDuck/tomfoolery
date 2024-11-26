package org.tomfoolery.core.dataproviders.generators.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.domain.documents.Document;

@FunctionalInterface
public interface DocumentQrCodeGenerator extends BaseGenerator {
    @NonNull String QR_CODE_EXTENSION = "png";

    Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String documentUrl);
}
