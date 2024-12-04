package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CloudUserRepository<User extends BaseUser> implements UserRepository<User> {
    private final CloudDatabaseConfig dbConfig;

    public CloudUserRepository(CloudDatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public @Nullable User getByUsername(@NonNull String username) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NonNull User entity) {
        String query = """
            INSERT INTO Users (id, username, password, created, lastLogin, lastLogout)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                username = EXCLUDED.username,
                password = EXCLUDED.password,
                lastLogin = EXCLUDED.lastLogin,
                lastLogout = EXCLUDED.lastLogout;
        """;

        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entity.getId().getUuid().toString());
            stmt.setString(2, entity.getCredentials().getUsername());
            stmt.setString(3, new String(entity.getCredentials().getPassword().getChars()));
            stmt.setTimestamp(4, Timestamp.from(entity.getAudit().getTimestamps().getCreated()));
            stmt.setTimestamp(5, entity.getAudit().getTimestamps().getLastLogin() != null
                    ? Timestamp.from(entity.getAudit().getTimestamps().getLastLogin())
                    : null);
            stmt.setTimestamp(6, entity.getAudit().getTimestamps().getLastLogout() != null
                    ? Timestamp.from(entity.getAudit().getTimestamps().getLastLogout())
                    : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(BaseUser.@NonNull Id entityId) {
        String query = "DELETE FROM Users WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable User getById(BaseUser.@NonNull Id entityId) {
        String query = "SELECT * FROM Users WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @NonNull List<User> show() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection connection = dbConfig.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        BaseUser.Id id = BaseUser.Id.of(UUID.fromString(rs.getString("id")));
        BaseUser.Credentials credentials = BaseUser.Credentials.of(
                rs.getString("username"),
                SecureString.of(rs.getString("password").toCharArray())
        );
        BaseUser.Audit.Timestamps timestamps = BaseUser.Audit.Timestamps.of(
                rs.getTimestamp("created").toInstant()
        );
        timestamps.setLastLogin(rs.getTimestamp("lastLogin") != null
                ? rs.getTimestamp("lastLogin").toInstant()
                : null);
        timestamps.setLastLogout(rs.getTimestamp("lastLogout") != null
                ? rs.getTimestamp("lastLogout").toInstant()
                : null);
        BaseUser.Audit audit = BaseUser.Audit.of(timestamps);
        return (User) new BaseUser(id, audit, credentials);
    }
}

