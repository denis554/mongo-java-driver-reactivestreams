/*
 * Copyright 2014-2015 MongoDB, Inc.
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

package com.mongodb.reactivestreams.client.internal;

import com.mongodb.Block;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.CreateIndexOptions;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.DropIndexOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.ChangeStreamPublisher;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.ListIndexesPublisher;
import com.mongodb.reactivestreams.client.MapReducePublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.ClientSession;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.async.client.Observables.observe;
import static com.mongodb.async.client.Observables.observeAndFlatten;
import static com.mongodb.reactivestreams.client.internal.PublisherHelper.voidToSuccessCallback;


final class MongoCollectionImpl<TDocument> implements MongoCollection<TDocument> {

    private final com.mongodb.async.client.MongoCollection<TDocument> wrapped;

    MongoCollectionImpl(final com.mongodb.async.client.MongoCollection<TDocument> wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public MongoNamespace getNamespace() {
        return wrapped.getNamespace();
    }

    @Override
    public Class<TDocument> getDocumentClass() {
        return wrapped.getDocumentClass();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return wrapped.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return wrapped.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return wrapped.getWriteConcern();
    }

    @Override
    public ReadConcern getReadConcern() {
        return wrapped.getReadConcern();
    }

    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(final Class<NewTDocument> clazz) {
        return new MongoCollectionImpl<NewTDocument>(wrapped.withDocumentClass(clazz));
    }

    @Override
    public MongoCollection<TDocument> withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoCollectionImpl<TDocument>(wrapped.withCodecRegistry(codecRegistry));
    }

    @Override
    public MongoCollection<TDocument> withReadPreference(final ReadPreference readPreference) {
        return new MongoCollectionImpl<TDocument>(wrapped.withReadPreference(readPreference));
    }

    @Override
    public MongoCollection<TDocument> withWriteConcern(final WriteConcern writeConcern) {
        return new MongoCollectionImpl<TDocument>(wrapped.withWriteConcern(writeConcern));
    }

    @Override
    public MongoCollection<TDocument> withReadConcern(final ReadConcern readConcern) {
        return new MongoCollectionImpl<TDocument>(wrapped.withReadConcern(readConcern));
    }

    @Override
    public Publisher<Long> count() {
        return count(new BsonDocument(), new CountOptions());
    }

    @Override
    public Publisher<Long> count(final Bson filter) {
        return count(filter, new CountOptions());
    }

    @Override
    public Publisher<Long> count(final Bson filter, final CountOptions options) {
        return new ObservableToPublisher<Long>(observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.count(filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<Long> count(final ClientSession clientSession) {
        return count(clientSession, new BsonDocument(), new CountOptions());
    }

    @Override
    public Publisher<Long> count(final ClientSession clientSession, final Bson filter) {
        return count(clientSession, filter, new CountOptions());
    }

    @Override
    public Publisher<Long> count(final ClientSession clientSession, final Bson filter, final CountOptions options) {
        return new ObservableToPublisher<Long>(observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.count(clientSession.getWrapped(), filter, options, callback);
            }
        }));
    }

    @Override
    public <TResult> DistinctPublisher<TResult> distinct(final String fieldName, final Class<TResult> resultClass) {
        return distinct(fieldName, new BsonDocument(), resultClass);
    }

    @Override
    public <TResult> DistinctPublisher<TResult> distinct(final String fieldName, final Bson filter, final Class<TResult> resultClass) {
        return new DistinctPublisherImpl<TResult>(wrapped.distinct(fieldName, resultClass)).filter(filter);
    }

    @Override
    public <TResult> DistinctPublisher<TResult> distinct(final ClientSession clientSession, final String fieldName,
                                                         final Class<TResult> resultClass) {
        return distinct(clientSession, fieldName, new BsonDocument(), resultClass);
    }

    @Override
    public <TResult> DistinctPublisher<TResult> distinct(final ClientSession clientSession, final String fieldName, final Bson filter, 
                                                         final Class<TResult> resultClass) {
        return new DistinctPublisherImpl<TResult>(wrapped.distinct(clientSession.getWrapped(), fieldName, resultClass)).filter(filter);
    }

    @Override
    public FindPublisher<TDocument> find() {
        return find(new BsonDocument(), getDocumentClass());
    }

    @Override
    public <TResult> FindPublisher<TResult> find(final Class<TResult> clazz) {
        return find(new BsonDocument(), clazz);
    }

    @Override
    public FindPublisher<TDocument> find(final Bson filter) {
        return find(filter, getDocumentClass());
    }

    @Override
    public <TResult> FindPublisher<TResult> find(final Bson filter, final Class<TResult> clazz) {
        return new FindPublisherImpl<TResult>(wrapped.find(filter, clazz));
    }

    @Override
    public FindPublisher<TDocument> find(final ClientSession clientSession) {
        return find(clientSession, new BsonDocument(), getDocumentClass());
    }

    @Override
    public <TResult> FindPublisher<TResult> find(final ClientSession clientSession, final Class<TResult> clazz) {
        return find(clientSession, new BsonDocument(), clazz);
    }

    @Override
    public FindPublisher<TDocument> find(final ClientSession clientSession, final Bson filter) {
        return find(clientSession, filter, getDocumentClass());
    }

    @Override
    public <TResult> FindPublisher<TResult> find(final ClientSession clientSession, final Bson filter, final Class<TResult> clazz) {
        return new FindPublisherImpl<TResult>(wrapped.find(clientSession.getWrapped(), filter, clazz));
    }

    @Override
    public AggregatePublisher<Document> aggregate(final List<? extends Bson> pipeline) {
        return aggregate(pipeline, Document.class);
    }

    @Override
    public <TResult> AggregatePublisher<TResult> aggregate(final List<? extends Bson> pipeline, final Class<TResult> clazz) {
        return new AggregatePublisherImpl<TResult>(wrapped.aggregate(pipeline, clazz));
    }

    @Override
    public AggregatePublisher<Document> aggregate(final ClientSession clientSession, final List<? extends Bson> pipeline) {
        return aggregate(clientSession, pipeline, Document.class);
    }

    @Override
    public <TResult> AggregatePublisher<TResult> aggregate(final ClientSession clientSession, final List<? extends Bson> pipeline,
                                                           final Class<TResult> clazz) {
        return new AggregatePublisherImpl<TResult>(wrapped.aggregate(clientSession.getWrapped(), pipeline, clazz));
    }

    @Override
    public ChangeStreamPublisher<Document> watch() {
        return watch(Document.class);
    }

    @Override
    public <TResult> ChangeStreamPublisher<TResult> watch(final Class<TResult> resultClass) {
        return watch(Collections.<Bson>emptyList(), resultClass);
    }

    @Override
    public ChangeStreamPublisher<Document> watch(final List<? extends Bson> pipeline) {
        return watch(pipeline, Document.class);
    }

    @Override
    public <TResult> ChangeStreamPublisher<TResult> watch(final List<? extends Bson> pipeline, final Class<TResult> resultClass) {
        return new ChangeStreamPublisherImpl<TResult>(wrapped.watch(pipeline, resultClass));
    }

    @Override
    public ChangeStreamPublisher<Document> watch(final ClientSession clientSession) {
        return watch(clientSession, Document.class);
    }

    @Override
    public <TResult> ChangeStreamPublisher<TResult> watch(final ClientSession clientSession, final Class<TResult> resultClass) {
        return watch(clientSession, Collections.<Bson>emptyList(), resultClass);
    }

    @Override
    public ChangeStreamPublisher<Document> watch(final ClientSession clientSession, final List<? extends Bson> pipeline) {
        return watch(clientSession, pipeline, Document.class);
    }

    @Override
    public <TResult> ChangeStreamPublisher<TResult> watch(final ClientSession clientSession, final List<? extends Bson> pipeline,
                                                          final Class<TResult> resultClass) {
        return new ChangeStreamPublisherImpl<TResult>(wrapped.watch(clientSession.getWrapped(), pipeline, resultClass));
    }

    @Override
    public MapReducePublisher<Document> mapReduce(final String mapFunction, final String reduceFunction) {
        return mapReduce(mapFunction, reduceFunction, Document.class);
    }

    @Override
    public <TResult> MapReducePublisher<TResult> mapReduce(final String mapFunction, final String reduceFunction,
                                                           final Class<TResult> clazz) {
        return new MapReducePublisherImpl<TResult>(wrapped.mapReduce(mapFunction, reduceFunction, clazz));
    }

    @Override
    public MapReducePublisher<Document> mapReduce(final ClientSession clientSession, final String mapFunction,
                                                  final String reduceFunction) {
        return mapReduce(clientSession, mapFunction, reduceFunction, Document.class);
    }

    @Override
    public <TResult> MapReducePublisher<TResult> mapReduce(final ClientSession clientSession, final String mapFunction,
                                                           final String reduceFunction, final Class<TResult> clazz) {
        return new MapReducePublisherImpl<TResult>(wrapped.mapReduce(clientSession.getWrapped(), mapFunction, reduceFunction, clazz));
    }

    @Override
    public Publisher<BulkWriteResult> bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(requests, new BulkWriteOptions());
    }

    @Override
    public Publisher<BulkWriteResult> bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests,
                                                final BulkWriteOptions options) {
        return new ObservableToPublisher<BulkWriteResult>(observe(new Block<SingleResultCallback<BulkWriteResult>>(){
            @Override
            public void apply(final SingleResultCallback<BulkWriteResult> callback) {
                wrapped.bulkWrite(requests, options, callback);
            }
        }));
    }

    @Override
    public Publisher<BulkWriteResult> bulkWrite(final ClientSession clientSession,
                                                final List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(clientSession, requests, new BulkWriteOptions());
    }

    @Override
    public Publisher<BulkWriteResult> bulkWrite(final ClientSession clientSession,
                                                final List<? extends WriteModel<? extends TDocument>> requests,
                                                final BulkWriteOptions options) {
        return new ObservableToPublisher<BulkWriteResult>(observe(new Block<SingleResultCallback<BulkWriteResult>>(){
            @Override
            public void apply(final SingleResultCallback<BulkWriteResult> callback) {
                wrapped.bulkWrite(clientSession.getWrapped(), requests, options, callback);
            }
        }));
    }

    @Override
    public Publisher<Success> insertOne(final TDocument document) {
        return insertOne(document, new InsertOneOptions());
    }

    @Override
    public Publisher<Success> insertOne(final TDocument document, final InsertOneOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.insertOne(document, options, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> insertOne(final ClientSession clientSession, final TDocument document) {
        return insertOne(clientSession, document, new InsertOneOptions());
    }

    @Override
    public Publisher<Success> insertOne(final ClientSession clientSession, final TDocument document, final InsertOneOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.insertOne(clientSession.getWrapped(), document, options, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> insertMany(final List<? extends TDocument> documents) {
        return insertMany(documents, new InsertManyOptions());
    }

    @Override
    public Publisher<Success> insertMany(final List<? extends TDocument> documents, final InsertManyOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.insertMany(documents, options, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> insertMany(final ClientSession clientSession, final List<? extends TDocument> documents) {
        return insertMany(clientSession, documents, new InsertManyOptions());
    }

    @Override
    public Publisher<Success> insertMany(final ClientSession clientSession, final List<? extends TDocument> documents,
                                         final InsertManyOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.insertMany(clientSession.getWrapped(), documents, options, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<DeleteResult> deleteOne(final Bson filter) {
        return deleteOne(filter, new DeleteOptions());
    }

    @Override
    public Publisher<DeleteResult> deleteOne(final Bson filter, final DeleteOptions options) {
        return new ObservableToPublisher<DeleteResult>(observe(new Block<SingleResultCallback<DeleteResult>>() {
            @Override
            public void apply(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteOne(filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<DeleteResult> deleteOne(final ClientSession clientSession, final Bson filter) {
        return deleteOne(clientSession, filter, new DeleteOptions());
    }

    @Override
    public Publisher<DeleteResult> deleteOne(final ClientSession clientSession, final Bson filter, final DeleteOptions options) {
        return new ObservableToPublisher<DeleteResult>(observe(new Block<SingleResultCallback<DeleteResult>>() {
            @Override
            public void apply(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteOne(clientSession.getWrapped(), filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<DeleteResult> deleteMany(final Bson filter) {
        return deleteMany(filter, new DeleteOptions());
    }

    @Override
    public Publisher<DeleteResult> deleteMany(final Bson filter, final DeleteOptions options) {
        return new ObservableToPublisher<DeleteResult>(observe(new Block<SingleResultCallback<DeleteResult>>() {
            @Override
            public void apply(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteMany(filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<DeleteResult> deleteMany(final ClientSession clientSession, final Bson filter) {
        return deleteMany(clientSession, filter, new DeleteOptions());
    }

    @Override
    public Publisher<DeleteResult> deleteMany(final ClientSession clientSession, final Bson filter, final DeleteOptions options) {
        return new ObservableToPublisher<DeleteResult>(observe(new Block<SingleResultCallback<DeleteResult>>() {
            @Override
            public void apply(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteMany(clientSession.getWrapped(), filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> replaceOne(final Bson filter, final TDocument replacement) {
        return replaceOne(filter, replacement, new ReplaceOptions());
    }

    @Override
    public Publisher<UpdateResult> replaceOne(final Bson filter, final TDocument replacement, final ReplaceOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.replaceOne(filter, replacement, options, callback);
            }
        }));
    }

    @Override
    @Deprecated
    public Publisher<UpdateResult> replaceOne(final Bson filter, final TDocument replacement, final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @SuppressWarnings("deprecation")
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.replaceOne(filter, replacement, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> replaceOne(final ClientSession clientSession, final Bson filter, final TDocument replacement) {
        return replaceOne(clientSession, filter, replacement, new ReplaceOptions());
    }

    @Override
    public Publisher<UpdateResult> replaceOne(final ClientSession clientSession, final Bson filter, final TDocument replacement,
                                              final ReplaceOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.replaceOne(clientSession.getWrapped(), filter, replacement, options, callback);
            }
        }));
    }

    @Override
    @Deprecated
    public Publisher<UpdateResult> replaceOne(final ClientSession clientSession, final Bson filter, final TDocument replacement,
                                              final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @SuppressWarnings("deprecation")
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.replaceOne(clientSession.getWrapped(), filter, replacement, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> updateOne(final Bson filter, final Bson update) {
        return updateOne(filter, update, new UpdateOptions());
    }

    @Override
    public Publisher<UpdateResult> updateOne(final Bson filter, final Bson update, final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateOne(filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> updateOne(final ClientSession clientSession, final Bson filter, final Bson update) {
        return updateOne(clientSession, filter, update, new UpdateOptions());
    }

    @Override
    public Publisher<UpdateResult> updateOne(final ClientSession clientSession, final Bson filter, final Bson update,
                                             final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateOne(clientSession.getWrapped(), filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> updateMany(final Bson filter, final Bson update) {
        return updateMany(filter, update, new UpdateOptions());
    }

    @Override
    public Publisher<UpdateResult> updateMany(final Bson filter, final Bson update, final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateMany(filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<UpdateResult> updateMany(final ClientSession clientSession, final Bson filter, final Bson update) {
        return updateMany(clientSession, filter, update, new UpdateOptions());
    }

    @Override
    public Publisher<UpdateResult> updateMany(final ClientSession clientSession, final Bson filter, final Bson update,
                                              final UpdateOptions options) {
        return new ObservableToPublisher<UpdateResult>(observe(new Block<SingleResultCallback<UpdateResult>>() {
            @Override
            public void apply(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateMany(clientSession.getWrapped(), filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndDelete(final Bson filter) {
        return findOneAndDelete(filter, new FindOneAndDeleteOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndDelete(final Bson filter, final FindOneAndDeleteOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndDelete(filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndDelete(final ClientSession clientSession, final Bson filter) {
        return findOneAndDelete(clientSession, filter, new FindOneAndDeleteOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndDelete(final ClientSession clientSession, final Bson filter,
                                                 final FindOneAndDeleteOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndDelete(clientSession.getWrapped(), filter, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndReplace(final Bson filter, final TDocument replacement) {
        return findOneAndReplace(filter, replacement, new FindOneAndReplaceOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndReplace(final Bson filter, final TDocument replacement, final FindOneAndReplaceOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndReplace(filter, replacement, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndReplace(final ClientSession clientSession, final Bson filter, final TDocument replacement) {
        return findOneAndReplace(clientSession, filter, replacement, new FindOneAndReplaceOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndReplace(final ClientSession clientSession, final Bson filter, final TDocument replacement,
                                                  final FindOneAndReplaceOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndReplace(clientSession.getWrapped(), filter, replacement, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndUpdate(final Bson filter, final Bson update) {
        return findOneAndUpdate(filter, update, new FindOneAndUpdateOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndUpdate(final Bson filter, final Bson update, final FindOneAndUpdateOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndUpdate(filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<TDocument> findOneAndUpdate(final ClientSession clientSession, final Bson filter, final Bson update) {
        return findOneAndUpdate(clientSession, filter, update, new FindOneAndUpdateOptions());
    }

    @Override
    public Publisher<TDocument> findOneAndUpdate(final ClientSession clientSession, final Bson filter, final Bson update,
                                                 final FindOneAndUpdateOptions options) {
        return new ObservableToPublisher<TDocument>(observe(new Block<SingleResultCallback<TDocument>>() {
            @Override
            public void apply(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndUpdate(clientSession.getWrapped(), filter, update, options, callback);
            }
        }));
    }

    @Override
    public Publisher<Success> drop() {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.drop(voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> drop(final ClientSession clientSession) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.drop(clientSession.getWrapped(), voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<String> createIndex(final Bson key) {
        return createIndex(key, new IndexOptions());
    }

    @Override
    public Publisher<String> createIndex(final Bson key, final IndexOptions options) {
        return new ObservableToPublisher<String>(observe(new Block<SingleResultCallback<String>>() {
            @Override
            public void apply(final SingleResultCallback<String> callback) {
                wrapped.createIndex(key, options, callback);
            }
        }));
    }

    @Override
    public Publisher<String> createIndex(final ClientSession clientSession, final Bson key) {
        return createIndex(clientSession, key, new IndexOptions());
    }

    @Override
    public Publisher<String> createIndex(final ClientSession clientSession, final Bson key, final IndexOptions options) {
        return new ObservableToPublisher<String>(observe(new Block<SingleResultCallback<String>>() {
            @Override
            public void apply(final SingleResultCallback<String> callback) {
                wrapped.createIndex(clientSession.getWrapped(), key, options, callback);
            }
        }));
    }

    @Override
    public Publisher<String> createIndexes(final List<IndexModel> indexes) {
        return createIndexes(indexes, new CreateIndexOptions());
    }

    @Override
    public Publisher<String> createIndexes(final List<IndexModel> indexes, final CreateIndexOptions createIndexOptions) {
        return new ObservableToPublisher<String>(observeAndFlatten(new Block<SingleResultCallback<List<String>>>() {
            @Override
            public void apply(final SingleResultCallback<List<String>> callback) {
                wrapped.createIndexes(indexes, createIndexOptions, callback);
            }
        }));
    }

    @Override
    public Publisher<String> createIndexes(final ClientSession clientSession, final List<IndexModel> indexes) {
        return createIndexes(clientSession, indexes, new CreateIndexOptions());
    }

    @Override
    public Publisher<String> createIndexes(final ClientSession clientSession, final List<IndexModel> indexes,
                                           final CreateIndexOptions createIndexOptions) {
        return new ObservableToPublisher<String>(observeAndFlatten(new Block<SingleResultCallback<List<String>>>() {
            @Override
            public void apply(final SingleResultCallback<List<String>> callback) {
                wrapped.createIndexes(clientSession.getWrapped(), indexes, createIndexOptions, callback);
            }
        }));
    }

    @Override
    public ListIndexesPublisher<Document> listIndexes() {
        return listIndexes(Document.class);
    }

    @Override
    public <TResult> ListIndexesPublisher<TResult> listIndexes(final Class<TResult> clazz) {
        return new ListIndexesPublisherImpl<TResult>(wrapped.listIndexes(clazz));
    }

    @Override
    public ListIndexesPublisher<Document> listIndexes(final ClientSession clientSession) {
        return listIndexes(clientSession, Document.class);
    }

    @Override
    public <TResult> ListIndexesPublisher<TResult> listIndexes(final ClientSession clientSession, final Class<TResult> clazz) {
        return new ListIndexesPublisherImpl<TResult>(wrapped.listIndexes(clientSession.getWrapped(), clazz));
    }

    @Override
    public Publisher<Success> dropIndex(final String indexName) {
        return dropIndex(indexName, new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndex(final Bson keys) {
        return dropIndex(keys, new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndex(final String indexName, final DropIndexOptions dropIndexOptions) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(indexName, dropIndexOptions, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> dropIndex(final Bson keys, final DropIndexOptions dropIndexOptions) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(keys, dropIndexOptions, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> dropIndex(final ClientSession clientSession, final String indexName) {
        return dropIndex(clientSession, indexName, new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndex(final ClientSession clientSession, final Bson keys) {
        return dropIndex(clientSession, keys, new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndex(final ClientSession clientSession, final String indexName,
                                        final DropIndexOptions dropIndexOptions) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(clientSession.getWrapped(), indexName, dropIndexOptions, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> dropIndex(final ClientSession clientSession, final Bson keys, final DropIndexOptions dropIndexOptions) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(clientSession.getWrapped(), keys, dropIndexOptions, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> dropIndexes() {
        return dropIndexes(new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndexes(final DropIndexOptions dropIndexOptions) {
        return dropIndex("*", dropIndexOptions);
    }

    @Override
    public Publisher<Success> dropIndexes(final ClientSession clientSession) {
        return dropIndexes(clientSession, new DropIndexOptions());
    }

    @Override
    public Publisher<Success> dropIndexes(final ClientSession clientSession, final DropIndexOptions dropIndexOptions) {
        return dropIndex(clientSession, "*", dropIndexOptions);
    }

    @Override
    public Publisher<Success> renameCollection(final MongoNamespace newCollectionNamespace) {
        return renameCollection(newCollectionNamespace, new RenameCollectionOptions());
    }

    @Override
    public Publisher<Success> renameCollection(final MongoNamespace newCollectionNamespace, final RenameCollectionOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.renameCollection(newCollectionNamespace, options, voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> renameCollection(final ClientSession clientSession, final MongoNamespace newCollectionNamespace) {
        return renameCollection(clientSession, newCollectionNamespace, new RenameCollectionOptions());
    }

    @Override
    public Publisher<Success> renameCollection(final ClientSession clientSession, final MongoNamespace newCollectionNamespace,
                                               final RenameCollectionOptions options) {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.renameCollection(clientSession.getWrapped(), newCollectionNamespace, options, voidToSuccessCallback(callback));
            }
        }));
    }

}
