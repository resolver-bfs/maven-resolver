package org.eclipse.aether.internal.impl.checksum;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithm;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;
import org.eclipse.aether.util.ChecksumUtils;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation.
 *
 * @since TBD
 */
@Singleton
@Named
public class DefaultChecksumAlgorithmFactorySelector
        implements ChecksumAlgorithmFactorySelector
{
    private final Map<String, ChecksumAlgorithmFactory> factories;

    /**
     * Default ctor for SL.
     */
    @Deprecated
    public DefaultChecksumAlgorithmFactorySelector()
    {
        this.factories = new HashMap<>();
        this.factories.put( ChecksumAlgorithmFactorySHA1.NAME, new ChecksumAlgorithmFactorySHA1() );
        this.factories.put( ChecksumAlgorithmFactoryMD5.NAME, new ChecksumAlgorithmFactoryMD5() );
    }

    @Inject
    public DefaultChecksumAlgorithmFactorySelector( Map<String, ChecksumAlgorithmFactory> factories )
    {
        this.factories = requireNonNull( factories );
    }

    @Override
    public ChecksumAlgorithmFactory select( String algorithmName )
    {
        return factories.get( algorithmName );
    }

    @Override
    public Set<String> knownAlgorithms()
    {
        return new HashSet<>( factories.keySet() );
    }

    @Override
    public Map<String, String> calculate( InputStream inputStream, List<ChecksumAlgorithmFactory> factories )
            throws IOException
    {
        LinkedHashMap<String, ChecksumAlgorithm> algorithms = new LinkedHashMap<>();
        factories.forEach( f -> algorithms.put( f.getName(), f.getAlgorithm() ) );
        try ( InputStream in = inputStream )
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate( 32 * 1024 );
            byte[] buffer = byteBuffer.array();
            for ( ; ; )
            {
                int read = in.read( buffer );
                if ( read < 0 )
                {
                    break;
                }
                ( (Buffer) byteBuffer ).rewind();
                ( (Buffer) byteBuffer ).limit( read );
                ( (Buffer) byteBuffer ).mark();
                for ( ChecksumAlgorithm checksumAlgorithm : algorithms.values() )
                {
                    checksumAlgorithm.update( byteBuffer );
                    ( (Buffer) byteBuffer ).reset();
                }
            }
        }
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        algorithms.forEach( ( k, v ) -> result.put( k, ChecksumUtils.toHexString( v.checksum() ) ) );
        return result;
    }
}
