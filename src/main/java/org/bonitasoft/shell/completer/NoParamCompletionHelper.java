package org.bonitasoft.shell.completer;

public class NoParamCompletionHelper implements CompletionHelper {

    private String helpMessage;

    public NoParamCompletionHelper(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    @Override
    public String getHelp(ArgumentParser argumentParser) {
        return helpMessage;
    }

}
