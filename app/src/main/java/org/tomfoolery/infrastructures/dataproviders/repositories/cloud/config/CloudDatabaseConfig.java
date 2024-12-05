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
    private final @NonNull String supabaseConnection = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.xshacoftigxrlenvttlf&password=Hieujson1234512345%25";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(supabaseConnection);
    }
}
