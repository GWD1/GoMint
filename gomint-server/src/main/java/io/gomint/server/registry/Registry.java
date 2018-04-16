package io.gomint.server.registry;

import com.google.common.reflect.ClassPath;
import io.gomint.server.GoMintServer;
import io.gomint.server.util.collection.FixedIndexResizableArray;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Registry<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger( Registry.class );

    private final GeneratorCallback<R> generatorCallback;
    private final FixedIndexResizableArray<R> generators = new FixedIndexResizableArray<>();
    private final Object2IntMap<Class<?>> apiReferences = new Object2IntOpenHashMap<>();
    private final GoMintServer server;

    /**
     * Build a new generator registry
     *
     * @param server   which started this gomint
     * @param callback which is used to generate a generator for each found element
     */
    public Registry( GoMintServer server, GeneratorCallback<R> callback ) {
        this.server = server;
        this.generatorCallback = callback;
    }

    /**
     * Register all classes which can be found in given path
     *
     * @param classPath which should be searched
     */
    public void register( String classPath ) {
        LOGGER.debug( "Going to scan: {}", classPath );

        for ( ClassPath.ClassInfo classInfo : this.server.getClassPath().getTopLevelClasses( classPath ) ) {
            register( classInfo.load() );
        }
    }

    private void register( Class<?> clazz ) {
        // We need register info
        if ( !clazz.isAnnotationPresent( RegisterInfo.class ) && !clazz.isAnnotationPresent( RegisterInfos.class ) ) {
            LOGGER.debug( "No register info annotation present" );
            return;
        }

        if ( clazz.isAnnotationPresent( RegisterInfo.class ) ) {
            int id = clazz.getAnnotation( RegisterInfo.class ).id();
            R generator = this.generatorCallback.generate( clazz );
            if ( generator != null ) {
                R oldGen = this.generators.set( id, generator );
                if ( oldGen != null ) {
                    LOGGER.warn( "Duplicated register info for id: {} -> {}; old: {}", id, clazz.getName(), oldGen.getClass().getName() );
                }

                // Check for API interfaces
                for ( Class<?> apiInter : clazz.getInterfaces() ) {
                    this.apiReferences.put( apiInter, id );
                }

                this.apiReferences.put( clazz, id );
            }
        } else {
            R generator = this.generatorCallback.generate( clazz );
            if ( generator != null ) {
                RegisterInfo[] infos = clazz.getAnnotation( RegisterInfos.class ).value();
                for ( RegisterInfo info : infos ) {
                    int id = info.id();

                    R oldGen = this.generators.set( id, generator );
                    if ( oldGen != null ) {
                        LOGGER.warn( "Duplicated register info for id: {} -> {}; old: {}", id, clazz.getName(), oldGen.getClass().getName() );
                    }

                    // Check for API interfaces
                    for ( Class<?> apiInter : clazz.getInterfaces() ) {
                        this.apiReferences.put( apiInter, id );
                    }

                    this.apiReferences.put( clazz, id );
                }
            }
        }
    }

    public R getGenerator( Class<?> clazz ) {
        // Get the internal ID
        int id = apiReferences.getOrDefault( clazz, -1 );
        if ( id == -1 ) {
            return null;
        }

        return getGenerator( id );
    }

    public R getGenerator( int id ) {
        return generators.get( id );
    }

    public int getId( Class<?> clazz ) {
        return apiReferences.getOrDefault( clazz, -1 );
    }

}
