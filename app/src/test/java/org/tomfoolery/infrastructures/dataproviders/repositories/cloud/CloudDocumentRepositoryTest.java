package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.abc.UnitTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

@Test(groups = "cloud")
public class CloudDocumentRepositoryTest extends UnitTest<CloudDocumentRepository> {
    private static final @NonNull String SAMPLE_ISBN = "123456789X";
    private static final @NonNull String SAMPLE_TITLE = "Sample Book";
    private static final @NonNull String SAMPLE_DESCRIPTION = "A sample description for the book.";
    private static final @NonNull List<String> SAMPLE_AUTHORS = List.of("Author1", "Author2");
    private static final @NonNull List<String> SAMPLE_GENRES = List.of("Fiction", "Adventure");
    private static final int SAMPLE_YEAR = 2023;
    private static final @NonNull String SAMPLE_PUBLISHER = "Sample Publisher";
    private static final byte[] SAMPLE_COVER_IMAGE = "Sample Cover Image".getBytes();
    private static final double SAMPLE_AVERAGE_RATING = 4.5;
    private static final int SAMPLE_NUMBER_OF_RATINGS = 10;
    private static final Staff.Id SAMPLE_CREATED_BY_STAFF_ID = Staff.Id.of(UUID.randomUUID());
    private static final Staff.Id SAMPLE_LAST_MODIFIED_BY_STAFF_ID = Staff.Id.of(UUID.randomUUID());

    private @NonNull Document sampleDocument;

    @Override
    protected @NonNull CloudDocumentRepository instantiate() {
        return CloudDocumentRepository.of();
    }

    @BeforeClass
    public void setUp() {
        super.setUp();

        val coverImage = Document.CoverImage.of(SAMPLE_COVER_IMAGE);
        val rating = Document.Rating.of(SAMPLE_AVERAGE_RATING, SAMPLE_NUMBER_OF_RATINGS);

        val audit = Document.Audit.of(
                Document.Audit.Timestamps.of(
                        Instant.now()
                ),
                SAMPLE_CREATED_BY_STAFF_ID
        );

        val metadata = Document.Metadata.of(
                SAMPLE_TITLE,
                SAMPLE_DESCRIPTION,
                SAMPLE_AUTHORS,
                SAMPLE_GENRES,
                Year.of(SAMPLE_YEAR),
                SAMPLE_PUBLISHER
        );

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

        // Verify all document properties
        assertEquals(sampleDocument.getMetadata(), retrievedDocument.getMetadata(), "Metadata should match.");
        assertEquals(sampleDocument.getRating(), retrievedDocument.getRating(), "Rating should match.");

        // Compare cover image bytes
        assertNotNull(retrievedDocument.getCoverImage(), "Cover image should not be null");
        assertEquals(sampleDocument.getCoverImage().getBytes(), retrievedDocument.getCoverImage().getBytes(), "Cover image should match.");

        // Verify audit details
//        assertEquals(sampleDocument.getAudit().getCreatedByStaffId(), retrievedDocument.getAudit().getCreatedByStaffId(), "Created by staff ID should match.");
        assertEquals(sampleDocument.getAudit().getLastModifiedByStaffId(), retrievedDocument.getAudit().getLastModifiedByStaffId(), "Last modified by staff ID should match.");
    }

//    @Test(dependsOnMethods = { "WhenRetrievingDocument_ExpectMatchingData" })
//    public void WhenDeletingDocument_ExpectDocumentToBeAbsent() {
//        this.unit.delete(sampleDocument.getId());
//
//        val retrievedDocument = this.unit.getById(sampleDocument.getId());
//        assertNull(retrievedDocument, "Document should be null after deletion.");
//    }

    @Test
    public void WhenListingDocuments_ExpectNonEmptyList() {
        this.unit.save(sampleDocument);

        val documents = this.unit.show();
        assertTrue(documents.size() > 0, "Document list should not be empty.");
        assertTrue(documents.stream().anyMatch(doc -> doc.getId().equals(sampleDocument.getId())),
                "Saved document should be in the list.");
    }
}
