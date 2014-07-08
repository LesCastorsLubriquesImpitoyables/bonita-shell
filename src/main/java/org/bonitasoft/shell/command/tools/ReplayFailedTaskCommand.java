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

import com.bonitasoft.engine.api.ProcessAPI;

public class ReplayFailedTaskCommand extends ShellCommand {

    public static final String COMMAND_NAME = "replay_failed_task";

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean execute(List<String> args, ShellContext context) throws Exception {
        ProcessAPI processAPI = context.getProcessAPI();

        // Search all failed connectors and flag them to be replayed 
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, Integer.MAX_VALUE);
        searchOptionsBuilder.filter(ConnectorInstancesSearchDescriptor.STATE, ConnectorState.FAILED);
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

        return false;
    }

    @Override
    public void printHelp() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validate(List<String> args) {
        // TODO Auto-generated method stub
        return false;
    }

}
