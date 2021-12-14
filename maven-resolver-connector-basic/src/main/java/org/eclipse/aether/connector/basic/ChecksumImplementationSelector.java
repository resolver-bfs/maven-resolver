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

import java.security.NoSuchAlgorithmException;

/**
 * Selector that selects implemntation based on passed in algorithm name.
 *
 * @since TBD
 */
public interface ChecksumImplementationSelector
{
  /**
   * Returns a new instance ready to perform checksum calculation for given algorithm, never {@code null}.
   *
   * @throws NoSuchAlgorithmException if algorithm asked for is not supported.
   */
  ChecksumImplementation select( String algorithm ) throws NoSuchAlgorithmException;
}
