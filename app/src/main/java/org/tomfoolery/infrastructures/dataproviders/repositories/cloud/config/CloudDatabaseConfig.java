package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(staticName = "of")
public class CloudDatabaseConfig {
    private final @NonNull String supabaseConnection = "raw-api-key";   // ?

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(supabaseConnection);
    }
}
