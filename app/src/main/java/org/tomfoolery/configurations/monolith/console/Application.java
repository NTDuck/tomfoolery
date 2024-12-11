package org.tomfoolery.configurations.monolith.console;

import lombok.Cleanup;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.dev.FileCachedInMemoryApplicationContext;
import org.tomfoolery.configurations.contexts.test.DeterministicUsersApplicationContextProxy;
import org.tomfoolery.configurations.contexts.test.KaggleDocumentDatasetApplicationContextProxy;
import org.tomfoolery.configurations.contexts.test.MockingApplicationContextProxy;
import org.tomfoolery.configurations.contexts.utils.containers.ApplicationContextProxies;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.BuiltinIOProvider;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.containers.Views;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.UpdateStaffCredentialsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval.*;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchAdministratorsByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchPatronsByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.SearchStaffByUsernameActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.search.SearchDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.users.authentication.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByAuthenticationTokenActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByCredentialsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.persistence.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.GetDocumentBorrowStatusActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.review.persistence.AddDocumentReviewActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.review.persistence.RemoveDocumentRatingActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.retrieval.GetPatronUsernameAndMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentContentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentCoverImageActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.retrieval.ShowDocumentsWithoutContentActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Closeable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.CompletableFuture;

public final class Application implements Runnable, Closeable {
    private final @NonNull ApplicationContext context;
    private final @NonNull IOProvider ioProvider = BuiltinIOProvider.of();
    private final @NonNull Views views;

    public static @NonNull Application of(@NonNull ApplicationContext context) {
        return new Application(context);
    }

    private Application(@NonNull ApplicationContext context) {
        this.context = context;

        this.views = Views.of(
            // Selection views
            GuestSelectionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAdministratorRepository(), this.context.getPatronRepository(), this.context.getStaffRepository()),
            AdministratorSelectionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAdministratorRepository(), this.context.getPatronRepository(), this.context.getStaffRepository()),
            PatronSelectionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAdministratorRepository(), this.context.getPatronRepository(), this.context.getStaffRepository()),
            StaffSelectionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAdministratorRepository(), this.context.getPatronRepository(), this.context.getStaffRepository()),

            // Guest action views
            CreatePatronAccountActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getPasswordEncoder()),
            LogUserInByCredentialsActionView.of(this.ioProvider, this.context.getUserRepositories(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getPasswordEncoder()),
            LogUserInByAuthenticationTokenActionView.of(this.ioProvider, this.context.getUserRepositories(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),

            // Shared user action views
            LogUserOutActionView.of(this.ioProvider, this.context.getUserRepositories(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),

            GetDocumentByIdActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            SearchDocumentsActionView.of(this.ioProvider, this.context.getDocumentSearchGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            GetDocumentQrCodeActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getDocumentQrCodeGenerator(), this.context.getDocumentUrlGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            ShowDocumentsActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            GetDocumentRecommendationActionView.of(this.ioProvider, this.context.getDocumentRecommendationGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),

            // Administrator action views
            CreateStaffAccountActionView.of(this.ioProvider, this.context.getStaffRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getPasswordEncoder()),
            DeleteStaffAccountActionView.of(this.ioProvider, this.context.getStaffRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            UpdateStaffCredentialsActionView.of(this.ioProvider, this.context.getStaffRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getPasswordEncoder()),

            GetAdministratorByIdActionView.of(this.ioProvider, this.context.getAdministratorRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            GetPatronByIdActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            GetStaffByIdActionView.of(this.ioProvider, this.context.getStaffRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ShowAdministratorAccountsActionView.of(this.ioProvider, this.context.getAdministratorRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ShowPatronAccountsActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ShowStaffAccountsActionView.of(this.ioProvider, this.context.getStaffRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            SearchAdministratorsByUsernameActionView.of(this.ioProvider, this.context.getAdministratorSearchGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            SearchPatronsByUsernameActionView.of(this.ioProvider, this.context.getPatronSearchGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            SearchStaffByUsernameActionView.of(this.ioProvider, this.context.getStaffSearchGenerator(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),

            // Patron action views
            GetPatronUsernameAndMetadataActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            DeletePatronAccountActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getPasswordEncoder()),
            UpdatePatronMetadataActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            UpdatePatronPasswordActionView.of(this.ioProvider, this.context.getPatronRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getPasswordEncoder()),

            BorrowDocumentActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getBorrowingSessionRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ReadBorrowedDocumentActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getDocumentContentRepository(), this.context.getBorrowingSessionRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            ReturnDocumentActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getBorrowingSessionRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ShowBorrowedDocumentsActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getBorrowingSessionRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider()),
            GetDocumentBorrowStatusActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getBorrowingSessionRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),

            AddDocumentReviewActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getReviewRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            RemoveDocumentRatingActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getReviewRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),

            // Staff action views
            AddDocumentActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getDocumentContentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileVerifier(), this.context.getFileStorageProvider()),
            RemoveDocumentRatingActionView.of(this.ioProvider, this.context.getHybridDocumentRepository(), this.context.getReviewRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            UpdateDocumentContentActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getDocumentContentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileVerifier(), this.context.getFileStorageProvider()),
            UpdateDocumentCoverImageActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileVerifier(), this.context.getFileStorageProvider()),
            UpdateDocumentMetadataActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository()),
            ShowDocumentsWithoutContentActionView.of(this.ioProvider, this.context.getDocumentRepository(), this.context.getDocumentContentRepository(), this.context.getAuthenticationTokenGenerator(), this.context.getAuthenticationTokenRepository(), this.context.getFileStorageProvider())
        );
    }

    @Override
    public void run() {
        BaseView view;
        Class<? extends BaseView> viewClass = LogUserInByAuthenticationTokenActionView.class;

        do {
            view = this.views.getViewByClass(viewClass);
            assert view != null;   // Expected

            view.run();
            viewClass = view.getNextViewClass();

        } while (viewClass != null);
    }

    public static void main(@NonNull String[] args) throws Exception {
        val applicationContext = FileCachedInMemoryApplicationContext.of();

        val applicationContextProxies = ApplicationContextProxies.of(List.of(
            MockingApplicationContextProxy.of(),
            DeterministicUsersApplicationContextProxy.of(),
            KaggleDocumentDatasetApplicationContextProxy.of()
        ));
        CompletableFuture.runAsync(() -> applicationContextProxies.intercept(applicationContext));

        @Cleanup
        val application = Application.of(applicationContext);

        // Prevents blocking when application terminates
        application.run();
    }
}
