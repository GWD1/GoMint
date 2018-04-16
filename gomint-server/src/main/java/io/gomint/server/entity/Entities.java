/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity;

import io.gomint.entity.Entity;
import io.gomint.server.GoMintServer;
import io.gomint.server.entity.generator.EntityGenerator;
import io.gomint.server.registry.GeneratorCallback;
import io.gomint.server.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Entities {

    private static final Logger LOGGER = LoggerFactory.getLogger( Entities.class );
    private final Registry<EntityGenerator> generators;

    public Entities( GoMintServer server ) {
        this.generators = new Registry<>( server, new GeneratorCallback<EntityGenerator>() {
            @Override
            public EntityGenerator generate( Class<?> clazz ) {
                try {
                    return (EntityGenerator) ClassLoader.getSystemClassLoader().loadClass( "io.gomint.server.entity.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
                } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                    e.printStackTrace();
                }

                return null;
            }
        } );

        this.generators.register( "io.gomint.server.entity" );
        this.generators.register( "io.gomint.server.entity.active" );
        this.generators.register( "io.gomint.server.entity.monster" );
        this.generators.register( "io.gomint.server.entity.passive" );
        this.generators.register( "io.gomint.server.entity.projectile" );
    }

    public <T extends Entity> T create( Class<T> entityClass ) {
        EntityGenerator entityGenerator = this.generators.getGenerator( entityClass );
        if ( entityGenerator == null ) {
            return null;
        }

        return entityGenerator.generate();
    }

    public <T extends Entity> T create( int entityId ) {
        EntityGenerator entityGenerator = this.generators.getGenerator( entityId );
        if ( entityGenerator == null ) {
            LOGGER.warn( "Could not find entity generator for id {}", entityId );
            return null;
        }

        return entityGenerator.generate();
    }

}
