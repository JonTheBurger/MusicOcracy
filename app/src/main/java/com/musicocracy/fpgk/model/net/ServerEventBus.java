package com.musicocracy.fpgk.model.net;

import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.Map;

import rx.Observable;

/**
 * Allows a server to interact with its clients in a functional reactive manner.
 * Incoming messages are observable by type. Outgoing messages can be sent to all clients.
 */
public interface ServerEventBus {
    int DEFAULT_PORT = 8100;
    void start();
    /**
     * @param message The envelope sent to all clients
     */
    void broadcast(EnvelopeMsg message);
    void shutdown() throws InterruptedException;
    /**
     * @return A map sorting received messages by their type. The message of the given type can be
     * found in the @see EnvelopeMsg#getBody() of the @see EnvelopeMsg
     */
    Map<MessageType, Observable<EnvelopeMsg>> getMessageBus();
}
