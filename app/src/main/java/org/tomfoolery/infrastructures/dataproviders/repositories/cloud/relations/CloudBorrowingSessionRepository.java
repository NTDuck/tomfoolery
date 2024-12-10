package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(staticName = "of")
public class CloudBorrowingSessionRepository implements BorrowingSessionRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public void save(@NonNull BorrowingSession borrowingSession) {
        String query = """
            INSERT INTO BorrowingSessions (
                id, documentId, patronId, borrowedTimestamp, dueTimestamp
            ) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                borrowedTimestamp = EXCLUDED.borrowedTimestamp,
                dueTimestamp = EXCLUDED.dueTimestamp;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, borrowingSession.getId().getFirstEntityId().toString());
            stmt.setString(2, borrowingSession.getId().getFirstEntityId().getISBN_10());
            stmt.setString(3, borrowingSession.getId().getSecondEntityId().getUuid().toString());
            stmt.setTimestamp(4, Timestamp.from(borrowingSession.getBorrowedTimestamp()));
            stmt.setTimestamp(5, Timestamp.from(borrowingSession.getDueTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(BorrowingSession.@NonNull Id borrowingSessionId) {
        String query = "DELETE FROM BorrowingSessions WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, borrowingSessionId.getFirstEntityId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BorrowingSession getById(BorrowingSession.@NonNull Id borrowingSessionId) {
        String query = "SELECT * FROM BorrowingSessions WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, borrowingSessionId.getFirstEntityId().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBorrowingSession(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @NonNull List<BorrowingSession> show() {
        List<BorrowingSession> borrowingSessions = new ArrayList<>();
        String query = "SELECT * FROM BorrowingSessions";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                borrowingSessions.add(mapResultSetToBorrowingSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowingSessions;
    }

    private BorrowingSession mapResultSetToBorrowingSession(ResultSet rs) throws SQLException {
        Document.Id documentId = Document.Id.of(rs.getString("documentId"));
        Patron.Id patronId = Patron.Id.of(UUID.fromString(rs.getString("patronId")));
        BorrowingSession.Id borrowingSessionId = BorrowingSession.Id.of(documentId, patronId);

        Instant borrowedTimestamp = rs.getTimestamp("borrowedTimestamp").toInstant();
        Instant dueTimestamp = rs.getTimestamp("dueTimestamp").toInstant();

        return BorrowingSession.of(borrowingSessionId, borrowedTimestamp, dueTimestamp);
    }
}

