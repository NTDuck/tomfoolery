package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.RequiredArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(staticName = "of")
public class CloudStaffRepository implements StaffRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public @Nullable Staff getByUsername(@NonNull String username) {
        String query = "SELECT * FROM Staff WHERE username = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NonNull Staff entity) {
        String query = """
            INSERT INTO Staff (id, username, password, created, lastLogin, lastLogout, lastModified, 
                               createdByAdministratorId, lastModifiedByAdministratorId)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                username = EXCLUDED.username,
                password = EXCLUDED.password,
                lastLogin = EXCLUDED.lastLogin,
                lastLogout = EXCLUDED.lastLogout,
                lastModified = EXCLUDED.lastModified,
                lastModifiedByAdministratorId = EXCLUDED.lastModifiedByAdministratorId;
        """;

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
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
            stmt.setTimestamp(7, entity.getAudit().getTimestamps().getLastModified() != null
                    ? Timestamp.from(entity.getAudit().getTimestamps().getLastModified())
                    : null);
            stmt.setString(8, entity.getAudit().getCreatedByAdministratorId().getUuid().toString());
            stmt.setString(9, entity.getAudit().getLastModifiedByAdministratorId() != null
                    ? entity.getAudit().getLastModifiedByAdministratorId().getUuid().toString()
                    : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(BaseUser.@NonNull Id entityId) {
        String query = "DELETE FROM Staff WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable Staff getById(BaseUser.@NonNull Id entityId) {
        String query = "SELECT * FROM Staff WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @NonNull List<Staff> show() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM Staff";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff.Id id = Staff.Id.of(UUID.fromString(rs.getString("id")));
        Staff.Credentials credentials = Staff.Credentials.of(
                rs.getString("username"),
                SecureString.of(rs.getString("password").toCharArray())
        );

        Staff.Audit.Timestamps timestamps = Staff.Audit.Timestamps.of(
                rs.getTimestamp("created").toInstant()
        );
        timestamps.setLastLogin(rs.getTimestamp("lastLogin") != null
                ? rs.getTimestamp("lastLogin").toInstant()
                : null);
        timestamps.setLastLogout(rs.getTimestamp("lastLogout") != null
                ? rs.getTimestamp("lastLogout").toInstant()
                : null);
        timestamps.setLastModified(rs.getTimestamp("lastModified") != null
                ? rs.getTimestamp("lastModified").toInstant()
                : null);

        Staff.Audit audit = Staff.Audit.of(
                timestamps,
                Administrator.Id.of(UUID.fromString(rs.getString("createdByAdministratorId")))
        );
        audit.setLastModifiedByAdministratorId(rs.getString("lastModifiedByAdministratorId") != null
                ? Administrator.Id.of(UUID.fromString(rs.getString("lastModifiedByAdministratorId")))
                : null);

        return Staff.of(id, audit, credentials);
    }
}
