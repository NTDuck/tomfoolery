package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Data(staticConstructor = "of")
public class Staff {
    private final @NonNull ID id = ID.of();

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull UUID value = UUID.randomUUID();
    }
}
