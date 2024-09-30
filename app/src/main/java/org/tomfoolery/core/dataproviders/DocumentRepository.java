package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;

import java.util.Collection;

public interface DocumentRepository extends Repository<Document, Document.ID> {
    Collection<Document> searchByTitle(@NonNull String title);
    Collection<Document> searchByAuthor(@NonNull String author);

    Collection<Document> searchByPatron(@NonNull Patron.ID patronId);
}
