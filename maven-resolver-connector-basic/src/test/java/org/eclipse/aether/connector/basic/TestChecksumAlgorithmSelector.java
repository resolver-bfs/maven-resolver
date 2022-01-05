package org.eclipse.aether.connector.basic;

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
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithm;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;

/**
 * Test implementation of {@link ChecksumAlgorithmFactorySelector}.
 */
public class TestChecksumAlgorithmSelector
        implements ChecksumAlgorithmFactorySelector
{
    public static final String SHA512 = "SHA-512";

    public static final String SHA256 = "SHA-256";

    public static final String SHA1 = "SHA-1";

    public static final String MD5 = "MD5";

    public static final String TEST_CHECKSUM = "test";

    public static final byte[] TEST_CHECKSUM_VALUE = new byte[] {0x01, 0x02, 0x03, 0x04};

    @Override
    public Set<String> knownAlgorithms()
    {
        return Collections.emptySet(); // irrelevant
    }

    @Override
    public Map<String, String> calculate( InputStream inputStream, List<ChecksumAlgorithmFactory> factories )
            throws IOException
    {
        throw new RuntimeException( "not implemented" );
    }

    @Override
    public ChecksumAlgorithmFactory select( final String algorithm )
    {
        if ( TEST_CHECKSUM.equals( algorithm ) )
        {
            return new ChecksumAlgorithmFactory( TEST_CHECKSUM, "test" )
            {
                @Override
                public ChecksumAlgorithm getAlgorithm()
                {
                    return new ChecksumAlgorithm()
                    {
                        @Override
                        public void update( final ByteBuffer input )
                        {

                        }

                        @Override
                        public byte[] checksum()
                        {
                            return TEST_CHECKSUM_VALUE;
                        }
                    };
                }
            };
        }
        return new MessageDigestChecksumAlgorithmFactory( algorithm );
    }

    private static class MessageDigestChecksumAlgorithmFactory
            extends ChecksumAlgorithmFactory
    {
        public MessageDigestChecksumAlgorithmFactory( String name )
        {
            super( name, name.replace( "-", "" ).toLowerCase( Locale.ENGLISH ) );
            // this call prevents component instantiation in case of unsupported algorithm, fail fast
            try
            {
                MessageDigest.getInstance( getName() );
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new IllegalStateException( "Unsupported checksum type: " + name, e );
            }
        }

        @Override
        public ChecksumAlgorithm getAlgorithm()
        {
            try
            {
                MessageDigest messageDigest = MessageDigest.getInstance( getName() );
                return new ChecksumAlgorithm()
                {
                    @Override
                    public void update( final ByteBuffer input )
                    {
                        messageDigest.update( input );
                    }

                    @Override
                    public byte[] checksum()
                    {
                        return messageDigest.digest();
                    }
                };
            }
            catch ( NoSuchAlgorithmException e )
            {
                throw new IllegalStateException( getName() + " MessageDigest not supported, is required by resolver.",
                        e );
            }
        }
    }
}
