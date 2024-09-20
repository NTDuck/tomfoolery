//package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;
//
//import java.util.Arrays;
//
//public abstract class Presenter {
//    private final String selectionLabel;
//    private final String[] parameterLabels;
//
//    protected Presenter(String selectionLabel, String... parameterLabels) {
//        this.selectionLabel = selectionLabel;
//        this.parameterLabels = generateFormattedParameterLabels(parameterLabels);
//    }
//
//    public final String getSelectionLabel() {
//        return this.selectionLabel;
//    }
//
//    public final String[] getParameterLabels() {
//        return this.parameterLabels;
//    }
//
//    public abstract String generateResponse(String[] userInput);
//
//    private static String[] generateFormattedParameterLabels(String[] labels) {
//        return Arrays.stream(labels)
//            .map(Presenter::generateFormattedParameterLabel)
//            .toArray(String[]::new);
//    }
//
//    private static String generateFormattedParameterLabel(String label) {
//        return "Enter " + label + ": ";
//    }
//}
