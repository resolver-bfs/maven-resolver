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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Objects.requireNonNull;

/**
 * Support for implementation of {@link ChecksumAlgorithmSelector} with default behaviour.
 */
public abstract class ChecksumAlgorithmSelectorSupport
    implements ChecksumAlgorithmSelector
{
    @Override
    public ChecksumAlgorithm select( final String algorithm ) throws NoSuchAlgorithmException
    {
        requireNonNull( algorithm, "algorithm name must not be null" );
        MessageDigest messageDigest = MessageDigest.getInstance( algorithm );
        return new ChecksumAlgorithm()
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
            public byte[] checksum()
            {
                return messageDigest.digest();
            }
        };
    }
}
