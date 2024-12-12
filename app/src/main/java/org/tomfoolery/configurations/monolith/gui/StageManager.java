package org.tomfoolery.configurations.monolith.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.dev.FileCachedInMemoryApplicationContext;
import org.tomfoolery.configurations.monolith.gui.view.admin.layout.AdminView;
import org.tomfoolery.configurations.monolith.gui.view.guest.LoginView;
import org.tomfoolery.configurations.monolith.gui.view.guest.SignupView;
import org.tomfoolery.configurations.monolith.gui.view.patron.layout.PatronView;
import org.tomfoolery.configurations.monolith.gui.view.staff.layout.StaffView;
import java.util.Objects;

@Getter
public class StageManager {
//    private static final @NonNull String APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME = "tomfoolery.context";
    private final @NonNull ApplicationContext resources = FileCachedInMemoryApplicationContext.of();

    private static StageManager instance;

    @Setter
    private Stage primaryStage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public StackPane getRootStackPane() {
        return (StackPane) this.getPrimaryStage().getScene().getRoot();
    }

    public void setLoginStageProperties() {
        primaryStage.setMinHeight(0);
        primaryStage.setMinWidth(0);

        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);

        primaryStage.setWidth(600);
        primaryStage.setHeight(550);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void setSignupStageProperties() {
        primaryStage.setResizable(false);

        primaryStage.setMaximized(false);

        primaryStage.setWidth(900);
        primaryStage.setHeight(600);

        Image icon = new Image(Objects.requireNonNull(StageManager.class.getResourceAsStream("/images/logo.png")));
        primaryStage.getIcons().add(icon);
    }

    public void setMainStageProperties() {
        primaryStage.setResizable(true);

        boolean isMaximized = primaryStage.isMaximized();
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        if (currentHeight < 1080 || currentWidth < 1920) {
            primaryStage.setWidth(1920);
            primaryStage.setHeight(1080);
        } else {
            primaryStage.setMaximized(isMaximized);
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);
    }

        primaryStage.setMinHeight(1080);
        primaryStage.setMinWidth(1920);

        primaryStage.setTitle("Tomfoolery - Library Management Application");
    }

    @SneakyThrows
    public void openLoginMenu() {
        LoginView controller = new LoginView(
                resources.getUserRepositories(),
                resources.getAuthenticationTokenGenerator(),
                resources.getAuthenticationTokenRepository(),
                resources.getPasswordEncoder()
        );

        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/LoginMenu.fxml"));
        loader.setController(controller);

        VBox root = loader.load();

        Scene scene = new Scene(root);

        setLoginStageProperties();
        primaryStage.setTitle("Tomfoolery - Login");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    @SneakyThrows
    public void openSignupMenu() {
        SignupView signUpController = new SignupView(resources.getPatronRepository(), resources.getPasswordEncoder());

        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource("/fxml/SignupMenu.fxml"));
        loader.setController(signUpController);

        VBox root = loader.load();

        Scene scene = new Scene(root);

        setSignupStageProperties();
        primaryStage.setTitle("Tomfoolery - Sign up");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void loadAdminView(StageManager.ContentType contentType) {
        AdminView adminView = new AdminView();
        adminView.loadView(contentType);
    }

    public void loadStaffView(StageManager.ContentType contentType) {
        StaffView staffView = new StaffView();
        staffView.loadView(contentType);
    }

    public void loadPatronView(StageManager.ContentType contentType) {
        PatronView patronView = new PatronView();
        patronView.loadView(contentType);
    }

//    private @NonNull ApplicationContext getApplicationContextFromEnvironment() {
//        val contextClassName = System.getenv(APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME);
//
//        if (contextClassName == null || contextClassName.isBlank()) {
//            throw new MissingResourceException(String.format("""
//            Required resource is missing: `ApplicationContext`.
//            Specify in `build.gradle.kts` as follows:
//
//            ```
//            tasks.register<JavaExec>("runXXX") {
//                environment[%s] = "<path.to.ApplicationContextYYY>"
//            }
//            ```""", APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME
//            ), contextClassName, APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME);
//        }
//
//        try {
//            val contextClass = Class.forName(contextClassName);
//            val contextConstructor = contextClass.getDeclaredConstructor();
//
//            return (ApplicationContext) contextConstructor.newInstance();
//
//        } catch (ClassNotFoundException exception) {
//            throw new MissingResourceException(String.format("""
//                Required resource is invalid: `ApplicationContext`.
//                Specify in `build.gradle.kts` as follows:
//
//                ```
//                tasks.register<JavaExec>("runXXX") {
//                    environment[%s] = "<path.to.ApplicationContextYYY>"
//                }
//                ```""", APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME
//            ), contextClassName, APPLICATION_CONTEXT_ENVIRONMENT_VARIABLE_NAME);
//
//        } catch (NoSuchMethodException | IllegalAccessException exception) {
//            throw new RuntimeException(String.format("""
//                Class `%s` must provide a public default constructor.
//                """, contextClassName));
//
//        } catch (InvocationTargetException exception) {
//            throw new RuntimeException(exception.getCause());
//
//        } catch (InstantiationException e) {
//            throw new RuntimeException(String.format("""
//                Class `%s` must not be abstract.
//                """, contextClassName));
//        }
//    }

    public enum ContentType {
        ADMIN_DASHBOARD,
        ADMIN_DISCOVER,
        ADMIN_CONTROL_CENTER,
        STAFF_DASHBOARD,
        STAFF_DISCOVER,
        STAFF_DOCUMENTS_MANAGEMENT,
        STAFF_SHOW_DOCUMENTS_WITHOUT_CONTENT,
        PATRON_DASHBOARD,
        PATRON_SHOW_BORROWED,
        PATRON_DISCOVER,
        PATRON_ACCOUNT_CENTER
    }
}