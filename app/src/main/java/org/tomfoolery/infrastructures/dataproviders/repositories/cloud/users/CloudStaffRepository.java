//package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;
//
//import org.checkerframework.checker.nullness.qual.NonNull;
//import org.checkerframework.checker.nullness.qual.Nullable;
//import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
//import org.tomfoolery.core.domain.users.abc.BaseUser;
//import org.tomfoolery.core.domain.users.abc.ModifiableUser;
//import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
//import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.UUID;
//
//public class CloudStaffRepository<Staff extends ModifiableUser> implements UserRepository<Staff> {
//    private final CloudDatabaseConfig dbConfig;
//
//    public CloudStaffRepository(CloudDatabaseConfig dbConfig) {
//        this.dbConfig = dbConfig;
//    }
//
//    @Override
//    public @Nullable Staff getByUsername(@NonNull String username) {
//        String query = "SELECT * FROM Staff WHERE username = ?";
//        try (Connection connection = dbConfig.connect();
//             PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, username);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return mapResultSetToStaff(rs);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public void save(@NonNull Staff entity) {
//        String query = """
//            INSERT INTO Staff (id, username, password, created, lastLogin, lastLogout, createdByAdminId, lastModifiedByAdminId)
//            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
//            ON CONFLICT (id) DO UPDATE SET
//                username = EXCLUDED.username,
//                password = EXCLUDED.password,
//                lastLogin = EXCLUDED.lastLogin,
//                lastLogout = EXCLUDED.lastLogout,
//                lastModifiedByAdminId = EXCLUDED.lastModifiedByAdminId;
//        """;
//
//        try (Connection connection = dbConfig.connect();
//             PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, entity.getId().getUuid().toString());
//            stmt.setString(2, entity.getCredentials().getUsername());
//            stmt.setString(3, new String(entity.getCredentials().getPassword().getChars()));
//            stmt.setTimestamp(4, Timestamp.from(entity.getAudit().getTimestamps().getCreated()));
//            stmt.setTimestamp(5, entity.getAudit().getTimestamps().getLastLogin() != null
//                    ? Timestamp.from(entity.getAudit().getTimestamps().getLastLogin())
//                    : null);
//            stmt.setTimestamp(6, entity.getAudit().getTimestamps().getLastLogout() != null
//                    ? Timestamp.from(entity.getAudit().getTimestamps().getLastLogout())
//                    : null);
//            stmt.setString(7, entity.getAudit().getCreatedByAdminId().getUuid().toString());
//            stmt.setString(8, entity.getAudit().getLastModifiedByAdminId() != null
//                    ? entity.getAudit().getLastModifiedByAdminId().getUuid().toString()
//                    : null);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void delete(BaseUser.@NonNull Id entityId) {
//        String query = "DELETE FROM Staff WHERE id = ?";
//        try (Connection connection = dbConfig.connect();
//             PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, entityId.getUuid().toString());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public @Nullable Staff getById(BaseUser.@NonNull Id entityId) {
//        String query = "SELECT * FROM Staff WHERE id = ?";
//        try (Connection connection = dbConfig.connect();
//             PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, entityId.getUuid().toString());
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return mapResultSetToStaff(rs);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public @NonNull List<Staff> show() {
//        List<Staff> staffList = new ArrayList<>();
//        String query = "SELECT * FROM Staff";
//        try (Connection connection = dbConfig.connect();
//             Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                staffList.add(mapResultSetToStaff(rs));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return staffList;
//    }
//
//    @SuppressWarnings("unchecked")
//    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
//        BaseUser.Id id = BaseUser.Id.of(UUID.fromString(rs.getString("id")));
//        BaseUser.Credentials credentials = BaseUser.Credentials.of(
//                rs.getString("username"),
//                SecureString.of(rs.getString("password").toCharArray())
//        );
//        BaseUser.Audit.Timestamps timestamps = BaseUser.Audit.Timestamps.of(
//                rs.getTimestamp("created").toInstant()
//        );
//        timestamps.setLastLogin(rs.getTimestamp("lastLogin") != null
//                ? rs.getTimestamp("lastLogin").toInstant()
//                : null);
//        timestamps.setLastLogout(rs.getTimestamp("lastLogout") != null
//                ? rs.getTimestamp("lastLogout").toInstant()
//                : null);
//        Staff.Audit audit = Staff.Audit.of(
//                timestamps,
//                BaseUser.Id.of(UUID.fromString(rs.getString("createdByAdminId")))
//        );
//        if (rs.getString("lastModifiedByAdminId") != null) {
//            audit.setLastModifiedByAdminId(
//                    BaseUser.Id.of(UUID.fromString(rs.getString("lastModifiedByAdminId")))
//            );
//        }
//        return (Staff) new Staff(id, audit, credentials);
//    }
//}
