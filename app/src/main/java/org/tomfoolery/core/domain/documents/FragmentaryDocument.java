package org.tomfoolery.core.domain.documents;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;

@Data
public final class FragmentaryDocument {
    private final Document.@NonNull Id id;
    private Document.@NonNull Metadata metadata;
    private Document.@NonNull Audit audit;

    public static @NonNull FragmentaryDocument of(@NonNull Document document) {
        return new FragmentaryDocument(document);
    }

    private FragmentaryDocument(@NonNull Document document) {
        this.id = document.getId();
        this.metadata = document.getMetadata();
        this.audit = document.getAudit();
    }
}
