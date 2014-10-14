#!/bin/sh
# that's shitty
java -cp $(for i in bin/*.jar ; do echo -n $i: ; done)$(for i in lib/*.jar ; do echo -n $i: ; done)$(for i in bonita/*.jar ; do echo -n $i: ; done) org.bonitasoft.engine.BonitaShell