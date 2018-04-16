/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.enchant;

import io.gomint.server.GoMintServer;
import io.gomint.server.enchant.generator.EnchantmentGenerator;
import io.gomint.server.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Enchantments {

    private static final Logger LOGGER = LoggerFactory.getLogger( Enchantments.class );
    private final Registry<EnchantmentGenerator> generators;

    public Enchantments( GoMintServer server ) {
        this.generators =  new Registry<>( server, clazz -> {
            try {
                return (EnchantmentGenerator)ClassLoader.getSystemClassLoader().loadClass( "io.gomint.server.enchant.generator." + clazz.getSimpleName() + "Generator" ).newInstance();
            } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e1 ) {
                LOGGER.error( "Could not use pre generated generator: ", e1 );
            }

            return null;
        } );

        this.generators.register( "io.gomint.server.enchant" );
    }

    /**
     * Create enchantment
     *
     * @param id  of the enchantment
     * @param lvl of the enchantment
     * @return new enchantment instance which contains level data
     */
    public Enchantment create( short id, short lvl ) {
        EnchantmentGenerator enchantmentGenerator = this.generators.getGenerator( id );
        if ( enchantmentGenerator == null ) {
            LOGGER.warn( "Unknown enchant {}", id );
            return null;
        }

        return enchantmentGenerator.generate( lvl );
    }

    public short getId( Class<? extends io.gomint.enchant.Enchantment> clazz ) {
        return (short) this.generators.getId( clazz );
    }

}
