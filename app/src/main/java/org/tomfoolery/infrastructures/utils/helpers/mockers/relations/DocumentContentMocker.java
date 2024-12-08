package org.tomfoolery.infrastructures.utils.helpers.mockers.relations;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;

@NoArgsConstructor(staticName = "of")
public class DocumentContentMocker implements EntityMocker<DocumentContent, DocumentContent.Id> {
    private static final byte @NonNull[] MOCK_PDF_CONTENT = {
        '%', 'P', 'D', 'F', '-', '1', '.', '0', '\n',
        '1', ' ', '0', ' ', 'o', 'b', 'j', '\n',
        '<', '<', ' ', '/', 'T', 'y', 'p', 'e', ' ', '/', 'C', 'a', 't', 'a', 'l', 'o', 'g', ' ', '/', 'P', 'a', 'g', 'e', 's', ' ', '2', ' ', '0', ' ', 'R', ' ', '>', '>', '\n',
        'e', 'n', 'd', 'o', 'b', 'j', '\n',
        '2', ' ', '0', ' ', 'o', 'b', 'j', '\n',
        '<', '<', ' ', '/', 'T', 'y', 'p', 'e', ' ', '/', 'P', 'a', 'g', 'e', 's', ' ', '/', 'K', 'i', 'd', 's', ' ', '[', '3', ' ', '0', ' ', 'R', ']', ' ', '/', 'C', 'o', 'u', 'n', 't', ' ', '1', ' ', '>', '>', '\n',
        'e', 'n', 'd', 'o', 'b', 'j', '\n',
        '3', ' ', '0', ' ', 'o', 'b', 'j', '\n',
        '<', '<', ' ', '/', 'T', 'y', 'p', 'e', ' ', '/', 'P', 'a', 'g', 'e', ' ', '/', 'P', 'a', 'r', 'e', 'n', 't', ' ', '2', ' ', '0', ' ', 'R', ' ', '/', 'M', 'e', 'd', 'i', 'a', 'B', 'o', 'x', ' ', '[', '0', ' ', '0', ' ', '1', ' ', '1', ']', ' ', '>', '>', '\n',
        'e', 'n', 'd', 'o', 'b', 'j', '\n',
        'x', 'r', 'e', 'f', '\n',
        '0', ' ', '4', '\n',
        '0', '0', '0', '0', '0', '0', '0', '0', '0', ' ', '6', '5', '5', '3', '5', ' ', 'f', '\n',
        '0', '0', '0', '0', '0', '0', '0', '1', '0', ' ', '0', '0', '0', '0', '0', ' ', 'n', '\n',
        '0', '0', '0', '0', '0', '0', '0', '6', '0', ' ', '0', '0', '0', '0', '0', ' ', 'n', '\n',
        '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', ' ', '0', '0', '0', '0', '0', ' ', 'n', '\n',
        't', 'r', 'a', 'i', 'l', 'e', 'r', '\n',
        '<', '<', ' ', '/', 'R', 'o', 'o', 't', ' ', '1', ' ', '0', ' ', 'R', ' ', '/', 'S', 'i', 'z', 'e', ' ', '4', ' ', '>', '>', '\n',
        's', 't', 'a', 'r', 't', 'x', 'r', 'e', 'f', '\n',
        '1', '6', '7', '\n',
        '%', '%', 'E', 'O', 'F', '\n'
    };

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();

    @Override
    public DocumentContent.@NonNull Id createMockEntityId() {
        val documentId = this.documentMocker.createMockEntityId();

        return DocumentContent.Id.of(documentId);
    }

    @Override
    public @NonNull DocumentContent createMockEntityWithId(DocumentContent.@NonNull Id documentContentId) {
        return DocumentContent.of(documentContentId, MOCK_PDF_CONTENT);
    }
}
