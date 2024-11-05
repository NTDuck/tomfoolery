package org.tomfoolery.infrastructures.dataproviders.cloud;

import lombok.*;
import org.tomfoolery.core.domain.documents.Document;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.dataclasses.AverageRating;

import java.io.IOException;
import java.time.*;
import java.util.*;

@Data(staticConstructor = "of")
public class GoogleSheetsDocumentService {
    private final Sheets sheetsService;
    private static final String SPREADSHEET_ID = "1djV8X6qqdhfcq--uQAQl9els71SjWp9Ii9LlnJSHg8Y";
    private static final String RANGE = "Documents!A2:M";
    private static final String HEADER_RANGE = "Documents!A1:M1";

    private static final List<String> HEADERS = Arrays.asList(
            "ISBN",
            "Title",
            "Description",
            "Authors",
            "Genres",
            "PublishedYear",
            "Publisher",
            "CreatedByStaffId",
            "LastModifiedByStaffId",
            "BorrowingPatronIds",
            "Rating",
            "Created",
            "LastModified"
    );

    public GoogleSheetsDocumentService(@NonNull Sheets sheetsService) {
        this.sheetsService = sheetsService;
        initializeSheet();
    }

    private void initializeSheet() {

    }

    public void addDocument(@NonNull Document document) {

    }

    public void modifyDocument(@NonNull Document document){

    }

    public void deleteDocument(@NonNull Document.Id id) {

    }
}