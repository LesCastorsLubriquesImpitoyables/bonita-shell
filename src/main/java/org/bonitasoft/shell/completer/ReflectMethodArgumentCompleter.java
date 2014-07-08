package org.bonitasoft.shell.completer;

import org.bonitasoft.shell.command.ReflectCommand;

import java.util.List;

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

        Class<?> type = command.getArgumentType(methodName,index);

        BonitaCompleter typeCompleter = TypeCompleters.getCompleter(type);

        return typeCompleter.complete(commandLine, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}

