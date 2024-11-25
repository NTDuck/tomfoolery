package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config;

import io.github.jan.supabase.SupabaseClientBuilder;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.SupabaseClientBuilder;

import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@RequiredArgsConstructor(staticName = "of")
public class CloudDatabaseConfig {
    private final String supabaseUrl;
    private final String supabaseKey;
    private final String supabaseConnection;
    public CloudDatabaseConfig(String configFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        }

        this.supabaseUrl = properties.getProperty("SUPABASE_URL");
        this.supabaseKey = properties.getProperty("SUPABASE_API_KEY");
        this.supabaseConnection = properties.getProperty("SUPABASE_CONNECTION");
        if (supabaseUrl == null || supabaseKey == null | supabaseConnection == null) {
            throw new IllegalStateException("Missing Supabase configurations in " + configFilePath);
        }
    }

    public Connection connect() throws SQLException {
        String jdbcUrl = supabaseConnection;

        return DriverManager.getConnection(jdbcUrl);
    }
}
