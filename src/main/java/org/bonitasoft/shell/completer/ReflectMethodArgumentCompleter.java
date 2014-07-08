package org.bonitasoft.shell.completer;

import org.bonitasoft.shell.command.ReflectCommand;

import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodArgumentCompleter implements BonitaCompleter {

    public ReflectMethodArgumentCompleter(ReflectCommand command) {
    }


    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {

        return 0;
    }
}

