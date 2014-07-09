package org.bonitasoft.shell.completer;

import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public interface CompletionHelper {

    void addHelp(ArgumentParser argumentParser, List<CharSequence> candidates);
}
