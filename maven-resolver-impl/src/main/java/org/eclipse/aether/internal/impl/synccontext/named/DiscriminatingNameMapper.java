package org.eclipse.aether.internal.impl.synccontext.named;

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

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ChecksumUtils;
import org.eclipse.aether.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Discriminating {@link NameMapper}, that wraps another {@link NameMapper} and adds a "discriminator" as prefix, that
 * makes lock names unique including the hostname and local repository (by default). The discriminator may be passed
 * in via {@link RepositorySystemSession} or is automatically calculated based on the local hostname and repository
 * path. The implementation retains order of collection elements as it got it from
 * {@link NameMapper#nameLocks(RepositorySystemSession, Collection, Collection)} method.
 * <p>
 * The default setup wraps {@link GAVNameMapper}, but manually may be created any instance needed.
 */
@Singleton
@Named( DiscriminatingNameMapper.NAME )
public class DiscriminatingNameMapper implements NameMapper
{
    public static final String NAME = "discriminating";

    /**
     * Configuration property to pass in discriminator
     */
    private static final String CONFIG_PROP_DISCRIMINATOR = "aether.syncContext.named.discriminating.discriminator";

    /**
     * Configuration property to pass in hostname
     */
    private static final String CONFIG_PROP_HOSTNAME = "aether.syncContext.named.discriminating.hostname";

    private static final String DEFAULT_HOSTNAME = "localhost";

    private static final Logger LOGGER = LoggerFactory.getLogger( DiscriminatingNameMapper.class );

    private final NameMapper nameMapper;

    private final String hostname;

    @Inject
    public DiscriminatingNameMapper( @Named( GAVNameMapper.NAME ) final NameMapper nameMapper )
    {
        this.nameMapper = Objects.requireNonNull( nameMapper );
        this.hostname = getHostname();
    }

    @Override
    public Collection<String> nameLocks( final RepositorySystemSession session,
                                         final Collection<? extends Artifact> artifacts,
                                         final Collection<? extends Metadata> metadatas )
    {
        String discriminator = createDiscriminator( session );
        return nameMapper.nameLocks( session, artifacts, metadatas ).stream().map( s -> discriminator + ":" + s )
                         .collect( toList() );
    }

    private String getHostname()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch ( UnknownHostException e )
        {
            LOGGER.warn( "Failed to get hostname, using '{}'", DEFAULT_HOSTNAME, e );
            return DEFAULT_HOSTNAME;
        }
    }

    private String createDiscriminator( final RepositorySystemSession session )
    {
        String discriminator = ConfigUtils.getString( session, null, CONFIG_PROP_DISCRIMINATOR );

        if ( discriminator == null || discriminator.isEmpty() )
        {
            String hostname = ConfigUtils.getString( session, this.hostname, CONFIG_PROP_HOSTNAME );
            File basedir = session.getLocalRepository().getBasedir();
            discriminator = hostname + ":" + basedir;
            return sha1String( discriminator );
        }
        return discriminator;
    }

    private String sha1String( String string )
    {
        try
        {
            return ChecksumUtils.toHexString(
                    MessageDigest.getInstance( "SHA-1" ).digest( string.getBytes( StandardCharsets.UTF_8 ) )
            );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( "Java must support SHA-1 to run resolver" );
        }
    }
}
