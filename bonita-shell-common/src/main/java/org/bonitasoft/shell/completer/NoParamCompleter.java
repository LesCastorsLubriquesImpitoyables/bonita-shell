package org.bonitasoft.shell.completer;

import java.util.List;

public class NoParamCompleter implements BonitaCompleter {

    private CompletionHelper completionHelper;

    public NoParamCompleter(String helpMessage) {
        completionHelper = new NoParamCompletionHelper(helpMessage);
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return completionHelper;
    }

}
