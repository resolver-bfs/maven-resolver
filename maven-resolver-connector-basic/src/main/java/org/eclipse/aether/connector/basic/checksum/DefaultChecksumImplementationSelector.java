package org.eclipse.aether.connector.basic.checksum;

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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.aether.connector.basic.ChecksumImplementation;
import org.eclipse.aether.connector.basic.ChecksumImplementationSelector;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link ChecksumImplementationSelector}.
 */
@Singleton
@Named
public class DefaultChecksumImplementationSelector
    implements ChecksumImplementationSelector
{
    private final Map<String, Provider<ChecksumImplementation>> providers;

    @Inject
    public DefaultChecksumImplementationSelector( final Map<String, Provider<ChecksumImplementation>> providers )
    {
        this.providers = requireNonNull( providers );
    }

    @Override
    public ChecksumImplementation select( final String algorithm ) throws NoSuchAlgorithmException
    {
        requireNonNull( algorithm, "algorithm name must not be null" );
        Provider<ChecksumImplementation> provider = providers.get( algorithm.toUpperCase( Locale.ENGLISH ) );
        if ( provider != null )
        {
            return provider.get();
        }

        // fallback to MessageDigest backed one
        MessageDigest messageDigest = MessageDigest.getInstance( algorithm );
        return new ChecksumImplementation()
        {
            @Override
            public void update( final ByteBuffer input )
            {
                messageDigest.update( input );
            }

            @Override
            public void reset()
            {
                messageDigest.reset();
            }

            @Override
            public byte[] digest()
            {
                return messageDigest.digest();
            }
        };
    }
}
