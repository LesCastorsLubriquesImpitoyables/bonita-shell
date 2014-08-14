package org.bonitasoft.shell.command.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.bpm.connector.ConnectorInstance;
import org.bonitasoft.engine.bpm.connector.ConnectorInstancesSearchDescriptor;
import org.bonitasoft.engine.bpm.connector.ConnectorState;
import org.bonitasoft.engine.bpm.connector.ConnectorStateReset;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.flownode.ActivityStates;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.InvalidSessionException;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.NoParamCompleter;

import com.bonitasoft.engine.api.ProcessAPI;

/**
 * All connectors and tasks are replayed.
 * On large number of available tasks or consuming connector it might impact temporarly the Engine performance.
 *
 * @author Christophe Gomes
 * @author Antoine Mottier
 */
public class ReplayFailedTaskCommand extends ShellCommand {

    public static final String COMMAND_NAME = "tools_replay_failed_task";
    private final List<BonitaCompleter> completers;

    public ReplayFailedTaskCommand() {
        completers = Arrays.asList((BonitaCompleter) new NoParamCompleter("No args"));
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public boolean execute(final List<String> args, final ShellContext context) throws Exception {
        try {
            final ProcessAPI processAPI = context.getProcessAPI();

            // Search all failed connectors and flag them to be replayed
            SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
            searchOptionsBuilder.filter(ConnectorInstancesSearchDescriptor.STATE, ConnectorState.FAILED.name());
            final SearchResult<ConnectorInstance> searchConnectorInstances = processAPI.searchConnectorInstances(searchOptionsBuilder.done());
            final List<ConnectorInstance> connectorInstances = searchConnectorInstances.getResult();

            final Map<Long, ConnectorStateReset> connectorsMap = new HashMap<Long, ConnectorStateReset>();
            for (final ConnectorInstance connectorInstance : connectorInstances) {
                connectorsMap.put(Long.valueOf(connectorInstance.getId()), ConnectorStateReset.TO_RE_EXECUTE);
            }
            processAPI.setConnectorInstanceState(connectorsMap);

            // Search failed task and trigger re-execution
            searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
            searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.STATE_NAME, ActivityStates.FAILED_STATE);
            final List<ActivityInstance> failedActivities = processAPI.searchActivities(searchOptionsBuilder.done()).getResult();

            for (final ActivityInstance activityInstance : failedActivities) {
                processAPI.replayActivity(activityInstance.getId());
            }

            return true;
        } catch (final InvalidSessionException e) {
            System.err.println("Invalid session. Make sure you are logged in.");
            return false;
        }
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return completers;
    }

    @Override
    public void printHelp() {
        System.out.println("No args");

    }

    @Override
    public boolean validate(final List<String> args) {
        return true;
    }

}
