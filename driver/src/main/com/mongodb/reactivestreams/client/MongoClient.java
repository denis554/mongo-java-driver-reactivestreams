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

import com.mongodb.annotations.Immutable;
import com.mongodb.client.options.OperationOptions;
import org.reactivestreams.Publisher;

import java.io.Closeable;

/**
 * A client-side representation of a MongoDB cluster.  Instances can represent either a standalone MongoDB instance, a replica set,
 * or a sharded cluster.  Instance of this class are responsible for maintaining an up-to-date state of the cluster,
 * and possibly cache resources related to this, including background threads for monitoring, and connection pools.
 * <p>
 * Instance of this class server as factories for {@code MongoDatabase} instances.
 * </p>
 */
@Immutable
public interface MongoClient extends Closeable {
    /**
     * Gets the database with the given name.
     *
     * @param name the name of the database
     * @return the database
     */
    MongoDatabase getDatabase(String name);

    /**
     * Gets the database with the given name.
     *
     * @param name                 the name of the database
     * @param options the database options
     * @return the database
     */
    MongoDatabase getDatabase(String name, OperationOptions options);

    /**
     * Close the client, which will close all underlying cached resources, including, for example,
     * sockets and background monitoring threads.
     */
    void close();

    /**
     * Gets the options that this client uses to connect to server.
     *
     * <p>Note: {@link MongoClientOptions} is immutable.</p>
     *
     * @return the options
     */
    MongoClientOptions getOptions();

    /**
     * Get a list of the database names
     *
     * @return a publisher emitting the sequence of database names
     * @mongodb.driver.manual reference/commands/listDatabases List Databases
     */
    Publisher<String> getDatabaseNames();

}
