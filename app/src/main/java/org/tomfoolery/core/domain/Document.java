package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data(staticConstructor = "of")
public class Document {
    private final @NonNull ID id;

    private @NonNull String title;
    private @NonNull String description;
    private @NonNull List<String> authors = new ArrayList<>();

    private transient @NonNull BufferedImage qrCode;

//    private final @NonNull Signature<Patron.ID> borrowingPatronIds = Signature.of();
    private final @NonNull Collection<Patron.ID> borrowingPatronIds = new HashSet<>();

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull String value;
    }
}
