package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;
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
    public void save(Document document) {
        String query = """
            INSERT INTO Document (
                id, title, description, authors, genres, publishedYear, publisher, coverImage, content,
                createdByStaffId, lastModifiedByStaffId, borrowingPatronIds, rating, created, lastModified
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                title = EXCLUDED.title,
                description = EXCLUDED.description,
                authors = EXCLUDED.authors,
                genres = EXCLUDED.genres,
                publishedYear = EXCLUDED.publishedYear,
                publisher = EXCLUDED.publisher,
                coverImage = EXCLUDED.coverImage,
                content = EXCLUDED.content,
                lastModifiedByStaffId = EXCLUDED.lastModifiedByStaffId,
                borrowingPatronIds = EXCLUDED.borrowingPatronIds,
                rating = EXCLUDED.rating,
                lastModified = EXCLUDED.lastModified;
        """;

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, document.getId().getISBN());
            stmt.setString(2, document.getMetadata().getTitle());
            stmt.setString(3, document.getMetadata().getDescription());
            stmt.setArray(4, connection.createArrayOf("TEXT", document.getMetadata().getAuthors().toArray()));
            stmt.setArray(5, connection.createArrayOf("TEXT", document.getMetadata().getGenres().toArray()));
            stmt.setInt(6, document.getMetadata().getPublishedYear().getValue());
            stmt.setString(7, document.getMetadata().getPublisher());
            stmt.setBytes(8, document.getMetadata().getCoverImage().getBuffer());
            stmt.setBytes(9, document.getContent().getBytes());
            stmt.setString(10, String.valueOf(document.getAudit().getCreatedByStaffId().getValue()));
            stmt.setString(11, document.getAudit().getLastModifiedByStaffId() != null
                    ? String.valueOf(document.getAudit().getLastModifiedByStaffId().getValue()) : null);
            stmt.setArray(12, connection.createArrayOf("TEXT", document.getAudit().getBorrowingPatronIds().stream()
                    .map(Patron.Id::getValue).toArray()));
            stmt.setDouble(13, document.getAudit().getRating().getValue());
            stmt.setTimestamp(14, Timestamp.from(document.getAudit().getTimestamps().getCreated()));
            stmt.setTimestamp(15, document.getAudit().getTimestamps().getLastModified() != null
                    ? Timestamp.from(document.getAudit().getTimestamps().getLastModified()) : null);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Document.@NonNull Id entityId) {

    }

    @Override
    public Document getById(Document.Id id) {
        String query = "SELECT * FROM Document WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.getISBN());
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

    public void deleteById(Document.Id id) {
        String query = "DELETE FROM Document WHERE id = ?";

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.getISBN());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @NonNull
    public Set<Document> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        Set<Document> documents = new HashSet<>();
        String query = "SELECT * FROM Document WHERE created >= ?";

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.from(fromTimestamp));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    @Override
    @NonNull
    public Set<Document> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        return new HashSet<>();
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document.Id id = Document.Id.of(rs.getString("id"));
        Document.Metadata metadata = Document.Metadata.of(
                rs.getString("title"),
                rs.getString("description"),
                Arrays.asList((String[]) rs.getArray("authors").getArray()),
                Arrays.asList((String[]) rs.getArray("genres").getArray()),
                Year.of(rs.getInt("publishedYear")),
                rs.getString("publisher"),
                Document.Metadata.CoverImage.of(rs.getBytes("coverImage"))
        );


        Document.Audit audit = Document.Audit.of(
                Staff.Id.of(UUID.fromString(rs.getString("createdByStaffId"))),
                AverageRating.of(rs.getDouble("rating")),
                Document.Audit.Timestamps.of(
                        rs.getTimestamp("created").toInstant()
                )
        );

        Document.Content content = Document.Content.of(rs.getBytes("content"));

        return Document.of(id, content, metadata, audit);
    }
}
