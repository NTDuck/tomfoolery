package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.util.Collection;

public interface DocumentRepository extends Repository<Document, Document.Id> {
    @NonNull Collection<Document> searchByTitle(@NonNull String title);
    @NonNull Collection<Document> searchByAuthor(@NonNull String author);
    @NonNull Collection<Document> searchByGenres(@NonNull Collection<String> genres);
    @NonNull Collection<Document> searchByPatron(ReadonlyUser.@NonNull Id patronId);
}
