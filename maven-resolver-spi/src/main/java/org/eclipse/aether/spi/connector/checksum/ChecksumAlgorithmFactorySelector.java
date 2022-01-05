package org.eclipse.aether.spi.connector.checksum;

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
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Component performing selection of {@link ChecksumAlgorithmFactory} based on known factory names.
 *
 * @since TBD
 */
public interface ChecksumAlgorithmFactorySelector
{
    /**
     * Returns factory for given algorithm name, or {@code null} if algorithm not known.
     */
    ChecksumAlgorithmFactory select( String algorithmName );

    /**
     * Returns a set of supported algorithms. This set represents ALL the algorithms suppported by Resolver, and is NOT
     * in any relation to given repository {@link org.eclipse.aether.spi.connector.layout.RepositoryLayout}
     * (is super set of it).
     */
    Set<String> knownAlgorithms();

    /**
     * Calculates checksums for specific input stream.
     *
     * @param inputStream The content for which to calculate checksums, must not be {@code null}. The stream is closed
     *                    when method returns.
     * @param factories   The checksum algorithm factories to use, must not be {@code null}.
     * @return The calculated checksums, indexed by algorithm name, or the exception that occurred while trying to
     * calculate it, never {@code null}.
     * @throws IOException If the content input stream consumption failed.
     */
    Map<String, String> calculate( InputStream inputStream, List<ChecksumAlgorithmFactory> factories )
            throws IOException;
}
