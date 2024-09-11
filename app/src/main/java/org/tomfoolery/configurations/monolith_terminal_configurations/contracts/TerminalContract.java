package org.tomfoolery.configurations.monolith_terminal_configurations.contracts;

public interface TerminalContract {
    interface View {
        void display(String content);
        void requestUserActionSelection();
        void requestUserActionInputs(String[] labels);
        void onDestroy();
    }

    interface Presenter {
        void onStart();
        void onUserActionSelection(String userActionSelection);
        void onUserActionInputs(String[] userInputs);
    }
}
