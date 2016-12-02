package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.Envelope;

public class ProtoMessageBySender {
    private final StringMessageBySender raw;
    private final ProtoEnvelopeFactory factory;
    public final Envelope message;

    public ProtoMessageBySender(StringMessageBySender raw, ProtoEnvelopeFactory factory) {
        this.raw = raw;
        this.factory = factory;
        this.message = factory.envelopeFromBase64(raw.message);
    }

    public void replyWith(MessageLite message) {
        Envelope envelope = factory.createEnvelopeFor(message);
        String base64 = factory.envelopeToBase64(envelope);
        raw.sender.writeAndFlush(base64);
    }

    public void closeConnection() {
        raw.sender.close();
    }
}
