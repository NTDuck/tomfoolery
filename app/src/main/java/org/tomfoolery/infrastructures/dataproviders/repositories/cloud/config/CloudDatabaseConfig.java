package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config;

import io.github.jan.supabase.SupabaseClientBuilder;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.SupabaseClientBuilder;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@NoArgsConstructor(staticName = "of")
public class CloudDatabaseConfig {
    private @NonNull String supabaseConnection;

    public void init() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
        }
        supabaseConnection = properties.getProperty("SUPABASE_CONNECTION");
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(supabaseConnection);
    }
}