package org.bonitasoft.shell.command.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.SubscriptionShellContext;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.BonitaStringCompleter;

import com.bonitasoft.engine.api.ProcessAPI;

/**
 * @author Christophe Gomes
 * @author Antoine Mottier
 */
public class SearchLockedCommand extends ShellCommand {

    public static final String COMMAND_NAME = "tools_search_locked";

    private static final String BEFORE_ARG_NAME = "before:";

    private static final String AFTER_ARG_NAME = "after:";

    private static final SimpleDateFormat yyyyMmDdFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Search for locked commands";
    }

    private static Date parseDate(String dateString) throws ParseException {
        int semiColonIndex = dateString.indexOf(":");
        return yyyyMmDdFormat.parse(dateString.substring(semiColonIndex + 1));
    }

    @Override
    public boolean execute(List<String> args, ShellContext context) throws Exception {

        // 
        Date before = null;
        Date after = null;

        // Argument parsing
        for (String arg : args) {
            if (arg.startsWith(BEFORE_ARG_NAME)) {
                before = parseDate(arg);
            } else if (arg.startsWith(AFTER_ARG_NAME)) {
                after = parseDate(arg);
            } else {
                throw new IllegalArgumentException("Unknown command argument: " + arg);
            }
        }

        ProcessAPI processAPI = (ProcessAPI) context.getApi("ProcessAPI");

        // Search all process instance within specified time frame
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);

        System.out.println("before: " + before + " after:" + after);

        if ((before != null) && (after != null)) {
            searchOptionsBuilder.between(ProcessInstanceSearchDescriptor.START_DATE, Long.valueOf(after.getTime()),
                    Long.valueOf(before.getTime()));
        } else if (before != null) {
            searchOptionsBuilder.lessOrEquals(ProcessInstanceSearchDescriptor.START_DATE, Long.valueOf(before.getTime()));
        } else if (after != null) {
            searchOptionsBuilder.greaterOrEquals(ProcessInstanceSearchDescriptor.START_DATE, Long.valueOf(after.getTime()));
        }

        SearchResult<ProcessInstance> searchProcessInstances = processAPI.searchProcessInstances(searchOptionsBuilder.done());
        List<ProcessInstance> processInstances = searchProcessInstances.getResult();

        StringBuilder display = new StringBuilder();
        for (ProcessInstance processInstance : processInstances) {
            display.append("\n##############################################################################");
            display.append("\nProcess definition name: ");
            display.append("\nProcess definition version: ");
            display.append("\nProcess instance id: ").append(processInstance.getId());
            display.append("\nProcess instance state: ").append(processInstance.getState());
            display.append("\nProcess instance start date: ").append(processInstance.getStartDate());
            display.append("\n##############################################################################");
        }

        System.out.println(display.toString());

        //login install install
        //tools search_locked after:2014/07/07 before:2014/08/01

        return true;
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return Arrays.asList((BonitaCompleter) new BonitaStringCompleter("after:", "before:"), (BonitaCompleter) new BonitaStringCompleter("before:"));
    }

    @Override
    public void printHelp() {
        System.out.println(COMMAND_NAME + " before:YYYY/MM/DD after:YYYY/MM/DD");
    }

    @Override
    public boolean validate(List<String> args) {
        return true;
    }

    @Override
    public boolean isActive() {
        return SubscriptionShellContext.getInstance().isLogged();
    }
}
