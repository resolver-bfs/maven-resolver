package org.eclipse.aether.internal.impl;

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

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.aether.spi.connector.checksum.ChecksumImplementation;
import org.eclipse.aether.spi.connector.checksum.ChecksumImplementationSelectorSupport;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link org.eclipse.aether.spi.connector.checksum.ChecksumImplementationSelector} that
 * is extensible.
 *
 * @since TBD
 */
@Singleton
@Named
public final class DefaultChecksumImplementationSelector
    extends ChecksumImplementationSelectorSupport
{
    private final Map<String, Provider<ChecksumImplementation>> providers;

    /**
     * Ctor for ServiceLocator.
     */
    public DefaultChecksumImplementationSelector()
    {
        this( Collections.emptyMap() );
    }

    @Inject
    public DefaultChecksumImplementationSelector( final Map<String, Provider<ChecksumImplementation>> providers )
    {
        this.providers = requireNonNull( providers );
    }

    @Override
    public ChecksumImplementation select( final String algorithm ) throws NoSuchAlgorithmException
    {
        requireNonNull( algorithm, "algorithm must not be null" );
        Provider<ChecksumImplementation> provider = providers.get( algorithm );
        if ( provider != null )
        {
            return provider.get();
        }
        return super.select( algorithm );
    }

}
