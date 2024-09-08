package org.tomfoolery;

import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

public class DeprecatedAppTest {
    @Test public void appHasName() {
        assertNotNull(DeprecatedApp.NAME, "App should have static attribute NAME");
    }
};
