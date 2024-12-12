package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudDocumentContentRepository implements DocumentContentRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public void save(@NonNull DocumentContent documentContent) {
        String query = """
            INSERT INTO DocumentContent (id, entityId, bytes)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                entityId = EXCLUDED.entityId,
                bytes = EXCLUDED.bytes;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, documentContent.getId().toString());
            stmt.setString(2, documentContent.getId().getEntityId().getISBN_10());
            stmt.setBytes(3, documentContent.getBytes());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DocumentContent.@NonNull Id id) {
        String query = "DELETE FROM DocumentContent WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DocumentContent getById(DocumentContent.@NonNull Id id) {
        String query = "SELECT * FROM DocumentContent WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDocumentContent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public @NonNull Set<DocumentContent.Id> showIds() {
        return Set.of();
    }

    @Override
    public @NonNull Set<DocumentContent> show() {
        List<DocumentContent> documentContents = new ArrayList<>();
        String query = "SELECT * FROM DocumentContent";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                documentContents.add(mapResultSetToDocumentContent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentContents.parallelStream().collect(Collectors.toUnmodifiableSet());
    }

    private DocumentContent mapResultSetToDocumentContent(ResultSet rs) throws SQLException {
        DocumentContent.Id id = DocumentContent.Id.of(
                Document.Id.of(rs.getString("entityId"))
        );
        byte[] bytes = rs.getBytes("bytes");

        return DocumentContent.of(id, bytes);
    }
}


