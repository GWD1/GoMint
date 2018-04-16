/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.potion.generator;

/**
 * @author generated
 * @version 2.0
 */
public class SlownessGenerator implements EffectGenerator {

    public io.gomint.server.entity.potion.effect.Effect generate( int amplifier, long lengthInMS ) {
        return new io.gomint.server.entity.potion.effect.Slowness( amplifier, lengthInMS );
    }

}