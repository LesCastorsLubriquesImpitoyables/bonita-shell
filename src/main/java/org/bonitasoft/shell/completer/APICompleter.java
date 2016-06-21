package org.bonitasoft.shell.completer;

import static jline.internal.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;

/**
 * @author Baptiste Mesta
 */
public class APICompleter implements Completer {
    private final ShellContext context;
    private HashMap<String, ShellCommand> commands;

    public APICompleter(HashMap<String, ShellCommand> commands) {
        this.commands = commands;
        context = ShellContext.getInstance();
    }


    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        SortedSet<String> strings = new TreeSet<>();
        for (Map.Entry<String, ShellCommand> command : commands.entrySet()) {
            if (command.getValue().isPlatform() == context.isLoggedOnPlatform()) {
                strings.add(command.getKey());
            }
        }
        if (buffer == null) {
            candidates.addAll(strings);
        } else {
            for (String match : strings.tailSet(buffer)) {
                if (!match.startsWith(buffer)) {
                    break;
                }

                candidates.add(match);
            }
        }

        return candidates.isEmpty() ? -1 : 0;
    }
}
