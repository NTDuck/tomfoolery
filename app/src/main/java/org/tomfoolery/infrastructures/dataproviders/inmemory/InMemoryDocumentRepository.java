package org.tomfoolery.infrastructures.dataproviders.inmemory;

import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.infrastructures.utils.Trie;

import javax.annotation.Nullable;

public class InMemoryDocumentRepository implements DocumentRepository {
    private final HashMap<Document.Id, Document> documents = new HashMap<>();
    private final Trie<String> titleTrie = Trie.of();
    private final Trie<String> authorTrie = Trie.of();
    private final Trie<String> genreTrie = Trie.of();
    @Override
    public void save(@NonNull Document document) {
        Document.Id id = document.getId();
        documents.put(id, document);
        titleTrie.put(document.getMetadata().getTitle().toLowerCase(), id.getValue());
        for (String author : document.getMetadata().getAuthors()) {
            authorTrie.put(author.toLowerCase(), id.getValue());
        }
        for (String genre : document.getMetadata().getGenres()) {
            genreTrie.put(genre.toLowerCase(), id.getValue());
        }
    }

    @Override
    public void delete(@NonNull Document.Id entityId) {
        Document document = documents.remove(entityId);
        if (document != null) {
            titleTrie.remove(document.getMetadata().getTitle().toLowerCase(), entityId.getValue());
            for (String author : document.getMetadata().getAuthors()) {
                authorTrie.remove(author.toLowerCase(), entityId.getValue());
            }
            for (String genre : document.getMetadata().getGenres()) {
                genreTrie.remove(genre.toLowerCase(), entityId.getValue());
            }
        }
    }

    @Override
    public @Nullable Document getById(Document.Id entityId) {
        return documents.get(entityId);
    }

    @Override
    public @NonNull Collection<Document> show() {
        return new ArrayList<>(documents.values());
    }

    @Override
    public @NonNull Collection<Document> searchByTitle(@NonNull String title) {
        List<String> ids = titleTrie.prefixSearch(title.toLowerCase());
        return getDocumentsByIds(ids);
    }

    @Override
    public @NonNull Collection<Document> searchByAuthor(@NonNull String author) {
        List<String> ids = authorTrie.prefixSearch(author.toLowerCase());
        return getDocumentsByIds(ids);
    }

    @Override
    public @NonNull Collection<Document> searchByGenre(@NonNull String genre) {
        List<String> ids = genreTrie.prefixSearch(genre.toLowerCase());
        return getDocumentsByIds(ids);
    }

    @Override
    public @NonNull Collection<Document> searchByPatron(ReadonlyUser.@NonNull Id patronId) {
        return documents.values().stream()
                .filter(doc -> doc.getAudit().getBorrowingPatronIds().contains(patronId))
                .collect(Collectors.toList());
    }

    private Collection<Document> getDocumentsByIds(List<String> ids) {
        return ids.stream()
                .map(Document.Id::of)
                .map(documents::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
