package org.bonitasoft.shell.command.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;

import com.bonitasoft.engine.api.ProcessAPI;

/**
 * @author Christophe Gomes
 * @author Antoine Mottier
 */
public class SearchLockedCommand extends ShellCommand {

    public static final String COMMAND_NAME = "search_locked";

    private static final String BEFORE_ARG_NAME = "before:";

    private static final String AFTER_ARG_NAME = "after:";

    private static final SimpleDateFormat yyyyMmDdFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public String getName() {
        return COMMAND_NAME;
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

        ProcessAPI processAPI = context.getProcessAPI();

        // Search all process instance within specified time frame
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);

        if ((before != null) && (after != null)) {
            searchOptionsBuilder.between(org.bonitasoft.engine.bpm.process.ProcessInstanceSearchDescriptor.START_DATE, after, before);
        }

        SearchResult<ProcessInstance> searchProcessInstances = processAPI.searchProcessInstances(searchOptionsBuilder.done());
        List<ProcessInstance> processInstances = searchProcessInstances.getResult();

        for (ProcessInstance processInstance : processInstances) {
            StringBuilder display = new StringBuilder();
            display.append("##############################################################################");
            display.append("Process definition name: ");
            display.append("Process definition version: ");
            display.append("Process instance id: ").append(processInstance.getId());
            display.append("Process instance state: ").append(processInstance.getState());
            display.append("Process instance start date: ").append(processInstance.getStartDate());
            display.append("##############################################################################");
        }

        return true;
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return super.getCompleters();
    }

    @Override
    public void printHelp() {
        System.out.println(COMMAND_NAME + " before:YYYY/MM/DD after:YYYY/MM/DD");
    }

    @Override
    public boolean validate(List<String> args) {
        return true;
    }

}
