package org.bonitasoft.shell.completer;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodArgumentParser {

    private String methodName;
    private int argumentIndex;

    public ReflectMethodArgumentParser(ArgumentParser commandLine) {
        methodName = commandLine.getArgumentAt(0);
        argumentIndex = commandLine.getLastArgumentIndex() - 1;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

}
