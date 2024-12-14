package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudIndexedDocumentSearchGenerator implements DocumentSearchGenerator {

    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public @NonNull List<Document> searchByNormalizedTitle(@NonNull String title) {
        return searchDocuments("normalizedTitle", title);
    }

    @Override
    public @NonNull List<Document> searchByNormalizedAuthor(@NonNull String author) {
        return searchDocuments("normalizedAuthors", author);
    }

    @Override
    public @NonNull List<Document> searchByNormalizedGenre(@NonNull String genre) {
        return searchDocuments("normalizedGenres", genre);
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        val documentMetadata = savedDocument.getMetadata();

        val normalizedTitle = this.normalize(documentMetadata.getTitle());
        val normalizedAuthors = this.normalize(documentMetadata.getAuthors());
        val normalizedGenres = this.normalize(documentMetadata.getGenres());

        String query = """
            INSERT INTO DocumentIndex (id, normalizedTitle, normalizedAuthors, normalizedGenres) 
            VALUES (?, ?, ?, ?) 
            ON CONFLICT (id) DO UPDATE SET 
                normalizedTitle = EXCLUDED.normalizedTitle, 
                normalizedAuthors = EXCLUDED.normalizedAuthors, 
                normalizedGenres = EXCLUDED.normalizedGenres;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, savedDocument.getId().getISBN_10());
            stmt.setString(2, normalizedTitle);
            stmt.setArray(3, connection.createArrayOf("TEXT", normalizedAuthors.toArray()));
            stmt.setArray(4, connection.createArrayOf("TEXT", normalizedGenres.toArray()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        String query = "DELETE FROM DocumentIndex WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, deletedDocument.getId().getISBN_10());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private @NonNull List<Document> searchDocuments(@NonNull String field, @NonNull String searchTerm) {
        List<Document> documents = new ArrayList<>();
        String query = """
            SELECT d.* FROM Document d
            JOIN DocumentIndex di ON d.id = di.id
            WHERE di.""" + field + """ 
        ILIKE ?;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, "%" + normalize(searchTerm) + "%");
            ResultSet rs = stmt.executeQuery();

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

