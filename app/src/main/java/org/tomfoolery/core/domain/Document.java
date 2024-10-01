package org.tomfoolery.core.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import org.tomfoolery.core.utils.id.Signature;
import org.tomfoolery.core.utils.id.SparseID;

import java.util.List;

@Data(staticConstructor = "of")
public class Document {
    @Setter(value = AccessLevel.NONE)
    private final @NonNull ID id;

    private @NonNull String title;
    private @NonNull String description;
    private @NonNull List<String> authors = List.of();

    private final @NonNull Signature<Patron.ID> borrowingPatronIds = Signature.of();

    @Value(staticConstructor = "of")
    public static class ID implements SparseID {
        @NonNull String value;
    }
}
