package org.bonitasoft.shell.completer.type;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;

import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public class BusinessArchiveTypeCompleter implements BonitaCompleter {
    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}
