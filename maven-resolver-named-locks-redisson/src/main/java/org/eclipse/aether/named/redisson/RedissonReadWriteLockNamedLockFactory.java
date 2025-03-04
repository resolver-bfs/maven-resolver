package org.eclipse.aether.named.redisson;

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

import org.eclipse.aether.named.support.ReadWriteLockNamedLock;
import org.redisson.api.RReadWriteLock;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provider of {@link RedissonReadWriteLockNamedLockFactory} using Redisson and {@link org.redisson.api.RReadWriteLock}.
 */
@Singleton
@Named( RedissonReadWriteLockNamedLockFactory.NAME )
public class RedissonReadWriteLockNamedLockFactory
    extends RedissonNamedLockFactorySupport
{
    public static final String NAME = "rwlock-redisson";

    private static final String TYPED_NAME_PREFIX = NAME_PREFIX + NAME + ":";

    @Override
    protected ReadWriteLockNamedLock createLock( final String name )
    {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock( TYPED_NAME_PREFIX + name );
        return new ReadWriteLockNamedLock( name, this, readWriteLock );
    }
}
