package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.*;

public class CloudDatabaseConfigTest {
    private static CloudDatabaseConfig cloudDatabaseConfig;
    @BeforeClass
    public void setup() throws IOException {
        Path configFilePath = Path.of("src/main/resources/config.properties");

        if (!Files.exists(configFilePath)) {
            throw new IOException("Configuration file not found at " + configFilePath.toAbsolutePath());
        }

        cloudDatabaseConfig = new CloudDatabaseConfig(configFilePath.toString());
    }

    @Test
    public void testConnection() {
        try (Connection connection = cloudDatabaseConfig.connect()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            System.out.println("Connection test successful!");
        } catch (SQLException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void cleanup() {

    }
}
