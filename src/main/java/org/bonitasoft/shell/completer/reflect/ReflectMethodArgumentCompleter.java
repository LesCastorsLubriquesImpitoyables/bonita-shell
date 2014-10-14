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

    private ReflectCommand command;

    public ReflectMethodArgumentCompleter(ReflectCommand command) {
        this.command = command;
    }


    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {

        ReflectMethodArgumentParser parser = new ReflectMethodArgumentParser(commandLine);

        String methodName = parser.getMethodName();
        int index = parser.getArgumentIndex();

        List<Class<?>> types = command.getArgumentType(methodName, index);

        BonitaCompleter typeCompleter = TypeCompleters.getCompleter(types);
        if(typeCompleter == null){
            return 0;
        }
        return typeCompleter.complete(commandLine, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}
