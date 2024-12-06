package org.tomfoolery.configurations.monolith.console.utils.helpers;

import com.github.lalyos.jfiglet.FigletFont;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;

import java.io.IOException;
import java.net.URISyntaxException;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class FontFormatter {
    public static @NonNull String format(@NonNull String content) throws IOException {
        return FigletFont.convertOneLine(content);
    }

    public static @NonNull String format(@NonNull String content, @NonNull Font font) throws IOException, URISyntaxException {
        val fontAbsolutePath = ResourceProvider.getResourceAbsolutePath(font.getPath());
        return FigletFont.convertOneLine(fontAbsolutePath, content);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PRIVATE)
    public enum Font {
        ANSI_SHADOW("console/fonts/ANSI-Shadow.flf"),
        BLOODY("console/fonts/Bloody.flf");

        private final @NonNull String path;
    }
}