package org.tomfoolery.infrastructures.utils.helpers.loaders;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ResourceLoader {
    public static @NonNull URL getUrl(@NonNull String resourceName) {
        return ClassLoader.getSystemResource(resourceName);
    }

    public static @NonNull Path getPath(@NonNull String resourceName) {
        val resourceUrl = getUrl(resourceName);
        val resourceUrlPath = resourceUrl.getPath();

        return Paths.get(resourceUrlPath);
    }

    public static @NonNull String getAbsolutePath(@NonNull String resourceName) {
        val resourcePath = getPath(resourceName);
        return resourcePath.toAbsolutePath().toString();
    }
}
