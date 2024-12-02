package org.tomfoolery.core.dataproviders.generators;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface FileHandler {
    @NonNull String save(@NonNull String path, byte @NonNull [] content);
    void open(@NonNull String path);
    @NonNull String read(@NonNull String path);
}
