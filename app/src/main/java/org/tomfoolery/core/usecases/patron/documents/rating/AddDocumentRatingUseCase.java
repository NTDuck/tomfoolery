package org.tomfoolery.core.usecases.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.AverageRating;

import java.util.Collection;
import java.util.List;

public final class AddDocumentRatingUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentRatingUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull AddDocumentRatingUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new AddDocumentRatingUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private AddDocumentRatingUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, RatingValueInvalidException, PatronRatingAlreadyExistsException {
        val patronAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);

        val patronRatingValue = request.getRatingValue();
        ensureRatingValueIsValid(patronRatingValue);

        val documentId = request.getDocumentId();
        ensurePatronRatingDoesNotExist(patron, documentId);

        val document = getDocumentFromId(documentId);
        addPatronRatingToDocument(document, patron, patronRatingValue);

        this.documentRepository.save(document);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensureRatingValueIsValid(double ratingValue) throws RatingValueInvalidException {
        if (!AverageRating.isRatingValueValid(ratingValue))
            throw new RatingValueInvalidException();
    }

    private static void ensurePatronRatingDoesNotExist(@NonNull Patron patron, Document.@NonNull Id documentId) throws PatronRatingAlreadyExistsException {
        val patronAudit = patron.getAudit();
        val patronRatingValues = patronAudit.getRatingValues();

        if (patronRatingValues.containsKey(documentId))
            throw new PatronRatingAlreadyExistsException();
    }

    private @NonNull Document getDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private static void addPatronRatingToDocument(@NonNull Document document, @NonNull Patron patron, double ratingValue) {
        val patronAudit = patron.getAudit();
        val patronRatingValues = patronAudit.getRatingValues();

        val documentId = document.getId();
        val documentAudit = document.getAudit();
        val documentRating = documentAudit.getRating();

        patronRatingValues.put(documentId, ratingValue);
        documentRating.addRating(ratingValue);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        @Unsigned double ratingValue;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class RatingValueInvalidException extends Exception {}
    public static class PatronRatingAlreadyExistsException extends Exception {}
}
