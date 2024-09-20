//package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class MainPresenter {
//    private final HashMap<Integer, Presenter> presenters;
//    private Presenter currentPresenter = null;
//
//    public MainPresenter() {
//        this.presenters = new HashMap<>();
//    }
//
//    public void addPresenter(Integer selection, Presenter presenter) {
//        this.presenters.putIfAbsent(selection, presenter);
//    }
//
//    public String generateMenu() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Welcome to My Application!\n");
//
//        for (Map.Entry<Integer, Presenter> entry : this.presenters.entrySet())
//            stringBuilder
//                .append("[").append(entry.getKey()).append("] ")
//                .append(entry.getValue().getSelectionLabel())
//                .append("\n");
//
//        return stringBuilder.toString();
//    }
//
//    public Boolean isSelectionValid(Integer selection) {
//        return this.presenters.containsKey(selection);
//    }
//
//    public void setSelection(Integer selection) {
//        assert this.isSelectionValid(selection);
//        this.currentPresenter = presenters.get(selection);
//    }
//
//    public String[] getParameterLabels() {
//        return this.currentPresenter.getParameterLabels();
//    }
//
//    public String generateResponse(String[] userInput) {
//        return this.currentPresenter.generateResponse(userInput);
//    }
//}
