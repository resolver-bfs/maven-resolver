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

/**
 * A component representing a checksum factory: provides {@link ChecksumAlgorithm} instances and some helpers.
 * Implementors should ensure that factory cannot be instantiated, if required conditions are not met.
 *
 * @since TBD
 */
public interface ChecksumAlgorithmFactory
{
    /**
     * Returns the algorithm name, usually used as key, never {@code null} value.
     */
    String getName();

    /**
     * Returns the file extension to be used for given checksum algorithm (without leading dot), never {@code null}.
     */
    String getExtension();

    /**
     * Returns a new instance of algorithm, never {@code null} value.
     */
    ChecksumAlgorithm getAlgorithm();
}
