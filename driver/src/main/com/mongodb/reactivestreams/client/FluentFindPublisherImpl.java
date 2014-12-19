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
import com.mongodb.Function;
import com.mongodb.MongoNamespace;
import com.mongodb.client.model.FindOptions;
import com.mongodb.client.options.OperationOptions;
import com.mongodb.operation.AsyncOperationExecutor;
import com.mongodb.operation.FindOperation;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Codec;
import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

class FluentFindPublisherImpl<T> implements FluentFindPublisher<T> {
    private final MongoNamespace namespace;
    private final OperationOptions options;
    private final AsyncOperationExecutor executor;
    private final FindOptions findOptions;
    private Object filter;
    private final Class<T> clazz;

    FluentFindPublisherImpl(final MongoNamespace namespace, final OperationOptions options, final AsyncOperationExecutor executor,
                            final Object filter, final FindOptions findOptions, final Class<T> clazz) {
        this.namespace = notNull("namespace", namespace);
        this.options = notNull("options", options);
        this.executor = notNull("executor", executor);
        this.filter = notNull("filter", filter);
        this.findOptions = notNull("findOptions", findOptions);
        this.clazz = notNull("clazz", clazz);
    }

    @Override
    public MongoPublisher<T> first() {
        return Publishers.flattenCursor(createQueryOperation().batchSize(0).limit(-1), options.getReadPreference(), executor);
    }

    @Override
    public FluentFindPublisher<T> filter(final Object filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public FluentFindPublisher<T> limit(final int limit) {
        findOptions.limit(limit);
        return this;
    }

    @Override
    public FluentFindPublisher<T> skip(final int skip) {
        findOptions.skip(skip);
        return this;
    }

    @Override
    public FluentFindPublisher<T> maxTime(final long maxTime, final TimeUnit timeUnit) {
        notNull("timeUnit", timeUnit);
        findOptions.maxTime(maxTime, timeUnit);
        return this;
    }

    @Override
    public FluentFindPublisher<T> batchSize(final int batchSize) {
        findOptions.batchSize(batchSize);
        return this;
    }

    @Override
    public FluentFindPublisher<T> modifiers(final Object modifiers) {
        findOptions.modifiers(modifiers);
        return this;
    }

    @Override
    public FluentFindPublisher<T> projection(final Object projection) {
        findOptions.projection(projection);
        return this;
    }

    @Override
    public FluentFindPublisher<T> sort(final Object sort) {
        findOptions.sort(sort);
        return this;
    }

    @Override
    public FluentFindPublisher<T> noCursorTimeout(final boolean noCursorTimeout) {
        findOptions.noCursorTimeout(noCursorTimeout);
        return this;
    }

    @Override
    public FluentFindPublisher<T> oplogReplay(final boolean oplogReplay) {
        findOptions.oplogReplay(oplogReplay);
        return this;
    }

    @Override
    public FluentFindPublisher<T> partial(final boolean partial) {
        findOptions.partial(partial);
        return this;
    }

    @Override
    public FluentFindPublisher<T> cursorType(final CursorType cursorType) {
        findOptions.cursorType(cursorType);
        return this;
    }

    private <C> Codec<C> getCodec(final Class<C> clazz) {
        return options.getCodecRegistry().get(clazz);
    }

    private FindOperation<T> createQueryOperation() {
        return new FindOperation<T>(namespace, getCodec(clazz))
               .filter(asBson(filter))
               .batchSize(findOptions.getBatchSize())
               .skip(findOptions.getSkip())
               .limit(findOptions.getLimit())
               .maxTime(findOptions.getMaxTime(MILLISECONDS), MILLISECONDS)
               .modifiers(asBson(findOptions.getModifiers()))
               .projection(asBson(findOptions.getProjection()))
               .sort(asBson(findOptions.getSort()))
               .noCursorTimeout(findOptions.isNoCursorTimeout())
               .oplogReplay(findOptions.isOplogReplay())
               .partial(findOptions.isPartial())
               .cursorType(findOptions.getCursorType())
               .slaveOk(options.getReadPreference().isSlaveOk());
    }

    private BsonDocument asBson(final Object document) {
        return BsonDocumentWrapper.asBsonDocument(document, options.getCodecRegistry());
    }


    @Override
    public void subscribe(final Subscriber<? super T> s) {
        publish().subscribe(s);
    }

    @Override
    public <O> MongoPublisher<O> map(final Function<? super T, ? extends O> function) {
        return Publishers.map(publish(), function);
    }

    MongoPublisher<T> publish() {
        return Publishers.flattenCursor(createQueryOperation(), options.getReadPreference(), executor);
    }
}
