package org.tomfoolery.configurations.monolith.console.utils.helpers;

import com.github.lalyos.jfiglet.FigletFont;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.utils.helpers.loaders.ResourceLoader;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class FigletFormatter {
    public static @NonNull String format(@NonNull String content, @NonNull Font font) throws IOException {
        val fontAbsolutePath = ResourceLoader.getAbsolutePath(font.getPath());
        return FigletFont.convertOneLine(fontAbsolutePath, content);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PRIVATE)
    public enum Font {
        ANSI_SHADOW("console/fonts/ANSI-Shadow.flf");

        private final @NonNull String path;
    }
}
