package org.bonitasoft.shell.command.tools;

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
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;

import com.bonitasoft.engine.api.ProcessAPI;

/**
 * All connectors and tasks are replayed.
 * On large number of available tasks or consuming connector it might impact temporarly the Engine performance.
 * 
 * @author Christophe Gomes
 * @author Antoine Mottier
 */
public class ReplayFailedTaskCommand extends ShellCommand {

    public static final String COMMAND_NAME = "replay_failed_task";

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public boolean execute(List<String> args, ShellContext context) throws Exception {
        ProcessAPI processAPI = context.getProcessAPI();

        // Search all failed connectors and flag them to be replayed 
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ConnectorInstancesSearchDescriptor.STATE, ConnectorState.FAILED.name());
        SearchResult<ConnectorInstance> searchConnectorInstances = processAPI.searchConnectorInstances(searchOptionsBuilder.done());
        List<ConnectorInstance> connectorInstances = searchConnectorInstances.getResult();

        Map<Long, ConnectorStateReset> connectorsMap = new HashMap<>();
        for (ConnectorInstance connectorInstance : connectorInstances) {
            connectorsMap.put(Long.valueOf(connectorInstance.getId()), ConnectorStateReset.TO_RE_EXECUTE);
        }
        processAPI.setConnectorInstanceState(connectorsMap);

        // Search failed task and trigger re-execution
        searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.STATE_NAME, ActivityStates.FAILED_STATE);
        List<ActivityInstance> failedActivities = processAPI.searchActivities(searchOptionsBuilder.done()).getResult();

        for (ActivityInstance activityInstance : failedActivities) {
            processAPI.replayActivity(activityInstance.getId());
        }

        return true;
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return super.getCompleters();
    }

    @Override
    public void printHelp() {
        System.out.println("No args");

    }

    @Override
    public boolean validate(List<String> args) {
        return true;
    }

}
