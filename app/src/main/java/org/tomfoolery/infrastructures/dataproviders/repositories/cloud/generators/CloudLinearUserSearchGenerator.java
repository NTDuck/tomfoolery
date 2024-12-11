package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import org.tomfoolery.core.dataproviders.generators.users.search.abc.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudLinearUserSearchGenerator<User extends BaseUser> implements UserSearchGenerator<User> {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;
    private final @NonNull UserRepository<User> userRepository;

    @Override
    public @NonNull List<User> searchByNormalizedUsername(@NonNull String normalizedUsername) {
        String query = """
            SELECT id FROM UserSearchCache 
            WHERE normalized_username LIKE ? || '%' 
            OR normalized_username LIKE '%' || ? || '%'
        """;

        List<UUID> matchingIds = new ArrayList<>();

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, normalizedUsername);
            stmt.setString(2, normalizedUsername);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                matchingIds.add(UUID.fromString(rs.getString("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchingIds.stream()
                .map(uuid -> (User) userRepository.getById(BaseUser.Id.of(uuid)))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void synchronizeSavedEntity(@NonNull User savedEntity) {
        String query = """
            INSERT INTO UserSearchCache (id, username, normalized_username)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                username = EXCLUDED.username,
                normalized_username = EXCLUDED.normalized_username
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, savedEntity.getId().getUuid().toString());
            stmt.setString(2, savedEntity.getCredentials().getUsername());
            stmt.setString(3, normalize(savedEntity.getCredentials().getUsername()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull User deletedEntity) {
        String query = "DELETE FROM UserSearchCache WHERE id = ?";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, deletedEntity.getId().getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}








