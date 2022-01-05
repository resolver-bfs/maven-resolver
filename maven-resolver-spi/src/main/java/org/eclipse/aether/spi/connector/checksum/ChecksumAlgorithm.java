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

import java.nio.ByteBuffer;

/**
 * Implementation performing checksum calculation for specific algorithm. Instances of this interface are stateful,
 * should not be reused.
 *
 * @since TBD
 */
public interface ChecksumAlgorithm
{
    /**
     * Updates the checksum algorithm inner state with input.
     */
    void update( ByteBuffer input );

    /**
     * Returns the algorithm end result as byte array. After invoking this method, this instance should be
     * discarded and not used anymore.
     */
    byte[] checksum();
}
