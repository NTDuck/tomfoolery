package org.tomfoolery.configurations.monolith.terminal.utils.contract;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public abstract class SelectionView implements View {
    private final @NonNull Rows rows;
    private @Nullable Class<? extends View> nextViewClass = this.getClass();

    public SelectionView(@NonNull Iterable<Row> rows) {
        this.rows = Rows.of(rows);
    }

    @Override
    public void run() {
        this.displayPrompt();
        this.displayRows();

        try {
            val selection = getSelectionFromUserInput();
            val row = this.rows.getRowBySelection(selection);

            if (row == null)
                throw new SelectionValueInvalidException();

            this.nextViewClass = row.getViewClass();
            this.displaySuccessMessage(row);

        } catch (SelectionFormatInvalidException exception) {
            this.nextViewClass = this.getClass();
            displayErrorMessageForSelectionFormatInvalidException();

        } catch (SelectionValueInvalidException exception) {
            this.nextViewClass = this.getClass();
            displayErrorMessageForSelectionValueInvalidException();
        }
    }

    @Override
    public final @Nullable Class<? extends View> getNextViewClass() {
        return this.nextViewClass;
    }

    private void displayPrompt() {
        val prompt = this.getPrompt();
        System.out.println(prompt);
    }

    protected abstract @NonNull String getPrompt();

    private void displayRows() {
        for (val row : this.rows)
            displayRow(row);
    }

    private static void displayRow(@NonNull Row row) {
        val rowAsString = getStringFromRow(row);
        System.out.println(rowAsString);
    }

    private static @NonNull String getStringFromRow(@NonNull Row row) {
        val selection = row.getSelection();
        val label = row.getLabel();

        return "[" + selection + "] " + label;
    }

    private static int getSelectionFromUserInput() throws SelectionFormatInvalidException {
        val scanner = ScannerService.getScanner();

        try {
            val selection = scanner.nextInt();
            scanner.nextLine();
            return selection;
        } catch (InputMismatchException exception) {
            throw new SelectionFormatInvalidException();
        }
    }

    private void displaySuccessMessage(@NonNull Row row) {
        val message = this.getSuccessMessage(row);
        System.out.println(message);
    }

    protected @NonNull String getSuccessMessage(@NonNull Row row) {
        val selection = row.getSelection();

        return "Success: selected [" + selection + "].";
    }

    private static void displayErrorMessageForSelectionFormatInvalidException() {
        val message = "Error: inputted selection is not a valid number.";
        System.out.println(message);
    }

    private static void displayErrorMessageForSelectionValueInvalidException() {
        val message = "Error: inputted selection does not match any action.";
        System.out.println(message);
    }

    @Value(staticConstructor = "of")
    public static class Row {
        int selection;
        @NonNull String label;
        @Nullable Class<? extends View> viewClass;
    }

    @NoArgsConstructor(staticName = "of")
    private static class Rows implements Iterable<Row> {
        private final @NonNull SequencedMap<Integer, Row> selectionToRowMap = new LinkedHashMap<>();

        private Rows(@NonNull Iterable<Row> rows) {
            for (val row : rows)
                this.addRow(row);
        }

        public static @NonNull Rows of(@NonNull Iterable<Row> rows) {
            return new Rows(rows);
        }

        public void addRow(@NonNull Row row) {
            this.selectionToRowMap.put(row.getSelection(), row);
        }

        public @Nullable Row getRowBySelection(int selection) {
            return this.selectionToRowMap.get(selection);
        }

        @Override
        public @NonNull Iterator<Row> iterator() {
            return this.selectionToRowMap.sequencedValues().iterator();
        }
    }

    private static class SelectionFormatInvalidException extends Exception {}
    private static class SelectionValueInvalidException extends Exception {}
}
