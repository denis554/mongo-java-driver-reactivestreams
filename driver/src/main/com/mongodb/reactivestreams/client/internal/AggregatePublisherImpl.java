/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client.internal;

import com.mongodb.Block;
import com.mongodb.client.model.Collation;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.Success;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.reactivestreams.client.internal.PublisherHelper.voidToSuccessCallback;


@SuppressWarnings("deprecation")
final class AggregatePublisherImpl<TResult> implements AggregatePublisher<TResult> {

    private final com.mongodb.async.client.AggregateIterable<TResult> wrapped;

    AggregatePublisherImpl(final com.mongodb.async.client.AggregateIterable<TResult> wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }


    @Override
    public AggregatePublisher<TResult> allowDiskUse(final Boolean allowDiskUse) {
        wrapped.allowDiskUse(allowDiskUse);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        wrapped.maxTime(maxTime, timeUnit);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> maxAwaitTime(final long maxAwaitTime, final TimeUnit timeUnit) {
        wrapped.maxAwaitTime(maxAwaitTime, timeUnit);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> useCursor(final Boolean useCursor) {
        wrapped.useCursor(useCursor);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> bypassDocumentValidation(final Boolean bypassDocumentValidation) {
        wrapped.bypassDocumentValidation(bypassDocumentValidation);
        return this;
    }

    @Override
    public Publisher<Success> toCollection() {
        return new ObservableToPublisher<Success>(com.mongodb.async.client.Observables.observe(
                new Block<com.mongodb.async.SingleResultCallback<Success>>(){
                    @Override
                    public void apply(final com.mongodb.async.SingleResultCallback<Success> callback) {
                        wrapped.toCollection(voidToSuccessCallback(callback));
                    }
                }));
    }

    @Override
    public AggregatePublisher<TResult> collation(final Collation collation) {
        wrapped.collation(collation);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> comment(final String comment) {
        wrapped.comment(comment);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> hint(final Bson hint) {
        wrapped.hint(hint);
        return this;
    }

    @Override
    public AggregatePublisher<TResult> batchSize(final int batchSize) {
        wrapped.batchSize(batchSize);
        return this;
    }

    @Override
    public Publisher<TResult> first() {
        return new ObservableToPublisher<TResult>(com.mongodb.async.client.Observables.observe(
                new Block<com.mongodb.async.SingleResultCallback<TResult>>() {
                    @Override
                    public void apply(final com.mongodb.async.SingleResultCallback<TResult> callback) {
                        wrapped.first(callback);
                    }
                }));
    }

    @Override
    public void subscribe(final Subscriber<? super TResult> s) {
        new ObservableToPublisher<TResult>(com.mongodb.async.client.Observables.observe(wrapped)).subscribe(s);
    }
}
