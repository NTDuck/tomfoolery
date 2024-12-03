package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.*;

public class CloudDocumentRepositoryTest {
    private CloudDocumentRepository repository;

    @BeforeClass
    public void setup() throws IOException {
        Path configFilePath = Path.of("src/main/resources/config.properties");
        CloudDatabaseConfig dbConfig = new CloudDatabaseConfig(configFilePath.toString());
        repository = new CloudDocumentRepository(dbConfig);
    }

    @Test
    public void testSaveAndGetById() {
        Document.Id docId = Document.Id.of("ISBN-TEST-001");
        Document.Metadata metadata = Document.Metadata.of(
                "Test Book",
                "A test description",
                List.of("Test Author"),
                List.of("Test Genre"),
                Year.of(2023),
                "Test Publisher",
                Document.Metadata.CoverImage.of("test cover image".getBytes())
        );

        Document.Content content = Document.Content.of("Test content".getBytes(StandardCharsets.UTF_8));

        Document.Audit audit = Document.Audit.of(
                Staff.Id.of(UUID.randomUUID()),
                AverageRating.of(4.5),
                Document.Audit.Timestamps.of(Instant.now())
        );

        Document testDocument = Document.of(docId, content, metadata, audit);

        repository.save(testDocument);

        Document retrievedDocument = repository.getById(docId);

        assertNotNull(retrievedDocument);
        assertEquals(retrievedDocument.getId(), docId);
        assertEquals(retrievedDocument.getMetadata().getTitle(), "Test Book");
    }


    @Test
    public void testDeleteById() {
        Document.Id docId = Document.Id.of("ISBN-TEST-DELETE");
        Document.Metadata metadata = Document.Metadata.of(
                "Delete Test Book",
                "A book to be deleted",
                List.of("Delete Author"),
                List.of("Delete Genre"),
                Year.of(2023),
                "Delete Publisher",
                Document.Metadata.CoverImage.of("delete cover image".getBytes())
        );

        Document.Content content = Document.Content.of("Delete content".getBytes(StandardCharsets.UTF_8));

        Document.Audit audit = Document.Audit.of(
                Staff.Id.of(UUID.randomUUID()),
                AverageRating.of(3.5),
                Document.Audit.Timestamps.of(Instant.now())
        );

        Document testDocument = Document.of(docId, content, metadata, audit);

        repository.save(testDocument);

        repository.deleteById(docId);

        Document deletedDocument = repository.getById(docId);

        assertNull(deletedDocument, "Document should be deleted");
    }
}