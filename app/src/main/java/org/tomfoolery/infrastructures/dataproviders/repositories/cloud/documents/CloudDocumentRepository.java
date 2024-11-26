package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemorySynchronizableRepository;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CloudDocumentRepository implements DocumentRepository {
    private final CloudDatabaseConfig cloudDatabaseConfig;

    public CloudDocumentRepository(CloudDatabaseConfig cloudDatabaseConfig) {
        this.cloudDatabaseConfig = cloudDatabaseConfig;
    }

    @Override
    public void save(@NonNull Document entity) {
        String query = """
            INSERT INTO documents (id, title, description, authors, genres, lastModified)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE 
            SET title = EXCLUDED.title,
                description = EXCLUDED.description,
                authors = EXCLUDED.authors,
                lastModified = EXCLUDED.lastModified;
        """;

        try (Connection connection = cloudDatabaseConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, entity.getId().getISBN());
            stmt.setString(2, entity.getMetadata().getTitle());
            stmt.setString(3, entity.getMetadata().getDescription());
            stmt.setString(4, String.join(",", entity.getMetadata().getAuthors())); // Convert list to CSV
            stmt.setTimestamp(5, Timestamp.from(Instant.now())); // Update lastModified to now

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save document: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Document.@NonNull Id entityId) {
        String query = "DELETE FROM documents WHERE id = ?";

        try (Connection connection = cloudDatabaseConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, entityId.getISBN());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete document: " + e.getMessage(), e);
        }
    }

    @Override
    public @Nullable Document getById(Document.@NonNull Id entityId) {
        String query = "SELECT * FROM documents WHERE id = ?";

        try (Connection connection = cloudDatabaseConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, entityId.getISBN());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDocument(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve document: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public @NonNull List<Document> show() {
        String query = "SELECT * FROM documents";

        try (Connection connection = cloudDatabaseConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<Document> documents = new ArrayList<>();
            while (rs.next()) {
                documents.add(mapRowToDocument(rs));
            }
            return documents;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve documents: " + e.getMessage(), e);
        }
    }

    @Override
    public @NonNull Set<Document> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        String query = "SELECT * FROM documents WHERE lastModified >= ?";

        try (Connection connection = cloudDatabaseConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.from(fromTimestamp));

            try (ResultSet rs = stmt.executeQuery()) {
                Set<Document> documents = new HashSet<>();
                while (rs.next()) {
                    documents.add(mapRowToDocument(rs));
                }
                return documents;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve saved entities: " + e.getMessage(), e);
        }
    }

    @Override
    public @NonNull Set<Document> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        // Assuming a soft-delete mechanism, adjust query if needed
        return Set.of();
    }

    private Document mapRowToDocument(ResultSet rs) throws SQLException {
        Document.Id id = Document.Id.of(rs.getString("id"));
        Document.Metadata metadata = Document.Metadata.of(
                rs.getString("title"),
                rs.getString("description"),
                Arrays.asList(rs.getString("authors").split(",")), // Convert CSV to List
                Collections.emptyList(), // Genres (not in schema)
                null, // PublishedYear (not in schema)
                null, // Publisher (not in schema)
                null // CoverImage (not in schema)
        );
        Document.Audit audit = Document.Audit.of(
                Instant.EPOCH, // Placeholder for `created`, now removed
                rs.getTimestamp("lastModified").toInstant()
        );

        return new Document(id, Document.Content.of(new byte[0]), metadata);
    }
}

