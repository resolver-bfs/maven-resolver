package org.eclipse.aether.transport.http;

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

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;

/**
 * A component extracting {@code x-checksum-XXX}` style-checksums from response headers.
 *
 * @since TBD
 */
@Singleton
@Named( "x-checksum" )
public class XChecksumChecksumExtractor
        extends ChecksumExtractor
{
    @Override
    public Map<String, String> extractChecksums( HttpResponse response )
    {
        // Central style: x-checksum-sha1: c74edb60ca2a0b57ef88d9a7da28f591e3d4ce7b
        String sha1 = extractChecksum( response, "sha1" );
        if ( sha1 != null )
        {
            return Collections.singletonMap( "SHA-1", sha1 );
        }
        // Central style: x-checksum-md5: 9ad0d8e3482767c122e85f83567b8ce6
        String md5 = extractChecksum( response, "md5" );
        if ( md5 != null )
        {
            return Collections.singletonMap( "MD5", md5 );
        }

        return null;
    }

    private String extractChecksum( HttpResponse response, String algorithm )
    {
        // Central style: x-checksum-sha1: c74edb60ca2a0b57ef88d9a7da28f591e3d4ce7b
        Header header = response.getFirstHeader( "x-checksum-" + algorithm );
        return header != null ? header.getValue() : null;
    }
}
