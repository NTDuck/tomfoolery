package org.arbitrary;

import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

public class AppTest {
    @Test public void appHasName() {
        assertNotNull(App.NAME, "App should have static attribute NAME");
    }
};
