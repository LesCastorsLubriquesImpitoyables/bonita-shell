package org.bonitasoft.shell

import spock.lang.Specification

/**
 * @author Baptiste Mesta
 */
public class ShellTest extends Specification {


    def "should parse arguments correctly"() {
        expect:
        result == new Shell().parse(line)
        where:
        line       || result
        "a b"       | ["a", "b"]
        "a \${b a}" | ["a", "\${b a}"]

    }

}
