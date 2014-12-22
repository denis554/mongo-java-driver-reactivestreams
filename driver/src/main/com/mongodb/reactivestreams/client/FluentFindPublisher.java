/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
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
 */

package com.mongodb.reactivestreams.client;

import com.mongodb.CursorType;

import java.util.concurrent.TimeUnit;

/**
 * Fluent interface for find.
 *
 * @param <T> The type of the result.
 */
public interface FluentFindPublisher<T> extends MongoPublisher<T> {

    /**
     * Helper to return a publisher limited first from the query.
     *
     * @return a MongoPublisher which will
     */
    MongoPublisher<T> first();

    /**
     * Sets the query filter to apply to the query.
     *
     * @param filter the filter, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/db.collection.find/ Filter
     */
    FluentFindPublisher<T> filter(Object filter);

    /**
     * Sets the limit to apply.
     *
     * @param limit the limit, which may be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.limit/#cursor.limit Limit
     */
    FluentFindPublisher<T> limit(int limit);

    /**
     * Sets the number of documents to skip.
     *
     * @param skip the number of documents to skip
     * @return this
     * @mongodb.driver.manual reference/method/cursor.skip/#cursor.skip Skip
     */
    FluentFindPublisher<T> skip(int skip);

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param maxTime  the max time
     * @param timeUnit the time unit, which may not be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.maxTimeMS/#cursor.maxTimeMS Max Time
     */
    FluentFindPublisher<T> maxTime(long maxTime, TimeUnit timeUnit);

    /**
     * Sets the number of documents to return per batch.
     *
     * @param batchSize the batch size
     * @return this
     * @mongodb.driver.manual reference/method/cursor.batchSize/#cursor.batchSize Batch Size
     */
    FluentFindPublisher<T> batchSize(int batchSize);

    /**
     * Sets the query modifiers to apply to this operation.
     *
     * @param modifiers the query modifiers to apply, which may be null.
     * @return this
     * @mongodb.driver.manual reference/operator/query-modifier/ Query Modifiers
     */
    FluentFindPublisher<T> modifiers(Object modifiers);

    /**
     * Sets a document describing the fields to return for all matching documents.
     *
     * @param projection the project document, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/db.collection.find/ Projection
     */
    FluentFindPublisher<T> projection(Object projection);

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @param sort the sort criteria, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/cursor.sort/ Sort
     */
    FluentFindPublisher<T> sort(Object sort);

    /**
     * The server normally times out idle cursors after an inactivity period (10 minutes)
     * to prevent excess memory use. Set this option to prevent that.
     *
     * @param noCursorTimeout true if cursor timeout is disabled
     * @return this
     */
    FluentFindPublisher<T> noCursorTimeout(boolean noCursorTimeout);

    /**
     * Users should not set this under normal circumstances.
     *
     * @param oplogReplay if oplog replay is enabled
     * @return this
     */
    FluentFindPublisher<T> oplogReplay(boolean oplogReplay);

    /**
     * Get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing an error).
     *
     * @param partial if partial results for sharded clusters is enabled
     * @return this
     */
    FluentFindPublisher<T> partial(boolean partial);

    /**
     * Sets the cursor type.
     *
     * @param cursorType the cursor type
     * @return this
     */
    FluentFindPublisher<T> cursorType(CursorType cursorType);

}
