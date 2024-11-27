package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.*;

public class CloudDocumentRepositoryTest {
    // TODO: Rewrite test - no mutable static attributes allowed
//    private static CloudDatabaseConfig cloudDatabaseConfig;
//    private static CloudDocumentRepository documentRepository;
//
//    @BeforeClass
//    public static void setup() throws IOException {
//        Path configFilePath = Path.of("src/main/resources/config.properties");
//
//        if (!Files.exists(configFilePath)) {
//            throw new IOException("Configuration file not found at " + configFilePath.toAbsolutePath());
//        }
//
//        cloudDatabaseConfig = new CloudDatabaseConfig(configFilePath.toString());
//
//        try {
//            documentRepository = new CloudDocumentRepository((CloudDatabaseConfig) cloudDatabaseConfig.connect());
//        } catch (SQLException e) {
//            fail("Failed to initialize CloudDocumentRepository: " + e.getMessage());
//        }
//    }
//
//    // TODO: Glorious evolution
////    @Test
////    public void testSaveAndRetrieveDocument() {
////        Document document = Document.of(
////                Document.Id.of("12345"),
////                Document.Content.of(new byte[0]),
////                Document.Metadata.of(
////                        "Test Title",
////                        "Test Description",
////                        List.of("Author1", "Author2"),
////                        List.of("Fiction"),
////                        null,
////                        null,
////                        null
////                ),
////                Document.Audit.of(Instant.now(), Instant.now(), "test-user")
////        );
////
////        // Save the document
////        documentRepository.save(document);
////
////        // Retrieve the document
////        Document retrievedDocument = documentRepository.getById(Document.Id.of("12345"));
////
////        assertNotNull("Retrieved document should not be null", retrievedDocument);
////        assertEquals("Document ID should match", document.getId(), retrievedDocument.getId());
////        assertEquals("Title should match", document.getMetadata().getTitle(), retrievedDocument.getMetadata().getTitle());
////        assertEquals("Description should match", document.getMetadata().getDescription(), retrievedDocument.getMetadata().getDescription());
////        assertEquals("Authors should match", document.getMetadata().getAuthors(), retrievedDocument.getMetadata().getAuthors());
////        System.out.println("Save and retrieve test passed!");
////    }
//
//    @Test
//    public void testDeleteDocument() {
//        // Create a test document
//        Document.Id documentId = Document.Id.of("12345");
//        documentRepository.delete(documentId);
//
//        // Verify the document is deleted
//        Document retrievedDocument = documentRepository.getById(documentId);
//        assertNull("Deleted document should be null", retrievedDocument);
//        System.out.println("Delete test passed!");
//    }
//
//    @Test
//    public void testShowDocuments() {
//        // Retrieve all documents
//        List<Document> documents = documentRepository.show();
//
//        assertNotNull("Document list should not be null", documents);
//        System.out.println("Show documents test passed! Retrieved " + documents.size() + " documents.");
//    }
//
//    @Test
//    public void testGetSavedEntitiesSince() {
//        Instant timestamp = Instant.now().minusSeconds(3600); // 1 hour ago
//        Set<Document> recentDocuments = documentRepository.getSavedEntitiesSince(timestamp);
//
//        assertNotNull("Recent documents set should not be null", recentDocuments);
//        System.out.println("Get saved entities since test passed! Retrieved " + recentDocuments.size() + " documents.");
//    }
//
//    @AfterClass
//    public static void cleanup() {
//        try {
//            cloudDatabaseConfig.connect().close();
//        } catch (SQLException e) {
//            System.err.println("Failed to close database connection: " + e.getMessage());
//        }
//    }
}
