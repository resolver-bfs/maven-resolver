package org.eclipse.aether.spi.connector;

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

import java.util.Map;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Component able to provide (expected) checksums beforehand the download happens.
 *
 * @since TBD
 */
public interface DownloadChecksumSource
{
    /**
     * May return the "expected" checksums for given artifact, from source other than remote repository.
     *
     * @param remoteRepository The remote repository artifact is about to be resolved.
     * @param artifact The artifact about to be resolved.
     * @return Map of "expeced" checksums, or {@code null}.
     */
      Map<String, String> getProvidedChecksums( RepositorySystemSession session,
                                                RemoteRepository remoteRepository,
                                                Artifact artifact );
}
