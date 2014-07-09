/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell;

import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.session.APISession;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.LoginAPI;
import com.bonitasoft.engine.api.ProcessAPI;
import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;

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
        throw new IllegalArgumentException("Unknown API: " + apiName);
    }

    public String getLoggedUser() {
        if(isLogged()) {
            return session.getUserName();
        }
        return "bonita";
    }
}
