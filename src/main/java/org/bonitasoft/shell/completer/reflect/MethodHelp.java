package org.bonitasoft.shell.completer.reflect;

import java.util.List;

/**
 * Created by baptiste on 09/07/14.
 */
public class MethodHelp {

    private final String comment;
    private final List<String> argumentNames;


    public MethodHelp(String comment, List<String> argumentNames) {
        this.comment = comment;
        this.argumentNames = argumentNames;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }
}
