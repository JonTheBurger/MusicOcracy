package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import rx.Observable;
import rx.functions.Func1;

/**
 * A wrapper around {@link RxTcpClient} that translates between {@link EnvelopeMsg} and Strings
 * @see ServerEventBus
 */
public class ClientEventBus {
    private final RxTcpClient client;
    private final ProtoEnvelopeFactory factory;

    /**
     * Basic constructor.
     * @param client reactive client used as networking backend
     * @param factory {@link EnvelopeMsg} to String translator
     */
    public ClientEventBus(RxTcpClient client, ProtoEnvelopeFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    /**
     * Starts the client. The client will automatically attempt to connect to servers on the
     * specified host/port, and automatically attempt reconnection if the server goes down.
     * @param host IPv4 address
     * @param port Port number
     */
    public void start(String host, int port) {
        client.start(host, port);
    }

    /**
     * Sends a message to the connected server. If no server is connected, the message is swallowed.
     * @param message Message that will attempted to be sent to the server.
     */
    public void send(MessageLite message) {
        client.send(factory.envelopeToBase64(factory.createEnvelopeFor(message)));
    }

    /**
     * Stops the client. The client will no longer attempt to reconnect/reconnect to servers.
     */
    public void stop() {
        client.stop();
    }

    /**
     * Boolean condition indicating if client is running. Note that running is not the same as
     * connected, i.e. running server might not be connected to a server. This is because a client
     * won't start running until it successfully connects to a server, but if that server goes down,
     * the client will still be running, attempting to reconnect.
     * @return Boolean condition indicating if server is running.
     * @sese #getIsRunningObservable()
     */
    public boolean isRunning() {
        return client.isRunning();
    }

    /**
     * An observable stream of all possible events from the server.
     * @return An observable stream of all possible events from the server.
     */
    public Observable<EnvelopeMsg> getObservable() {
        return client.getObservable()
            .map(new Func1<String, EnvelopeMsg>() {
                @Override
                public EnvelopeMsg call(String base64) {
                    return factory.envelopeFromBase64(base64);
                }
            });
    }

    /**
     * An observable stream of events received from the server filtered by type.
     * @param type Designates the type of messages to listen for
     * @return An observable stream of received from the server.
     */
    public Observable<EnvelopeMsg> getObservable(final MessageType type) {
        return getObservable()
            .filter(new Func1<EnvelopeMsg, Boolean>() {
                @Override
                public Boolean call(EnvelopeMsg message) {
                    return message.getHeader().getType() == type;
                }
            });
    }

    /**
     * An observable stream of client running status changes.
     * @return An observable stream of server running status changes.
     * @see #isRunning()
     */
    public Observable<Boolean> getIsRunningObservable() {
        return client.getIsRunningObservable();
    }

    /**
     * An observable stream of log messages provided as Strings.
     * @return An observable stream of log messages provided as Strings.
     */
    public Observable<String> getObservableLog() {
        return client.getObservableLog();
    }
}
