package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import java.sql.*;
import java.time.Instant;
import java.time.Year;
import java.util.*;

public class CloudDocumentRepository implements DocumentRepository {
    private final CloudDatabaseConfig dbConfig;

    public CloudDocumentRepository(CloudDatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public void save(@NotNull Document document) {
        String query = """
            INSERT INTO Document (
                id, title, description, authors, genres, publishedYear, publisher, coverImage,
                createdByStaffId, lastModifiedByStaffId, created, lastModified
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                title = EXCLUDED.title,
                description = EXCLUDED.description,
                authors = EXCLUDED.authors,
                genres = EXCLUDED.genres,
                publishedYear = EXCLUDED.publishedYear,
                publisher = EXCLUDED.publisher,
                coverImage = EXCLUDED.coverImage,
                lastModifiedByStaffId = EXCLUDED.lastModifiedByStaffId,
                lastModified = EXCLUDED.lastModified;
        """;

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, document.getId().getISBN_10());
            stmt.setString(2, document.getMetadata().getTitle());
            stmt.setString(3, document.getMetadata().getDescription());
            stmt.setArray(4, connection.createArrayOf("TEXT", document.getMetadata().getAuthors().toArray()));
            stmt.setArray(5, connection.createArrayOf("TEXT", document.getMetadata().getGenres().toArray()));
            stmt.setInt(6, document.getMetadata().getPublishedYear().getValue());
            stmt.setString(7, document.getMetadata().getPublisher());
            stmt.setBytes(8, document.getCoverImage().getBytes());
            stmt.setString(9, String.valueOf(document.getAudit().getCreatedByStaffId().getUuid()));
            stmt.setString(10, document.getAudit().getLastModifiedByStaffId() != null
                    ? String.valueOf(document.getAudit().getLastModifiedByStaffId().getUuid()) : null);
            stmt.setTimestamp(11, Timestamp.from(document.getAudit().getTimestamps().getCreated()));
            stmt.setTimestamp(12, document.getAudit().getTimestamps().getLastModified() != null
                    ? Timestamp.from(document.getAudit().getTimestamps().getLastModified()) : null);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Document.@NonNull Id id) {
        String query = "DELETE FROM Document WHERE id = ?";

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.getISBN_10());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Document getById(Document.@NotNull Id id) {
        String query = "SELECT * FROM Document WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.getISBN_10());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDocument(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @NonNull
    public List<Document> show() {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Document";

        try (Connection connection = dbConfig.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document.Id id = Document.Id.of(rs.getString("id"));
        Document.Metadata metadata = Document.Metadata.of(
                rs.getString("title"),
                rs.getString("description"),
                Arrays.asList((String[]) rs.getArray("authors").getArray()),
                Arrays.asList((String[]) rs.getArray("genres").getArray()),
                Year.of(rs.getInt("publishedYear")),
                rs.getString("publisher")
        );

        Document.CoverImage coverImage = rs.getBytes("coverImage") != null
                ? Document.CoverImage.of(rs.getBytes("coverImage"))
                : null;

        Document.Audit audit = Document.Audit.of(
                Document.Audit.Timestamps.of(rs.getTimestamp("created").toInstant()),
                Staff.Id.of(UUID.fromString(rs.getString("createdByStaffId")))
        );
        return Document.of(id, audit, metadata);
    }
}