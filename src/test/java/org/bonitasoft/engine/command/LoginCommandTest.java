package org.bonitasoft.engine.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.LoginCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginCommandTest {

    @Mock
    private ShellContext context;

    private LoginCommand loginCommand;

    @Before
    public void setup() {
        loginCommand = new LoginCommand();
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("login", loginCommand.getName());
    }

    @Test
    public void testPrintHelp() throws Exception {
        loginCommand.printHelp();
    }

    @Test
    public void testValidate() throws Exception {
        assertFalse(loginCommand.validate(Arrays.asList("john")));
        assertFalse(loginCommand.validate(Arrays.asList("john", "jack", "james")));
        assertFalse(loginCommand.validate(Arrays.asList("john", "bpm")));
        assertTrue(loginCommand.validate(Arrays.asList("tenant", "john", "bpm")));
    }

    @Test
    public void testExecute() throws Exception {
        loginCommand.execute(Arrays.asList("tenant", "john", "bpm"), context);
        verify(context).login("john", "bpm");
    }

    @Test
    public void testExecuteWhenAlreadyLogged() throws Exception {
        when(context.isLogged()).thenReturn(true);
        loginCommand.execute(Arrays.asList("tenant", "john", "bpm"), context);
        verify(context, times(1)).isLogged();
        verify(context, times(0)).login("john", "bpm");
    }

}
