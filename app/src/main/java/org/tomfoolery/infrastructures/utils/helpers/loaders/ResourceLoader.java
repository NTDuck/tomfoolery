package org.tomfoolery.infrastructures.utils.helpers.loaders;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ResourceLoader {
    public static @NonNull URL getUrl(@NonNull String resourceName) {
        return ClassLoader.getSystemResource(resourceName);
    }

    public static @NonNull Path getPath(@NonNull String resourceName) throws URISyntaxException {
        val resourceUrl = getUrl(resourceName);
        val resourceUri = resourceUrl.toURI();

        return Paths.get(resourceUri);
    }

    public static @NonNull String getAbsolutePath(@NonNull String resourceName) throws URISyntaxException {
        val resourcePath = getPath(resourceName);
        return resourcePath.toAbsolutePath().toString();
    }
}
