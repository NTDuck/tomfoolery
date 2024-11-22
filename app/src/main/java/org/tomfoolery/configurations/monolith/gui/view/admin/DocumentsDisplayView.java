package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.tomfoolery.core.domain.documents.Document;

public class DocumentsDisplayView {
    @FXML
    private TableView<DocumentViewModel> documentsTable;

    @FXML
    public void initialize() {
        loadDocuments();
    }

    public ObservableList<DocumentViewModel> getDocuments() {
        ObservableList<DocumentViewModel> documents = FXCollections.observableArrayList();
        for (int i = 0; i < 50; ++i) {
            DocumentViewModel example = new DocumentViewModel(
                    "95728445",
                    "The noxian guillotine",
                    "Pham Trung Hieu, Nguyen Anh Duy",
                    "Horror, Science, Adventure",
                    "2024",
                    "Today",
                    "Now"
            );
            documents.add(example);
        }
        return documents;
    }

    public void loadDocuments() {
        documentsTable.getItems().clear();
        documentsTable.getItems().addAll(getDocuments());
    }

    @AllArgsConstructor
    @Getter
    public static class DocumentViewModel {
        final String ISBN;
        final String title;
        final String authors;
        final String genres;
        final String publishedYear;
        final String created;
        final String lastModified;

        public DocumentViewModel(Document document) {
            ISBN = document.getId().getISBN();
            title = document.getMetadata().getTitle();
            authors = String.join(", ", document.getMetadata().getAuthors());
            genres = String.join(", ", document.getMetadata().getGenres());
            publishedYear = document.getMetadata().getPublishedYear().toString();
            created = String.valueOf(document.getAudit().getTimestamps().getCreated());
            lastModified = String.valueOf(document.getAudit().getTimestamps().getLastModified());
        }
    }
}
