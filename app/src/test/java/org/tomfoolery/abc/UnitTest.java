package org.tomfoolery.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.BeforeClass;

/**
 * @see <a href="https://stackoverflow.com/questions/155436/unit-test-naming-best-practices">Recommended practices</a>
 * @see <a href="https://stackoverflow.com/questions/3963708/gradle-how-to-display-test-results-in-the-console-in-real-time">Test logger</a>
 */
public abstract class UnitTest<Unit> {
    protected @NonNull Unit unit;

    protected abstract @NonNull Unit instantiate();

    @BeforeClass
    protected void setUp() {
        this.unit = this.instantiate();
    }
}
