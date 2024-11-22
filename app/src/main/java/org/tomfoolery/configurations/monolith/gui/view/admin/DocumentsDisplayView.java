package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.tomfoolery.core.domain.documents.Document;

public class DocumentsDisplayView {
    @FXML
    private TableView<Document> documentsTable;

    @FXML
    public void initialize() {
        loadDocuments();
    }

    public void getDocuments() {

    }

    public void loadDocuments() {
        documentsTable.getItems().addAll();
    }
}
