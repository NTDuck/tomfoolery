package org.tomfoolery.configurations.monolith.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64AuthenticationTokenService;
import org.tomfoolery.infrastructures.dataproviders.hash.base64.Base64PasswordService;
import org.tomfoolery.infrastructures.dataproviders.inmemory.*;

public class MainApplication extends Application {
    private static final @NonNull @Getter AdministratorRepository administratorRepository = InMemoryAdministratorRepository.of();
    private static final @NonNull @Getter StaffRepository staffRepository = InMemoryStaffRepository.of();
    private static final @NonNull @Getter PatronRepository patronRepository = InMemoryPatronRepository.of();
    private static final @NonNull @Getter PasswordService passwordService = Base64PasswordService.of();
    private static final @NonNull @Getter DocumentRepository documentRepository = InMemoryDocumentRepository.of();
    private static final @NonNull @Getter UserRepositories userRepositories = UserRepositories.of(administratorRepository, staffRepository, patronRepository);
    private static final @NonNull @Getter AuthenticationTokenService authenticationTokenService = Base64AuthenticationTokenService.of();
    private static final @NonNull @Getter AuthenticationTokenRepository authenticationTokenRepository = InMemoryAuthenticationTokenRepository.of();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.setPrimaryStage(primaryStage);
        StageManager.openLoginMenu();

//        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/LoginMenu.fxml"));
//        Parent root = loginLoader.load();
//
//        Scene scene = new Scene(root);
//        primaryStage.setHeight(800);
//        primaryStage.setWidth(600);
//        primaryStage.setResizable(false);
//        primaryStage.setTitle("Tomfoolery - Login");
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }
}