package org.tomfoolery.configurations.monolith_terminal_configurations.contracts;

public interface TerminalContract {
    public interface View {
        public void display(String content);
        public void requestUserActionSelection();
        public void requestUserActionInputs(String[] labels);
        public void onDestroy();
    }

    public interface Presenter {
        public void onStart();
        public void onUserActionSelection(String userActionSelection);
        public void onUserInputs(String[] userInputs);
    }
}
