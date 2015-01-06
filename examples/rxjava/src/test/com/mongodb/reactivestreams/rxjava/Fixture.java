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

package com.mongodb.reactivestreams.rxjava;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoNamespace;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import rx.RxReactiveStreams;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Helper class for asynchronous tests.
 */
public final class Fixture {
    public static final String DEFAULT_URI = "mongodb://localhost:27017";
    public static final String MONGODB_URI_SYSTEM_PROPERTY_NAME = "org.mongodb.test.uri";
    private static final String DEFAULT_DATABASE_NAME = "JavaDriverReactiveTest";

    private static ConnectionString connectionString;
    private static MongoClient mongoClient;

    private Fixture() {
    }

    public static synchronized MongoClient getMongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(getConnectionString());
            Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        }
        return mongoClient;
    }

    public static synchronized ConnectionString getConnectionString() {
        if (connectionString == null) {
            String mongoURIProperty = System.getProperty(MONGODB_URI_SYSTEM_PROPERTY_NAME);
            String mongoURIString = mongoURIProperty == null || mongoURIProperty.isEmpty()
                    ? DEFAULT_URI : mongoURIProperty;
            connectionString = new ConnectionString(mongoURIString);
        }
        return connectionString;
    }

    public static String getDefaultDatabaseName() {
        return DEFAULT_DATABASE_NAME;
    }

    public static MongoDatabase getDefaultDatabase() {
        return getMongoClient().getDatabase(getDefaultDatabaseName());
    }

    public static MongoCollection<Document> initializeCollection(final MongoNamespace namespace) throws Throwable {
        MongoDatabase database = getMongoClient().getDatabase(namespace.getDatabaseName());
        try {
            RxReactiveStreams.toObservable(database.executeCommand(new Document("drop", namespace.getCollectionName())))
                    .timeout(10, SECONDS).toBlocking().toIterable();
        } catch (MongoCommandException e) {
            if (!e.getErrorMessage().startsWith("ns not found")) {
                throw e;
            }
        }
        return database.getCollection(namespace.getCollectionName());
    }

    public static void dropDatabase(final String name) throws Throwable {
        if (name == null) {
            return;
        }
        try {
            RxReactiveStreams.toObservable(getMongoClient().getDatabase(name).executeCommand(new Document("dropDatabase", 1)))
                    .timeout(10, SECONDS).toBlocking().toIterable();
        } catch (MongoCommandException e) {
            if (!e.getErrorMessage().startsWith("ns not found")) {
                throw e;
            }
        }
    }

    public static void drop(final MongoNamespace namespace) throws Throwable {
        try {
            RxReactiveStreams.toObservable(getMongoClient().getDatabase(namespace.getDatabaseName())
                    .executeCommand(new Document("drop", namespace.getCollectionName()))).timeout(10, SECONDS).toBlocking().toIterable();
        } catch (MongoCommandException e) {
            if (!e.getErrorMessage().contains("ns not found")) {
                throw e;
            }
        }
    }

    static class ShutdownHook extends Thread {
        @Override
        public void run() {
            try {
                dropDatabase(getDefaultDatabaseName());
            } catch (Throwable e) {
                // ignore
            }
            mongoClient.close();
            mongoClient = null;
        }
    }

}
