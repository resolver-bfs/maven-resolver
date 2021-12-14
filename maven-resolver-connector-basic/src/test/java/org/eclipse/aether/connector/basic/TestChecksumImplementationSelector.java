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

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import org.eclipse.aether.spi.connector.checksum.ChecksumImplementation;
import org.eclipse.aether.spi.connector.checksum.ChecksumImplementationSelector;
import org.eclipse.aether.spi.connector.checksum.ChecksumImplementationSelectorSupport;

/**
 * Test implementation of {@link ChecksumImplementationSelector}.
 */
public class TestChecksumImplementationSelector
    extends ChecksumImplementationSelectorSupport
{
  public static final String TEST_CHECKSUM = "test";

  public static final byte[] TEST_CHECKSUM_VALUE = new byte[] { 0x1, 0x2, 0x3, 0x4 };

  @Override
  public ChecksumImplementation select(final String algorithm) throws NoSuchAlgorithmException {
    if ( TEST_CHECKSUM.equals( algorithm ) )
    {
      return new ChecksumImplementation() {
        @Override
        public void update(final ByteBuffer input) {

        }

        @Override
        public void reset() {

        }

        @Override
        public byte[] digest() {
          return TEST_CHECKSUM_VALUE;
        }
      };
    }
    return super.select(algorithm);
  }
}
