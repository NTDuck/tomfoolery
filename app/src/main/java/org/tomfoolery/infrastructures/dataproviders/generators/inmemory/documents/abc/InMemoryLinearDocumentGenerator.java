package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc;

import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.InMemoryLinearGenerator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

public class InMemoryLinearDocumentGenerator extends InMemoryLinearGenerator<Document, Document.Id> {
    protected InMemoryLinearDocumentGenerator() {
        super(DocumentComparator.byIdAscending);
    }
}
