package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.val;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Staff;

import static org.testng.Assert.*;

public class InMemoryDocumentRepositoryTest {
    private DocumentRepository documentRepository;

    @BeforeMethod
    public void setUp() {
        this.documentRepository = InMemoryDocumentRepository.of();

        for (int i = 0; i < 2048; ++i) {
            val documentId = Document.Id.of(String.format("id-%d", i));
            val documentMetadata = Document.Metadata.of(String.format("Title-%d", i), String.format("Description-%d", i));
            val documentAudit = Document.Audit.of(Staff.Id.of());

            val documentAuthors = documentMetadata.getAuthors();
            for (int j = 0; j < i % 4; ++j)
                documentAuthors.add(String.format("Author-%d", j));

            val documentGenres = documentMetadata.getGenres();
            for (int j = 0; j < i % 7; ++j)
                documentGenres.add(String.format("Genre-%d", j));

            val document = Document.of(documentId, documentMetadata, documentAudit);
            documentRepository.save(document);
        }
    }

    @AfterMethod
    public void tearDown() {
        this.documentRepository = null;
    }

    @Test
    public void test() {
        val paginatedDocuments = this.documentRepository.showPaginatedEntities(1, 6);
        Reporter.log(paginatedDocuments.toString(), true);
    }
}
