package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config;

import io.github.jan.supabase.SupabaseClientBuilder;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.SupabaseClient;
import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@RequiredArgsConstructor(staticName = "of")
public class CloudDatabaseConfig {
    private final String supabaseUrl;
    private final String supabaseKey;

    public CloudDatabaseConfig(String configFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        }

        this.supabaseUrl = properties.getProperty("SUPABASE_URL");
        this.supabaseKey = properties.getProperty("SUPABASE_API_KEY");

        if (supabaseUrl == null || supabaseKey == null) {
            throw new IllegalStateException("Missing Supabase configurations in " + configFilePath);
        }
    }
}
