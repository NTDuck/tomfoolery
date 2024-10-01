package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.utils.id.CompactID;

@Data(staticConstructor = "of")
public class Staff {
    private final @NonNull ID id = ID.of();

    @Value(staticConstructor = "of")
    public static class ID implements CompactID {
        private static int size = 0;
        int value = ++size;
    }
}
