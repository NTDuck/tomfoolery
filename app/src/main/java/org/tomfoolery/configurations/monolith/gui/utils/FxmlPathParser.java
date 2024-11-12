package org.tomfoolery.configurations.monolith.gui.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

public class FxmlPathParser {
    private static final String FXML_BASE_PATH = "/fxml/";

    public static SceneInfo getSceneInfo(String fxmlPath) {
        if (fxmlPath == null || !fxmlPath.startsWith(FXML_BASE_PATH) || !fxmlPath.endsWith(".fxml")) {
            throw new IllegalArgumentException("Invalid FXML path format. Expected: /fxml/UserType/SceneType.fxml");
        }

        String cleanPath = fxmlPath.substring(FXML_BASE_PATH.length(), fxmlPath.length() - 5);

        // Split into components
        String[] components = cleanPath.split("/");
        if (components.length != 2) {
            throw new IllegalArgumentException("Path must contain exactly one user type and one scene type");
        }

        return SceneInfo.of(components[0], components[1]);
    }

    @Getter
    @Value(staticConstructor = "of")
    public static class SceneInfo {
        String userType;
        String sceneType;
    }
}
