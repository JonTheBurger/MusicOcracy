package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.CoinStatusReply;
import com.musicocracy.fpgk.net.proto.CoinStatusRequest;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;
import com.musicocracy.fpgk.net.proto.Header;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProtoEnvelopeFactory {
    private static final int CURRENT_VERSION = 0;
    private static final Map<Class, MessageType> messageTypeMap;
    private final Base64Encoder encoder;
    static {
        Map<Class, MessageType> map = new HashMap<>(MessageType.values().length);

        map.put(BasicReply.class, MessageType.BASIC_REPLY);
        map.put(ConnectRequest.class, MessageType.CONNECT_REQUEST);
        map.put(PlayRequestRequest.class, MessageType.PLAY_REQUEST_REQUEST);
        map.put(SendVoteRequest.class, MessageType.SEND_VOTE_REQUEST);
        map.put(VotableSongsRequest.class, MessageType.VOTABLE_SONGS_REQUEST);
        map.put(VotableSongsReply.class, MessageType.VOTABLE_SONGS_REPLY);
        map.put(BrowseSongsRequest.class, MessageType.BROWSE_SONGS_REQUEST);
        map.put(BrowseSongsReply.class, MessageType.BROWSE_SONGS_REPLY);
        map.put(CoinStatusRequest.class, MessageType.COIN_STATUS_REQUEST);
        map.put(CoinStatusReply.class, MessageType.COIN_STATUS_REPLY);

        messageTypeMap = Collections.unmodifiableMap(map);
    }

    public ProtoEnvelopeFactory(Base64Encoder encoder) {
        this.encoder = encoder;
    }

    public Map<Class, MessageType> getMessageTypeMap() {
        return messageTypeMap;
    }

    public Envelope createEnvelopeFor(MessageLite message) {
        return createEnvelopeFor(message, CURRENT_VERSION);
    }

    public Envelope createEnvelopeFor(MessageLite message, int version) {
        if (!(message instanceof Envelope)) {
            return createEnvelopeForRaw(message, version);
        } else if (((Envelope)message).getHeader().getVersion() == version) {
            return (Envelope)message;
        } else {
            throw new IllegalArgumentException("Attempted to create Envelope of version " + version + " with an Envelope of version " + ((Envelope)message).getHeader().getVersion());
        }
    }

    // Assumes MessageLite !instanceof Envelope
    private Envelope createEnvelopeForRaw(MessageLite message, int version) {
        switch (version) {
            case 0:
                return createEnvelopeVersion0(message);
            default:
                return Envelope.getDefaultInstance();
        }
    }

    public Envelope createEnvelopeVersion0(MessageLite body) {
        Header header = Header.newBuilder()
                .setType(messageTypeMap.get(body.getClass()))
                .setVersion(0)
                .build();
        Envelope envelope = Envelope.newBuilder()
                .setHeader(header)
                .setBody(body.toByteString())
                .build();

        return envelope;
    }

    public Envelope envelopeFromBase64(String base64) {
        try {
            byte[] bytes = encoder.base64ToBytes(base64);
            return Envelope.parseFrom(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return Envelope.getDefaultInstance();
        }
    }

    public String envelopeToBase64(Envelope message) {
        return encoder.bytesToBase64(message.toByteArray());
    }
}
