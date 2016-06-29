package org.bonitasoft.shell;

/**
 * @author Baptiste Mesta
 */
public class BonitaShellFactorySubscription extends BonitaShellFactory {

    @Override
    public ShellContext getShellContext() {
        return SubscriptionShellContext.getInstance();
    }
}
