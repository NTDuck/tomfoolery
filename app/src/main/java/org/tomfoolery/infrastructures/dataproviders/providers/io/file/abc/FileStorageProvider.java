package org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

public interface FileStorageProvider {
    @NonNull String save(byte @NonNull [] bytes) throws IOException;
    void open(@NonNull String filePathName) throws IOException;
    byte @NonNull [] read(@NonNull String filePathName) throws IOException;
}
