package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud.CloudDatabaseConfigurationsProvider;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudPatronRepository implements PatronRepository {
    private final @NonNull CloudDatabaseConfigurationsProvider cloudDatabaseConfigurationsProvider;

    @Override
    public @Nullable Patron getByUsername(@NonNull String username) {
        String query = "SELECT * FROM Patrons WHERE username = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatron(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NonNull Patron entity) {
        String query = """
            INSERT INTO Patrons (
                id, username, password, created, lastLogin, lastLogout,
                firstName, lastName, dateOfBirth, phoneNumber, city, country, email
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                username = EXCLUDED.username,
                password = EXCLUDED.password,
                lastLogin = EXCLUDED.lastLogin,
                lastLogout = EXCLUDED.lastLogout,
                firstName = EXCLUDED.firstName,
                lastName = EXCLUDED.lastName,
                dateOfBirth = EXCLUDED.dateOfBirth,
                phoneNumber = EXCLUDED.phoneNumber,
                city = EXCLUDED.city,
                country = EXCLUDED.country,
                email = EXCLUDED.email;
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
            stmt.setString(7, entity.getMetadata().getName().getFirstName());
            stmt.setString(8, entity.getMetadata().getName().getLastName());
            stmt.setDate(9, Date.valueOf(entity.getMetadata().getDateOfBirth()));
            stmt.setString(10, entity.getMetadata().getPhoneNumber());
            stmt.setString(11, entity.getMetadata().getAddress().getCity());
            stmt.setString(12, entity.getMetadata().getAddress().getCountry());
            stmt.setString(13, entity.getMetadata().getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(BaseUser.@NonNull Id entityId) {
        String query = "DELETE FROM Patrons WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable Patron getById(BaseUser.@NonNull Id entityId) {
        String query = "SELECT * FROM Patrons WHERE id = ?";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entityId.getUuid().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatron(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @NonNull Set<BaseUser.Id> showIds() {
        Set<BaseUser.Id> ids = new HashSet<>();
        String query = "SELECT id FROM Patrons";

        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BaseUser.Id id = BaseUser.Id.of(UUID.fromString(rs.getString("id")));
                ids.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Set.copyOf(ids);
    }


    @Override
    public @NonNull Set<Patron> show() {
        List<Patron> patrons = new ArrayList<>();
        String query = "SELECT * FROM Patrons";
        try (Connection connection = cloudDatabaseConfigurationsProvider.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                patrons.add(mapResultSetToPatron(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patrons.parallelStream().collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    private Patron mapResultSetToPatron(ResultSet rs) throws SQLException {
        Patron.Id id = Patron.Id.of(UUID.fromString(rs.getString("id")));
        Patron.Credentials credentials = Patron.Credentials.of(
                rs.getString("username"),
                SecureString.of(rs.getString("password").toCharArray())
        );
        Patron.Audit.Timestamps timestamps = Patron.Audit.Timestamps.of(
                rs.getTimestamp("created").toInstant()
        );
        timestamps.setLastLogin(rs.getTimestamp("lastLogin") != null
                ? rs.getTimestamp("lastLogin").toInstant()
                : null);
        timestamps.setLastLogout(rs.getTimestamp("lastLogout") != null
                ? rs.getTimestamp("lastLogout").toInstant()
                : null);
        Patron.Audit audit = Patron.Audit.of(timestamps);

        Patron.Metadata metadata = Patron.Metadata.of(
                Patron.Metadata.Name.of(
                        rs.getString("firstName"),
                        rs.getString("lastName")
                ),
                rs.getDate("dateOfBirth").toLocalDate(),
                rs.getString("phoneNumber"),
                Patron.Metadata.Address.of(
                        rs.getString("city"),
                        rs.getString("country")
                ),
                rs.getString("email")
        );
        return Patron.of(id, audit, credentials, metadata);
    }
}

