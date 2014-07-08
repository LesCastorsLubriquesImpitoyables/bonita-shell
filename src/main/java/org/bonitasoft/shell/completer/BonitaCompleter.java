package org.bonitasoft.shell.completer;

import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public interface BonitaCompleter {


        int complete(ArgumentParser commandLine, List<CharSequence> candidates);


}
