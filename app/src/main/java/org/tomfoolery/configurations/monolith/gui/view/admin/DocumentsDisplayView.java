package org.tomfoolery.configurations.monolith.gui.view.admin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.view.user.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;

public class DocumentsDisplayView extends ShowDocumentsView {
    public DocumentsDisplayView(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }
}
