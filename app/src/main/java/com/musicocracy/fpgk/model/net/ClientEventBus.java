package com.musicocracy.fpgk.model.net;

import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.Map;

import rx.Observable;

/**
 * Allows a client to interact with a server in a functional reactive manner.
 * Incoming messages are observable by type. Can send messages to server.
 */
public interface ClientEventBus {
    void connect(String host, int port);
    /**
     * @param message The envelope sent to the server
     */
    void broadcast(EnvelopeMsg message);
    void disconnect();
    /**
     * @return A map sorting received messages by their type. The message of the given type can be
     * found in the @see EnvelopeMsg#getBody() of the @see EnvelopeMsg
     */
    Map<MessageType, Observable<EnvelopeMsg>> getMessageBus();
}
