/*
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client.internal;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.gridfs.GridFSUploadStream;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;

import java.nio.ByteBuffer;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.async.client.Observables.observe;
import static com.mongodb.reactivestreams.client.internal.PublisherHelper.voidToSuccessCallback;

final class GridFSUploadStreamImpl implements GridFSUploadStream {

    private final com.mongodb.async.client.gridfs.GridFSUploadStream wrapped;

    GridFSUploadStreamImpl(final com.mongodb.async.client.gridfs.GridFSUploadStream wrapped) {
        this.wrapped = notNull("GridFSUploadStream", wrapped);
    }

    @Override
    public ObjectId getObjectId() {
        return wrapped.getObjectId();
    }

    @Override
    public BsonValue getId() {
        return wrapped.getId();
    }

    @Override
    public Publisher<Integer> write(final ByteBuffer src) {
        return new ObservableToPublisher<Integer>(observe(new Block<SingleResultCallback<Integer>>() {
            @Override
            public void apply(final SingleResultCallback<Integer> callback) {
                wrapped.write(src, callback);
            }
        }));
    }

    @Override
    public Publisher<Success> close() {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.close(voidToSuccessCallback(callback));
            }
        }));
    }

    @Override
    public Publisher<Success> abort() {
        return new ObservableToPublisher<Success>(observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.abort(voidToSuccessCallback(callback));
            }
        }));
    }
}
