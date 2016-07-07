package org.bonitasoft.shell;

import org.bonitasoft.engine.bpm.BaseElement;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.job.FailedJob;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.shell.completer.type.BaseElementTypeCompleter;
import org.bonitasoft.shell.completer.type.BusinessArchiveTypeCompleter;
import org.bonitasoft.shell.completer.type.FailedJobTypeCompleter;
import org.bonitasoft.shell.completer.type.ProcessDefinitionTypeCompleter;
import org.bonitasoft.shell.completer.type.ProcessDeploymentInfoTypeCompleter;
import org.bonitasoft.shell.completer.type.SearchResultTypeCompleter;
import org.bonitasoft.shell.completer.type.TypeCompleters;

/**
 * @author Baptiste Mesta
 */
public class BonitaShellFactorySubscription extends BonitaShellFactory {

    @Override
    public Shell createShell() throws Exception {
        Shell shell = super.createShell();
        TypeCompleters.addCompleter(ProcessDefinition.class, new ProcessDefinitionTypeCompleter());
        TypeCompleters.addCompleter(SearchResult.class, new SearchResultTypeCompleter());
        TypeCompleters.addCompleter(ProcessDeploymentInfo.class, new ProcessDeploymentInfoTypeCompleter());
        TypeCompleters.addCompleter(BaseElement.class, new BaseElementTypeCompleter());
        TypeCompleters.addCompleter(FailedJob.class, new FailedJobTypeCompleter());
        TypeCompleters.addCompleter(BusinessArchive.class, new BusinessArchiveTypeCompleter());
        return shell;
    }

    @Override
    public ShellContext getShellContext() {
        return SubscriptionShellContext.getInstance();
    }
}
