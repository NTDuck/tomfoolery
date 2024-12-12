package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.sql.*;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudDocumentRepository implements DocumentRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public void save(@NonNull Document document) {
        String query = """
        INSERT INTO Document (
            id, title, description, authors, genres, publishedYear, publisher, coverImage,
            averageRating, numberOfRatings, createdByStaffId, lastModifiedByStaffId, created, lastModified
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            title = EXCLUDED.title,
            description = EXCLUDED.description,
            authors = EXCLUDED.authors,
            genres = EXCLUDED.genres,
            publishedYear = EXCLUDED.publishedYear,
            publisher = EXCLUDED.publisher,
            coverImage = EXCLUDED.coverImage,
            averageRating = EXCLUDED.averageRating,
            numberOfRatings = EXCLUDED.numberOfRatings,
            lastModifiedByStaffId = EXCLUDED.lastModifiedByStaffId,
            lastModified = EXCLUDED.lastModified;
    """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, document.getId().getISBN_10());
            stmt.setString(2, document.getMetadata().getTitle());
            stmt.setString(3, document.getMetadata().getDescription());
            stmt.setArray(4, connection.createArrayOf("TEXT", document.getMetadata().getAuthors().toArray()));
            stmt.setArray(5, connection.createArrayOf("TEXT", document.getMetadata().getGenres().toArray()));
            stmt.setInt(6, document.getMetadata().getPublishedYear().getValue());
            stmt.setString(7, document.getMetadata().getPublisher());
            stmt.setBytes(8, document.getCoverImage() != null ? document.getCoverImage().getBytes() : null);
            stmt.setObject(9, document.getRating() != null ? document.getRating().getAverageRating() : null, Types.DOUBLE);
            stmt.setObject(10, document.getRating() != null ? document.getRating().getNumberOfRatings() : null, Types.INTEGER);
            stmt.setString(11, String.valueOf(document.getAudit().getCreatedByStaffId().getUuid()));
            stmt.setString(12, document.getAudit().getLastModifiedByStaffId() != null
                    ? String.valueOf(document.getAudit().getLastModifiedByStaffId().getUuid()) : null);
            stmt.setTimestamp(13, Timestamp.from(document.getAudit().getTimestamps().getCreated()));
            stmt.setTimestamp(14, document.getAudit().getTimestamps().getLastModified() != null
                    ? Timestamp.from(document.getAudit().getTimestamps().getLastModified()) : null);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(Document.@NonNull Id id) {
        String query = "DELETE FROM Document WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.getISBN_10());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Document getById(Document.@NonNull Id id) {
        String query = "SELECT * FROM Document WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
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
    public @NonNull Set<Document.Id> showIds() {
        return Set.of();
    }

    @Override
    @NonNull
    public Set<Document> show() {
        List<Document> documents = new ArrayList<>();
        String query = "SELECT * FROM Document";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documents.parallelStream().collect(Collectors.toUnmodifiableSet());
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

        Document.Rating rating = rs.getObject("averageRating") != null && rs.getObject("numberOfRatings") != null
                ? Document.Rating.of(rs.getDouble("averageRating"), rs.getInt("numberOfRatings"))
                : null;

        Document.Audit audit = Document.Audit.of(
                Document.Audit.Timestamps.of(rs.getTimestamp("created").toInstant()),
                Staff.Id.of(UUID.fromString(rs.getString("createdByStaffId")))
        );

        return Document.of(id, audit, metadata, rating, coverImage);
    }
}