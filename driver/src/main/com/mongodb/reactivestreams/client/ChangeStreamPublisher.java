/*
 * Copyright 2017 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mongodb.reactivestreams.client;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.FullDocument;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

/**
 * Iterable for change streams.
 *
 * @param <TResult> The type of the result.
 * @mongodb.server.release 3.6
 * @since 1.6
 */
public interface ChangeStreamPublisher<TResult> extends Publisher<TResult> {
    /**
     * Sets the fullDocument value.
     *
     * @param fullDocument the fullDocument
     * @return this
     */
    ChangeStreamPublisher<TResult> fullDocument(FullDocument fullDocument);

    /**
     * Sets the logical starting point for the new change stream.
     *
     * @param resumeToken the resume token
     * @return this
     */
    ChangeStreamPublisher<TResult> resumeAfter(Bson resumeToken);

    /**
     * Sets the maximum await execution time on the server for this operation.
     *
     * @param maxAwaitTime  the max await time.  A zero value will be ignored, and indicates that the driver should respect the server's
     *                      default value
     * @param timeUnit the time unit, which may not be null
     * @return this
     */
    ChangeStreamPublisher<TResult>  maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit);

    /**
     * Sets the collation options
     *
     * <p>A null value represents the server default.</p>
     * @param collation the collation options to use
     * @return this
     */
    ChangeStreamPublisher<TResult> collation(Collation collation);
}
