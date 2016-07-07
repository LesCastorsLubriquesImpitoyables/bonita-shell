package org.bonitasoft.shell.completer.type;

import java.io.Serializable;
import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

import groovy.lang.GroovyShell;

/**
 * @author Baptiste Mesta
 */
public class GroovyCompleter implements TypeHandler<Serializable> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public Serializable getValue(String argument) {
        argument = argument.trim();
        if (isAScript(argument)) {
            return argument;
        }
        return evaluate(argument.substring(2, argument.length() - 1));
    }

    private boolean isAScript(String argument) {
        return !argument.startsWith("${") || !argument.endsWith("}");
    }

    private Serializable evaluate(String argument) {
        final GroovyShell shell = new GroovyShell();
        return (Serializable) shell.evaluate(argument);
    }

    @Override
    public boolean isCastableTo(String argument) {
        return isAScript(argument);
    }

    @Override
    public String getString(Serializable result) {
        return String.valueOf(result);
    }
}
