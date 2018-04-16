/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server;

import java.lang.instrument.Instrumentation;

/**
 * @author geNAZt
 * @version 1.0
 */
public class SelfInstrumentation {

    private static Instrumentation instrumentation;

    public static void premain( String args, Instrumentation inst ) {
        System.out.println( "Attached pre-main" );
        instrumentation = inst;
    }

    public static long getObjectSize( Object o ) {
        if ( instrumentation == null ) {
            return -1;
        }

        return instrumentation.getObjectSize( o );
    }

}
