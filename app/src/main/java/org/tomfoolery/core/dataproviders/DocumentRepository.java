package org.tomfoolery.core.dataproviders;

import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;

import java.util.Collection;

public interface DocumentRepository extends Repository<Document, Document.ID> {
    Collection<Document> searchByTitle(String title);
    Collection<Document> searchByAuthor(String author);
    Collection<Document> searchByGenres(Collection<String> genres);
    Collection<Document> searchByPatron(Patron.ID patronId);
}
