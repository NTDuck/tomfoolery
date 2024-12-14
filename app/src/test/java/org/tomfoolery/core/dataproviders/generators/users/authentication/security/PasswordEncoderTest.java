package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import com.github.javafaker.Faker;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import java.util.Iterator;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "password" })
public abstract class PasswordEncoderTest extends BaseUnitTest<PasswordEncoder> {
    private static final @Unsigned int NUMBER_OF_INVOCATIONS = 10;

    @Test(dataProvider = "PasswordEncoderTestDataProvider")
    void GivenRawPassword_WhenEncodingPassword_ExpectValidEncodedPassword(@NonNull SecureString rawPassword) {
        val encodedPassword = this.testSubject.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(this.testSubject.verify(rawPassword, encodedPassword));
    }

    @DataProvider(name = "PasswordEncoderTestDataProvider", parallel = true)
    public @NonNull Iterator<Object[]> createData() {
        val faker = Faker.instance();

        return IntStream.range(0, NUMBER_OF_INVOCATIONS).parallel()
            .mapToObj(_ -> faker.internet().password())
            .map(SecureString::of)
            .map(rawPassword -> new Object[] { rawPassword })
            .iterator();
    }
}
