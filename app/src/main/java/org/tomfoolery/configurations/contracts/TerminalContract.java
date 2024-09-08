package org.tomfoolery.configurations.contracts;

public interface TerminalContract {
    public interface View {
        public void displayResult(String result);
        public void onDestroy();
    }

    public interface Presenter {
        public void onUserInput(String userInput);
    }
}
