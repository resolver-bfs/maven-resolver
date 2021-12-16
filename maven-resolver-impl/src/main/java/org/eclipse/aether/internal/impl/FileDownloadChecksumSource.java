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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.DownloadChecksumSource;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.util.ChecksumUtils;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * {@link DownloadChecksumSource} that does use specified directory structure to look up checksums.
 *
 * @since TBD
 */
@Singleton
@Named( FileDownloadChecksumSource.NAME )
public final class FileDownloadChecksumSource
    implements DownloadChecksumSource
{
    public static final String NAME = "file";

    private static final String CONFIG_PROP_BASE_DIR = "aether.artifactResolver.downloadChecksumSource.file.baseDir";

    private static final Logger LOGGER = LoggerFactory.getLogger( FileDownloadChecksumSource.class );

    private final RepositoryLayoutProvider repositoryLayoutProvider;

    @Inject
    public FileDownloadChecksumSource( RepositoryLayoutProvider repositoryLayoutProvider )
    {
        this.repositoryLayoutProvider = requireNonNull( repositoryLayoutProvider );
    }

    @Override
    public Map<String, String> getProvidedChecksums( RepositorySystemSession session,
                                                     RemoteRepository remoteRepository,
                                                     Artifact artifact )
    {
        final String baseDirPath = ConfigUtils.getString( session, null, CONFIG_PROP_BASE_DIR );
        final Path baseDir;
        if ( baseDirPath != null )
        {
            baseDir = Paths.get( baseDirPath );
        }
        else
        {
            baseDir = session.getLocalRepository().getBasedir().toPath().resolve( ".checksums" );
        }

        if ( !Files.isDirectory( baseDir ) )
        {
            return null;
        }

        try
        {
            RepositoryLayout repositoryLayout = repositoryLayoutProvider
                .newRepositoryLayout( session, remoteRepository );

            HashMap<String, String> checksums = new HashMap<>();
            for ( String algorithm : repositoryLayout.getChecksumAlgorithms() )
            {
                Path checksumPath = baseDir.resolve( getPathForArtifactChecksum( artifact, algorithm ) );
                if ( Files.isReadable( checksumPath ) )
                {
                    try
                    {
                        String checksum = ChecksumUtils.read( checksumPath.toFile() );
                        if ( checksum != null )
                        {
                            LOGGER.debug( "{} -> {}:{}", ArtifactIdUtils.toId( artifact ), algorithm, checksum );
                            checksums.put( algorithm, checksum );
                        }
                    }
                    catch ( IOException e )
                    {
                        // skip it TODO: silently?
                    }
                }
            }
            return checksums.isEmpty() ? null : checksums;
        }
        catch ( NoRepositoryLayoutException e )
        {
            // fall thru TODO: silently?
            return null;
        }
    }

    private String getPathForArtifactChecksum( Artifact artifact, String algorithm )
    {
        StringBuilder path = new StringBuilder()
            .append( artifact.getGroupId().replace( '.', '/' ) ).append( '/' )
            .append( artifact.getArtifactId() ).append( '/' )
            .append( artifact.getBaseVersion() ).append( '/' )
            .append( artifact.getArtifactId() ).append( '-' )
            .append( artifact.getVersion() );
        if ( artifact.getClassifier().length() > 0 )
        {
            path.append( '-' ).append( artifact.getClassifier() );
        }
        if ( artifact.getExtension().length() > 0 )
        {
            path.append( '.' ).append( artifact.getExtension() );
        }
        path.append( '.' ).append( algorithm.replace( "-", "" ).toLowerCase( Locale.ENGLISH ) );
        return path.toString();
    }

}
