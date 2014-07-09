/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.session.APISession;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.LoginAPI;
import com.bonitasoft.engine.api.MonitoringAPI;
import com.bonitasoft.engine.api.PageAPI;
import com.bonitasoft.engine.api.ProcessAPI;
import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.ReportingAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;
import com.bonitasoft.engine.api.ThemeAPI;

/**
 * @author Baptiste Mesta
 */
public class ShellContext {

    private static ShellContext INSTANCE = new ShellContext();

    public static ShellContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShellContext();
        }
        return INSTANCE;
    }

    private ShellContext() {
    }

    private LoginAPI loginAPI;

    private APISession session;

    // ------------------ Tenant ------------------------
    /**
     * @return
     */
    public boolean isLogged() {
        return session != null;
    }

    public void logout() throws Exception {
        loginAPI.logout(session);
        loginAPI = null;
        session = null;
    }

    /**
     * @param tenant
     * @param username
     * @param password
     */
    public void login(final String username, final String password) throws Exception {
        loginAPI = TenantAPIAccessor.getLoginAPI();
        session = loginAPI.login(username, password);
    }

    public IdentityAPI getIdentityAPI() throws BonitaException {
        return TenantAPIAccessor.getIdentityAPI(session);

    }

    public ProcessAPI getProcessAPI() throws BonitaException {
        return TenantAPIAccessor.getProcessAPI(session);
    }

    public ProfileAPI getProfileAPI() throws BonitaException {
        return TenantAPIAccessor.getProfileAPI(session);
    }

    public PageAPI getPageAPI() throws BonitaException {
        return TenantAPIAccessor.getPageAPI(session);
    }

    public MonitoringAPI getMonitoringAPI() throws BonitaException {
        return TenantAPIAccessor.getMonitoringAPI(session);
    }

    public ThemeAPI getThemeAPI() throws BonitaException {
        return TenantAPIAccessor.getThemeAPI(session);
    }

    public ReportingAPI getReportingAPI() throws BonitaException {
        return TenantAPIAccessor.getReportingAPI(session);
    }

    public CommandAPI getCommandAPI() throws BonitaException {
        return TenantAPIAccessor.getCommandAPI(session);
    }

    @Deprecated
    public Object getApi(final String apiName) throws Exception {
        if (apiName.equals("ProcessAPI")) {
            return getProcessAPI();
        }
        if (apiName.equals("IdentityAPI")) {
            return getIdentityAPI();
        }
        if (apiName.equals("ProfileAPI")) {
            return getProfileAPI();
        }
        if (apiName.equals("PageAPI")) {
            return getPageAPI();
        }
        if (apiName.equals("MonitoringAPI")) {
            return getMonitoringAPI();
        }
        if (apiName.equals("ThemeAPI")) {
            return getThemeAPI();
        }
        if (apiName.equals("ReportingAPI")) {
            return getReportingAPI();
        }
        if (apiName.equals("CommandAPI")) {
            return getCommandAPI();
        }
        throw new IllegalArgumentException("Unknown API: " + apiName);
    }

    public String getLoggedUser() {
        if(isLogged()) {
            return session.getUserName();
        }
        return "bonita";
    }
}
