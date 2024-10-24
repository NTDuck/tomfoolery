package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;
import org.tomfoolery.core.utils.enums.DocumentAttributeName;
import org.tomfoolery.infrastructures.dataproviders.inmemory.abc.BaseInMemorySearchableRepository;

import java.util.List;
import java.util.Map;

public class InMemoryDocumentRepository extends BaseInMemorySearchableRepository<Document, Document.Id, DocumentAttributeName> implements DocumentRepository {
    private InMemoryDocumentRepository() {
        super(Map.of(
            DocumentAttributeName.TITLE, document -> document.getMetadata().getTitle(),
            DocumentAttributeName.DESCRIPTION, document -> document.getMetadata().getDescription(),
            DocumentAttributeName.AUTHOR, document -> document.getMetadata().getAuthors(),
            DocumentAttributeName.GENRE, document -> document.getMetadata().getGenres()
        ), Map.of(
            SearchCriterion.SearchMethod.FULL_TEXT, (attribute, attributeValue) -> {
                if (!(attribute instanceof List<?> attributes))
                    return attribute == attributeValue;

                return attributes.contains(attributeValue);
            },
            SearchCriterion.SearchMethod.PREFIX, (attribute, attributeValue) -> {
                if (!(attribute instanceof List<?> attributes))
                    return ((String) attribute).startsWith((String) attributeValue);

                for (val attribute_ : attributes)
                    if (((String) attribute_).startsWith((String) attributeValue))
                        return true;

                return false;
            },
            SearchCriterion.SearchMethod.SUFFIX, (attribute, attributeValue) -> {
                if (!(attribute instanceof List<?> attributes))
                    return ((String) attribute).endsWith((String) attributeValue);

                for (val attribute_ : attributes)
                    if (((String) attribute_).endsWith((String) attributeValue))
                        return true;

                return false;
            }
        ));
    }

    public static @NonNull InMemoryDocumentRepository of() {
        return new InMemoryDocumentRepository();
    }
}
