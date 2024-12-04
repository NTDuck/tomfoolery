package org.tomfoolery.infrastructures.dataproviders.providers.resources;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ResourceProvider {
    public static @NonNull URL getResourceUrl(@NonNull String resourceName) {
        return ClassLoader.getSystemResource(resourceName);
    }

    public static @NonNull Path getResourcePath(@NonNull String resourceName) throws URISyntaxException {
        val resourceUrl = getResourceUrl(resourceName);
        val resourceUri = resourceUrl.toURI();

        return Paths.get(resourceUri);
    }

    public static @NonNull String getResourceAbsolutePath(@NonNull String resourceName) throws URISyntaxException {
        val resourcePath = getResourcePath(resourceName);
        return resourcePath.toAbsolutePath().toString();
    }
}
