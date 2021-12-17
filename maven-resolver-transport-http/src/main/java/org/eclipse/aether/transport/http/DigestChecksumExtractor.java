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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;

/**
 * A component extracting Digest header value from response.
 * This class is EXPERIMENTAL and INCOMPLETE.
 *
 * @since TBD
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-digest-headers-07">Digest Fields (DRAFT)</a>
 */
@Singleton
@Named( "digest" )
public class DigestChecksumExtractor
        extends ChecksumExtractor
{
    static final String HEADER_WANT_DIGEST = "Want-Digest";

    static final String HEADER_DIGEST = "Digest";

    @Override
    public void prepareRequest( HttpUriRequest request )
    {
        if ( request instanceof HttpGet )
        {
            request.addHeader( HEADER_WANT_DIGEST, "sha;q=0.5, md5;q=0.1" );
        }
    }

    @Override
    public Map<String, String> extractChecksums( HttpResponse response )
    {
        // values: comma separates list of <key>=<value>
        Header header = response.getFirstHeader( HEADER_DIGEST );
        String digest = header != null ? header.getValue() : null;
        if ( digest != null )
        {
            String[] elements = digest.split( ",", -1 );
            for ( String element : elements )
            {
                if ( element != null && element.indexOf( '=' ) > 0 )
                {
                    if ( element.startsWith( "sha" ) )
                    {
                        return Collections.singletonMap( "SHA-1", element.substring( element.indexOf( '=' ) + 1 ) );
                    }
                    if ( element.startsWith( "md5" ) )
                    {
                        return Collections.singletonMap( "MD5", element.substring( element.indexOf( '=' ) + 1 ) );
                    }
                }
            }
        }
        return null;
    }
}
