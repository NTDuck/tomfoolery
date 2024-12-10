package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.relations;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(staticName = "of")
public class CloudReviewRepository implements ReviewRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public void save(@NonNull Review review) {
        String query = """
            INSERT INTO Review (documentId, patronId, rating)
            VALUES (?, ?, ?)
            ON CONFLICT (documentId, patronId) DO UPDATE SET
                rating = EXCLUDED.rating;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, review.getId().getFirstEntityId().getISBN_10());
            stmt.setString(2, review.getId().getSecondEntityId().getUuid().toString());
            stmt.setDouble(3, review.getRating());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Review.@NonNull Id reviewId) {
        String query = "DELETE FROM Review WHERE documentId = ? AND patronId = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reviewId.getFirstEntityId().getISBN_10());
            stmt.setString(2, reviewId.getSecondEntityId().getUuid().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable Review getById(Review.@NonNull Id reviewId) {
        String query = "SELECT * FROM Review WHERE documentId = ? AND patronId = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reviewId.getFirstEntityId().getISBN_10());
            stmt.setString(2, reviewId.getSecondEntityId().getUuid().toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToReview(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @NonNull
    public List<Review> show() {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM Review";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Document.Id documentId = Document.Id.of(rs.getString("documentId"));
        Patron.Id patronId = Patron.Id.of(UUID.fromString(rs.getString("patronId")));
        double rating = rs.getDouble("rating");

        return Review.of(Review.Id.of(documentId, patronId), rating);
    }
}
