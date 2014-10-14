package org.bonitasoft.shell.completer.reflect;

import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;
import org.bonitasoft.shell.completer.type.TypeCompleters;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodArgumentCompleter implements BonitaCompleter {

    private ReflectMethodCommand command;

    public ReflectMethodArgumentCompleter(ReflectMethodCommand command) {
        this.command = command;
    }


    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {

        int index = commandLine.getLastArgumentIndex();

        List<Class<?>> types = command.getArgumentType(index);

        BonitaCompleter typeCompleter = TypeCompleters.getCompleter(types);
        if(typeCompleter == null){
            return 0;
        }
        return typeCompleter.complete(commandLine, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return new ReflectMethodCommandCompletionHelper(command);
    }
}
