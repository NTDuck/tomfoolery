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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudReviewRepository implements ReviewRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public void save(@NonNull Review review) {
        String query = """
            INSERT INTO Review (id, documentId, patronId, rating)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                documentId = EXCLUDED.documentId,
                patronId = EXCLUDED.patronId,
                rating = EXCLUDED.rating;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, review.getId().toString());
            stmt.setString(2, review.getId().getFirstEntityId().getISBN_10());
            stmt.setString(3, review.getId().getSecondEntityId().getUuid().toString());
            stmt.setDouble(4, review.getRating());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Review.@NonNull Id reviewId) {
        String query = "DELETE FROM Review WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, reviewId.toString()); // Use the review's unique id for deletion

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable Review getById(Review.@NonNull Id reviewId) {
        String query = "SELECT * FROM Review WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, reviewId.toString()); // Use the review's unique id to query

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
    public @NonNull Set<Review.Id> showIds() {
        return Set.of();
    }

    @Override
    @NonNull
    public Set<Review> show() {
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
        return reviews.parallelStream().collect(Collectors.toUnmodifiableSet());
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        Document.Id documentId = Document.Id.of(rs.getString("documentId"));
        Patron.Id patronId = Patron.Id.of(UUID.fromString(rs.getString("patronId")));
        double rating = rs.getDouble("rating");

        return Review.of(Review.Id.of(documentId, patronId), rating);
    }
}
