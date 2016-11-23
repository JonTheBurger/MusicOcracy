package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;

public class ProtoMessageBySender {
    private final StringMessageBySender raw;
    private final ProtoEnvelopeFactory factory;
    public final EnvelopeMsg message;

    public ProtoMessageBySender(StringMessageBySender raw, ProtoEnvelopeFactory factory) {
        this.raw = raw;
        this.factory = factory;
        this.message = factory.envelopeFromBase64(raw.message);
    }

    public void replyWith(MessageLite message) {
        EnvelopeMsg envelope = factory.createEnvelopeFor(message);
        raw.sender.writeAndFlush(factory.envelopeToBase64(envelope));
    }
}
