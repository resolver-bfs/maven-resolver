# Smart Checksum Strategies
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

By default, Resolver will fetch the payload checksum from remote repository. These
checksums are used to enforce transport validity (that download is not corrupted).

This implies, that to get one artifact, resolver needs to issue two HTTP reuest:
one to get the artifact itself, and one to get the checksum.

By using "smart checksums" feature, we are able to half the issued HTTP request 
count, as many services and Maven Central emit the reference checksums in
the artifact response itself, as HTTP Headers, so we are able to get the
artifact and it's checksum in only one request.


## Sonatype Nexus2

Sonatype Nexus2 uses SHA-1 hash to generate `ETag` header in "shielded" (a la Plexus Cipher)
way. Naturally, this means only SHA-1 is available in artifact response header.

Emitted by: Sonatype Nexus2 only.

## Non-standard headers

Some MRMs and Maven Central emit headers `x-checksum-sha1` and `x-checksum-md5`. Resolver
will detect these and use their value.

Emitted by: Maven Central and Artifactory.

## Digest Headers (draft)

**Warning: Thie strategy is EXPERIMENTAL and INCOMPLETE**

As experiment, support for [draft-ietf-httpbis-digest-headers-07](https://www.ietf.org/archive/id/draft-ietf-httpbis-digest-headers-07.html)
has been added. Untested, unfinished.

Emitted by: ?


