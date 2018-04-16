/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.potion;

import io.gomint.server.GoMintServer;
import io.gomint.server.entity.potion.effect.Effect;
import io.gomint.server.entity.potion.generator.EffectGenerator;
import io.gomint.server.registry.GeneratorCallback;
import io.gomint.server.registry.Registry;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Effects {

    private final Registry<EffectGenerator> generators;

    public Effects( GoMintServer server ) {
        this.generators = new Registry<>( server, new GeneratorCallback<EffectGenerator>() {
            @Override
            public EffectGenerator generate( Class<?> clazz ) {
                try {
                    // Use the same code source as the Gomint JAR
                    return (EffectGenerator) ClassLoader.getSystemClassLoader().loadClass( "io.gomint.server.entity.potion.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
                } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                    e.printStackTrace();
                }

                return null;
            }
        } );

        this.generators.register( "io.gomint.server.entity.potion.effect" );
    }

    public Effect generate( int id, int amplifier, long lengthInMS ) {
        EffectGenerator instance = this.generators.getGenerator( id );
        if ( instance != null ) {
            return instance.generate( amplifier, lengthInMS );
        }

        return null;
    }

}
