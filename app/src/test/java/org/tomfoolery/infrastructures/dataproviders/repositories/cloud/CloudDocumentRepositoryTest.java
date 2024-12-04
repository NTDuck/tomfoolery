package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.abc.UnitTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class CloudDocumentRepositoryTest extends UnitTest<CloudDocumentRepository> {
    private static final @NonNull String SAMPLE_ISBN = "123456789X";
    private static final @NonNull String SAMPLE_TITLE = "Sample Book";
    private static final @NonNull String SAMPLE_DESCRIPTION = "A sample description for the book.";
    private static final @NonNull List<String> SAMPLE_AUTHORS = List.of("Author1", "Author2");
    private static final @NonNull List<String> SAMPLE_GENRES = List.of("Fiction", "Adventure");
    private static final int SAMPLE_YEAR = 2023;
    private static final @NonNull String SAMPLE_PUBLISHER = "Sample Publisher";
    private static final double SAMPLE_AVERAGE_RATING = 4.5;
    private static final int SAMPLE_NUMBER_OF_RATINGS = 100;
    private static final byte[] SAMPLE_COVER_IMAGE = "SampleImageData".getBytes();

    private @NonNull Document sampleDocument;

    private final @NonNull CloudDatabaseConfig cloudDatabaseConfig = CloudDatabaseConfig.of();

    @Override
    protected @NonNull CloudDocumentRepository instantiate() {
        try {
            cloudDatabaseConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CloudDocumentRepository.of(cloudDatabaseConfig);
    }

    @BeforeClass
    public void setUp() {
        super.setUp();

        val audit = Document.Audit.of(
                Document.Audit.Timestamps.of(Instant.now()),
                Staff.Id.of(UUID.randomUUID())
        );

        val metadata = Document.Metadata.of(
                SAMPLE_TITLE,
                SAMPLE_DESCRIPTION,
                SAMPLE_AUTHORS,
                SAMPLE_GENRES,
                Year.of(SAMPLE_YEAR),
                SAMPLE_PUBLISHER
        );

        val rating = Document.Rating.of(SAMPLE_AVERAGE_RATING, SAMPLE_NUMBER_OF_RATINGS);
        val coverImage = Document.CoverImage.of(SAMPLE_COVER_IMAGE);

        this.sampleDocument = Document.of(
                Document.Id.of(SAMPLE_ISBN),
                audit,
                metadata,
                rating,
                coverImage
        );
    }


    @Test
    public void WhenSavingDocument_ExpectDocumentToExist() {
        this.unit.save(sampleDocument);

        val retrievedDocument = this.unit.getById(sampleDocument.getId());
        assertNotNull(retrievedDocument, "Document should exist after saving.");
        assertEquals(sampleDocument.getId(), retrievedDocument.getId(), "Saved document ID should match.");
    }

    @Test(dependsOnMethods = { "WhenSavingDocument_ExpectDocumentToExist" })
    public void WhenRetrievingDocument_ExpectMatchingData() {
        val retrievedDocument = this.unit.getById(sampleDocument.getId());
        assertNotNull(retrievedDocument, "Retrieved document should not be null.");
        assertEquals(sampleDocument.getMetadata(), retrievedDocument.getMetadata(), "Metadata should match.");
    }

    @Test(dependsOnMethods = { "WhenRetrievingDocument_ExpectMatchingData" })
    public void WhenDeletingDocument_ExpectDocumentToBeAbsent() {
        this.unit.delete(sampleDocument.getId());

        val retrievedDocument = this.unit.getById(sampleDocument.getId());
        assertNull(retrievedDocument, "Document should be null after deletion.");
    }

    @Test
    public void WhenListingDocuments_ExpectNonEmptyList() {
        this.unit.save(sampleDocument);

        val documents = this.unit.show();
        assertTrue(documents.size() > 0, "Document list should not be empty.");
        assertTrue(documents.stream().anyMatch(doc -> doc.getId().equals(sampleDocument.getId())),
                "Saved document should be in the list.");
    }

//    @AfterClass
//    public void tearDown() {
//        try {
//            // Delete the sample document after all tests are finished
//            this.unit.delete(sampleDocument.getId());
//
//            // Verify deletion
//            val retrievedDocument = this.unit.getById(sampleDocument.getId());
//            assertNull(retrievedDocument, "Document should be null after deletion.");
//            System.out.println("Sample document successfully deleted from the database.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Failed to delete the sample document: " + e.getMessage());
//        }
//    }

}
