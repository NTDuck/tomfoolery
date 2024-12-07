package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.testng.Assert.*;

@Test(groups = "cloud")
public class CloudDatabaseConfigTest extends BaseUnitTest<CloudDatabaseConfig> {
    private final @NonNull CloudDatabaseConfig cloudDatabaseConfig = CloudDatabaseConfig.of();

    @Override
    protected @NonNull CloudDatabaseConfig createTestSubject() {
        return CloudDatabaseConfig.of();
    }

    @BeforeClass
    public void setUp() {
        super.setUp();
    }

    @Test
    public void WhenConnectingToDatabase_ExpectSuccessfulConnection() {
        try (Connection connection = this.cloudDatabaseConfig.connect()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(5), "Connection should be valid");
        } catch (SQLException e) {
            fail("Failed to connect to the database: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = { "WhenConnectingToDatabase_ExpectSuccessfulConnection" })
    public void GivenValidConnection_WhenQueryingDatabase_ExpectExpectedResult() {
        try (Connection connection = this.cloudDatabaseConfig.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            assertTrue(resultSet.next(), "ResultSet should have at least one row");
            assertEquals(resultSet.getInt(1), 1, "Query result should match expected value");
        } catch (SQLException e) {
            fail("Failed to execute query: " + e.getMessage());
        }
    }
}

