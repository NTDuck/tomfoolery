package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import com.github.javafaker.Faker;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import java.util.stream.Stream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "password" }, dataProviderClass = PasswordEncoderTest.Dataprovider.class)
public abstract class PasswordEncoderTest extends BaseUnitTest<PasswordEncoder> {
    @Test
    public void GivenRawPassword_WhenEncodingPassword_ExpectValidEncodedPassword(@NonNull SecureString rawPassword) {
        val encodedPassword = this.testSubject.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(this.testSubject.verify(rawPassword, encodedPassword));
    }

    public static class Dataprovider {
        private static final @Unsigned int NUMBER_OF_PASSWORDS = 44;
        private static final @NonNull Faker faker = Faker.instance();

        @DataProvider
        public static Object[][] provide() {
            return createMockRawPasswords(NUMBER_OF_PASSWORDS);
        }

        private static @NonNull SecureString createMockRawPassword() {
            val rawPassword = faker.internet().password();
            return SecureString.of(rawPassword);
        }

        private static @NonNull Object[][] createMockRawPasswords(@Unsigned int count) {
            return Stream.generate(Dataprovider::createMockRawPassword)
                .limit(count)
                .map(password -> new Object[] { password })
                .toArray(Object[][]::new);
        }
    }
}
