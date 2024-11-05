package org.tomfoolery.infrastructures.dataproviders.generators.qrgen.documents.references;

import lombok.val;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.domain.documents.Document;

public class QrgenDocumentQrCodeGenerator implements DocumentQrCodeGenerator {
    @Override
    public Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String documentUrl) {
        val buffer = QRCode.from(documentUrl).to(ImageType.PNG).stream().toByteArray();
        return Document.QrCode.of(buffer);
    }
}
