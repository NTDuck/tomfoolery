package org.tomfoolery.infrastructures.dataproviders.providers.configurations.cloud;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RequiredArgsConstructor(staticName = "of")
public class CloudDatabaseConfigurationsProvider {
    private final @NonNull DotenvProvider dotenvProvider;

    public Connection connect() throws SQLException {
        val supabaseApiKey = dotenvProvider.get("SUPABASE_DATABASE_URL");
        return DriverManager.getConnection(String.valueOf(supabaseApiKey));
    }
}
