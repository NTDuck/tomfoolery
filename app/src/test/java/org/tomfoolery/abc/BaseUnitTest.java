package org.tomfoolery.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @see <a href="https://stackoverflow.com/questions/155436/unit-test-naming-best-practices">Recommended practices</a>
 * @see <a href="https://stackoverflow.com/questions/3963708/gradle-how-to-display-test-results-in-the-console-in-real-time">Test logger</a>
 * @see <a href="https://stackoverflow.com/questions/7321407/what-is-sut-and-where-did-it-come-from">System Under Test</a>
 */
@Test(groups = { "unit" })
public abstract class BaseUnitTest<TestSubject> {
    protected @NonNull TestSubject testSubject;

    protected abstract @NonNull TestSubject createTestSubject();

    @BeforeClass
    protected void setUp() {
        this.testSubject = this.createTestSubject();
    }
}
