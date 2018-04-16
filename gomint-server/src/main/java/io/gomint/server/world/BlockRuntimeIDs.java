/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockRuntimeIDs {

    private static final Logger LOGGER = LoggerFactory.getLogger( BlockRuntimeIDs.class );
    private static final Long2IntMap RUNTIME_IDS = new Long2IntOpenHashMap(); // HashMaps are fine for multithreaded reading

    static {
        // Set cleary invalid default value
        RUNTIME_IDS.defaultReturnValue( -1 );

        // Get the correct resource
        InputStream inputStream = BlockRuntimeIDs.class.getResourceAsStream( "/temp_runtimeids.json" );
        if ( inputStream == null ) {
            try {
                inputStream = new FileInputStream( "gomint-server/src/main/resources/temp_runtimeids.json" );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
        }

        JSONParser parser = new JSONParser();

        try ( InputStreamReader reader = new InputStreamReader( inputStream ) ) {
            JSONArray runtimeIDs = (JSONArray) parser.parse( reader );
            for ( Object id : runtimeIDs ) {
                JSONObject idObj = (JSONObject) id;
                long blockId = (Long) idObj.get( "id" ) << 32 | ( ( (Long) idObj.get( "data" ) ).intValue() & 0xffffffffL );
                RUNTIME_IDS.put( blockId, ( (Long) idObj.get( "runtimeID" ) ).intValue() );
            }
        } catch ( IOException | ParseException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get the correct runtime id for the client. This may also result in blocks being 0'ed due to invalid blocks.
     *
     * @param blockId   which should be converted
     * @param dataValue which should be converted
     * @return runtime id or 0
     */
    public static Integer fromLegacy( int blockId, byte dataValue ) {
        // The nukkit/PC data corruption seems to go further, now single sandstone blocks where found corrupted
        // I simply assume that every block could be corrupted and try to use data value 0 as fallback when the
        // original combination could not be found
        long hashId = ( (long) blockId ) << 32 | ( dataValue & 0xffffffffL );

        int runtimeId = RUNTIME_IDS.get( hashId );
        if ( runtimeId == RUNTIME_IDS.defaultReturnValue() ) {
            hashId = ( (long) blockId ) << 32;
            runtimeId = RUNTIME_IDS.get( hashId );
            if ( runtimeId == RUNTIME_IDS.defaultReturnValue() ) {
                LOGGER.warn( "Unknown blockId and dataValue combination: {}:{}. Be sure your worlds are not corrupted!", blockId, dataValue );
                return 0;
            }
        }

        return runtimeId;
    }

}
