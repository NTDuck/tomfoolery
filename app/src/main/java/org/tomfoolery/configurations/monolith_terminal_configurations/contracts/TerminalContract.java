package org.tomfoolery.configurations.monolith_terminal_configurations.contracts;

public interface TerminalContract {
    public interface View {
        public void displayResponse(String response);
        public void onDestroy();
    }

    public interface Presenter {
        public void onUserInput(String userInput);
    }
}
