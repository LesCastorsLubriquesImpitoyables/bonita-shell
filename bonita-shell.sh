#!/bin/sh
# that's shitty
# -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044
java -cp $(for i in bin/*.jar ; do echo -n $i: ; done)$(for i in lib/*.jar ; do echo -n $i: ; done)$(for i in bonita/*.jar ; do echo -n $i: ; done) org.bonitasoft.shell.BonitaShell config.properties