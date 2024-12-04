package org.tomfoolery.infrastructures.dataproviders.repositories.cloud;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomfoolery.abc.UnitTest;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.config.CloudDatabaseConfig;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudUserRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class CloudUserRepositoryTest extends UnitTest<CloudUserRepository<BaseUser>> {

    private static final @NonNull String SAMPLE_USERNAME = "sampleUser";
    private static final @NonNull char[] SAMPLE_PASSWORD = "securePass".toCharArray();

    private @NonNull BaseUser sampleUser;
    private final CloudDatabaseConfig cloudDatabaseConfig = CloudDatabaseConfig.of();

    @Override
    protected @NonNull CloudUserRepository<BaseUser> instantiate() {
        try {
            cloudDatabaseConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new CloudUserRepository<>(cloudDatabaseConfig);
    }

    @BeforeMethod
    public void setUp() {
        super.setUp();

        val id = BaseUser.Id.of(UUID.randomUUID());
        val credentials = BaseUser.Credentials.of(SAMPLE_USERNAME, SecureString.of(SAMPLE_PASSWORD));
        val timestamps = BaseUser.Audit.Timestamps.of(Instant.now());
        val audit = BaseUser.Audit.of(timestamps);

        this.sampleUser = new BaseUser(id, audit, credentials);

        this.unit.delete(sampleUser.getId());
    }

    @Test
    public void WhenSavingUser_ExpectUserToExist() {
        this.unit.save(sampleUser);

        val retrievedUser = this.unit.getById(sampleUser.getId());
        assertNotNull(retrievedUser, "User should exist after saving.");
        assertEquals(sampleUser.getId(), retrievedUser.getId(), "Saved user ID should match.");
    }

    @Test(dependsOnMethods = { "WhenSavingUser_ExpectUserToExist" })
    public void WhenRetrievingUserByUsername_ExpectMatchingUser() {
        val retrievedUser = this.unit.getByUsername(sampleUser.getCredentials().getUsername());
        assertNotNull(retrievedUser, "Retrieved user should not be null.");
        assertEquals(sampleUser.getCredentials().getUsername(), retrievedUser.getCredentials().getUsername(),
                "Retrieved username should match.");
    }

    @Test(dependsOnMethods = { "WhenSavingUser_ExpectUserToExist" })
    public void WhenDeletingUser_ExpectUserToBeAbsent() {
        this.unit.delete(sampleUser.getId());

        val retrievedUser = this.unit.getById(sampleUser.getId());
        assertNull(retrievedUser, "User should be null after deletion.");
    }

    @Test
    public void WhenListingUsers_ExpectNonEmptyList() {
        this.unit.save(sampleUser);

        val users = this.unit.show();
        assertTrue(users.size() > 0, "User list should not be empty.");
        assertTrue(users.stream().anyMatch(user -> user.getId().equals(sampleUser.getId())),
                "Saved user should be in the list.");
    }

    @AfterClass
    public void tearDownClass() {
        try {
            this.unit.delete(sampleUser.getId());

            val users = this.unit.show();
            for (BaseUser user : users) {
                this.unit.delete(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to clean up users after tests: " + e.getMessage());
        }
    }

}
