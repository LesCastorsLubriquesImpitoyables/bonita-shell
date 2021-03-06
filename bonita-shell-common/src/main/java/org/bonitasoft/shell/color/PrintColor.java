/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.color;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

/**
 * Allow to print colorized String.
 *
 * @author Joachim Segala
 */
public class PrintColor {

    private static boolean enabled;

    public static final void printRed(final String pText) {
        print(pText, Ansi.Color.RED, false);
    }

    public static final void printRedBold(final String pText) {
        print(pText, Ansi.Color.RED, true);
    }

    public static final void printGreen(final String pText) {
        print(pText, Ansi.Color.GREEN, false);
    }

    public static final void printGreenBold(final String pText) {
        print(pText, Ansi.Color.GREEN, true);
    }

    public static final void printBlue(final String pText) {
        print(pText, Ansi.Color.BLUE, false);
    }

    public static final void printBlueBold(final String pText) {
        print(pText, Ansi.Color.BLUE, true);
    }

    public static final void init() {
        AnsiConsole.systemInstall();
    }

    public static final void clean() {
        AnsiConsole.systemUninstall();
    }

    private static final Ansi ansi() {
        return new Ansi();
    }

    private static final void print(final String pText, final Color pColor,
                                    final boolean pBold) {
        if (enabled) {
            if (pBold) {
                System.out.println(ansi().bold().fg(pColor).a(pText).reset());
            } else {
                System.out.println(ansi().fg(pColor).a(pText).reset());
            }
        } else {
            System.out.println(pText);
        }
    }

    public static final String getGreenBold(final String pText) {
        return getColorized(pText, Ansi.Color.GREEN, true);
    }

    public static final String getGreen(final String pText) {
        return getColorized(pText, Ansi.Color.GREEN, false);
    }

    public static final String getBlueBold(final String pText) {
        return getColorized(pText, Ansi.Color.BLUE, true);
    }

    public static final String getBlue(final String pText) {
        return getColorized(pText, Ansi.Color.BLUE, false);
    }

    public static final String getRedBold(final String pText) {
        return getColorized(pText, Ansi.Color.RED, true);
    }

    public static final String getRed(final String pText) {
        return getColorized(pText, Ansi.Color.RED, false);
    }

    private static String getColorized(final String pText, final Color pColor, final boolean pBold) {
        if (enabled) {
            Ansi ansi = ansi();
            if (pBold) {
                ansi = ansi.bold();
            }
            return ansi.fg(pColor).a(pText).reset().toString();

        } else {
            return pText;
        }
    }

    public static void enable(boolean enabled) {
        PrintColor.enabled = enabled;
    }
}
