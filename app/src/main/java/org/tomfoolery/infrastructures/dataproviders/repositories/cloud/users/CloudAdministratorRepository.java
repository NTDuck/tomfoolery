package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;


import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(staticName = "of")
public class CloudAdministratorRepository implements UserRepository<Administrator> {
    private final CloudDatabaseConfig dbConfig = CloudDatabaseConfig.of();

    @Override
    public @Nullable Administrator getByUsername(@NonNull String username) {
        String query = "SELECT * FROM Administrators WHERE username = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAdministrator(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NonNull Administrator entity) {
        String query = """
            INSERT INTO Administrators (id, username, password, created, lastLogin, lastLogout)
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
        String query = "DELETE FROM Administrators WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable Administrator getById(BaseUser.@NonNull Id entityId) {
        String query = "SELECT * FROM Administrators WHERE id = ?";
        try (Connection connection = dbConfig.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAdministrator(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @NonNull List<Administrator> show() {
        List<Administrator> administrators = new ArrayList<>();
        String query = "SELECT * FROM Administrators";
        try (Connection connection = dbConfig.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                administrators.add(mapResultSetToAdministrator(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return administrators;
    }

    @SuppressWarnings("unchecked")
    private Administrator mapResultSetToAdministrator(ResultSet rs) throws SQLException {
        Administrator.Id id = Administrator.Id.of(UUID.fromString(rs.getString("id")));
        Administrator.Credentials credentials = Administrator.Credentials.of(
                rs.getString("username"),
                SecureString.of(rs.getString("password").toCharArray())
        );
        Administrator.Audit.Timestamps timestamps = Administrator.Audit.Timestamps.of(
                rs.getTimestamp("created").toInstant()
        );
        timestamps.setLastLogin(rs.getTimestamp("lastLogin") != null
                ? rs.getTimestamp("lastLogin").toInstant()
                : null);
        timestamps.setLastLogout(rs.getTimestamp("lastLogout") != null
                ? rs.getTimestamp("lastLogout").toInstant()
                : null);
        Administrator.Audit audit = Administrator.Audit.of(timestamps);
        return Administrator.of(id, audit, credentials);
    }
}