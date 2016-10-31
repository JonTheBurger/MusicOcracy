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
 * This is a heavily modified version of https://github.com/allenxwang/RxNetty-1/blob/master/rx-netty-examples/src/main/java/io/reactivex/netty/examples/tcp/event/TcpEventStreamServer.java
 */
package com.musicocracy.fpgk.model.net;

import android.util.Log;

import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.Map;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Notification;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class TcpServerEventBus implements ServerEventBus {
    private static final String TAG = TcpServerEventBus.class.getSimpleName();
    private final RxServer<String, String> server;
    private final WritableEventStream<EnvelopeMsg> outputEventStream;
    private final WritableEventStream<EnvelopeMsg> inputEventStream;
    private final Map<MessageType, Observable<EnvelopeMsg>> messageBus; // TODO: Tuple<Observable<EnvelopeMsg>, CONNECTION>
    public final int port;

    public TcpServerEventBus(int port, final ProtoEnvelopeFactory factory) {
        this.port = port;
        this.outputEventStream = new WritableEventStream<>();
        this.inputEventStream = new WritableEventStream<>();
        this.messageBus = factory.createMessageBus(inputEventStream.getObservable());
        this.server = RxNetty.createTcpServer(this.port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
            @Override
            public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                Observable<Void> receiver = newConnection
                        .getInput()
                        .share()
                        .flatMap(new Func1<String, Observable<Void>>() {
                            @Override
                            public Observable<Void> call(String string) {
                                inputEventStream.broadcast(factory.envelopeFromBase64(string));
                                return Observable.empty();
                            }
                        });

                Observable<Void> transmitter = outputEventStream.getObservable()
                        .flatMap(new Func1<EnvelopeMsg, Observable<Notification<Void>>>() {
                            @Override
                            public Observable<Notification<Void>> call(EnvelopeMsg message) {
                                String raw = factory.envelopeToBase64(message);
                                Log.i(TAG, "Sent: " + raw);
                                return newConnection.writeAndFlush(raw).materialize();
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
                        .map(new Func1<Notification<Void>, Void>() {
                            @Override
                            public Void call(Notification<Void> notification) {
                                return null;
                            }
                        });

                return Observable.merge(transmitter, receiver);
            }
        });
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void broadcast(EnvelopeMsg message) {
        this.outputEventStream.broadcast(message);
    }

    @Override
    public void shutdown() throws InterruptedException {
        server.shutdown();
    }

    @Override
    public Map<MessageType, Observable<EnvelopeMsg>> getMessageBus() {
        return messageBus;
    }
}
