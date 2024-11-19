package org.tomfoolery.infrastructures.dataproviders.generators.qrgen.documents.references;

import lombok.NoArgsConstructor;
import lombok.val;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.domain.documents.Document;

@NoArgsConstructor(staticName = "of")
public class QrgenDocumentQrCodeGenerator implements DocumentQrCodeGenerator {
    @Override
    public Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String documentUrl) {
        val bytes = QRCode.from(documentUrl).to(ImageType.PNG)
            .stream().toByteArray();
        return Document.QrCode.of(bytes);
    }
}
