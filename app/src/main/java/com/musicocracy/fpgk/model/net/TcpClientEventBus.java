/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This is a heavily modified version of https://github.com/allenxwang/RxNetty-1/blob/master/rx-netty-examples/src/main/java/io/reactivex/netty/examples/tcp/event/TcpEventStreamClient.java
 */
package com.musicocracy.fpgk.model.net;

import android.util.Log;

import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.Map;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

public class TcpClientEventBus implements ClientEventBus {
    private static final String TAG = TcpClientEventBus.class.getSimpleName();
    private final WritableEventStream<EnvelopeMsg> outputEventStream;
    private final WritableEventStream<EnvelopeMsg> inputEventStream;
    private final ProtoEnvelopeFactory factory;
    private final Map<MessageType, Observable<EnvelopeMsg>> messageBus;
    private RxClient<String, String> client;
    private Subscription driver;  // We need a subscription to make Tx/Rx callbacks run.

    public TcpClientEventBus(ProtoEnvelopeFactory factory) {
        this.outputEventStream = new WritableEventStream<>();
        this.inputEventStream = new WritableEventStream<>();
        this.factory = factory;
        this.messageBus = factory.createMessageBus(inputEventStream.getObservable());
    }

    private void unsubscribeDriver() {
        if (driver != null && !driver.isUnsubscribed()) {
            driver.unsubscribe();
        }
    }

    @Override
    public void connect(String host, int port) {
        this.client = RxNetty.createTcpClient(host, port, PipelineConfigurators.stringMessageConfigurator());
        unsubscribeDriver();
        driver = client.connect().flatMap(new Func1<ObservableConnection<String, String>, Observable<EnvelopeMsg>>() {
            @Override
            public Observable<EnvelopeMsg> call(final ObservableConnection<String, String> serverConnection) {
                Observable<EnvelopeMsg> receiver = serverConnection
                        .getInput()
                        .share()
                        .flatMap(new Func1<String, Observable<EnvelopeMsg>>() {
                            @Override
                            public Observable<EnvelopeMsg> call(String string) {
                                inputEventStream.broadcast(factory.envelopeFromBase64(string));
                                return Observable.empty();
                            }
                        });

                Observable<EnvelopeMsg> transmitter = outputEventStream.getObservable()
                        .flatMap(new Func1<EnvelopeMsg, Observable<Notification<Void>>>() {
                            @Override
                            public Observable<Notification<Void>> call(EnvelopeMsg message) {
                                String raw = factory.envelopeToBase64(message);
                                Log.i(TAG, "Sent: " + raw);
                                return serverConnection.writeAndFlush(raw).materialize();
                            }
                        })
                        .takeWhile(new Func1<Notification<Void>, Boolean>() {
                            @Override
                            public Boolean call(Notification<Void> notification) {
                                return !notification.isOnError();
                            }
                        })
                        .doAfterTerminate(new Action0() {
                            @Override
                            public void call() {
                                Log.i(TAG, " --> Closing connection and stream");
                            }
                        })
                        .map(new Func1<Notification<Void>, EnvelopeMsg>() {
                            @Override
                            public EnvelopeMsg call(Notification<Void> notification) {
                                return EnvelopeMsg.getDefaultInstance();
                            }
                        });

                return Observable.merge(transmitter, receiver);
            }
        }).subscribe();
    }

    @Override
    public void broadcast(EnvelopeMsg message) {
        this.outputEventStream.broadcast(message);
    }

    @Override
    public void disconnect() {
        unsubscribeDriver();
        client.shutdown();
    }

    @Override
    public Map<MessageType, Observable<EnvelopeMsg>> getMessageBus() {
        return messageBus;
    }
}
