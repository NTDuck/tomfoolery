package org.tomfoolery.infrastructures.dataproviders.generators.zxing.documents.references;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
public class ZxingDocumentQrCodeGenerator implements DocumentQrCodeGenerator {
    private static final @NonNull String QR_CODE_EXTENSION = "png";
    private static final @Unsigned int IMAGE_SIZE = 512;

    private static final @NonNull Map<EncodeHintType, ?> HINTS = Map.of(
        EncodeHintType.MARGIN, 4
    );

    @Override
    @SneakyThrows
    public Document.@NonNull QrCode generateQrCodeFromUrl(@NonNull String documentUrl) {
        val bitMatrix = generateBitMatrixFromText(documentUrl);
        val imageBuffer = getBytesFromBitMatrix(bitMatrix);

        return Document.QrCode.of(imageBuffer);
    }

    private static @NonNull BitMatrix generateBitMatrixFromText(@NonNull String text) throws WriterException {
        val qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, IMAGE_SIZE, IMAGE_SIZE, HINTS);
    }

    private static byte @NonNull [] getBytesFromBitMatrix(@NonNull BitMatrix bitMatrix) throws IOException {
        @Cleanup val byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, QR_CODE_EXTENSION, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
