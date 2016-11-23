package com.musicocracy.fpgk.domain.net;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.CoinStatusReply;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;
import com.musicocracy.fpgk.net.proto.Header;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;

public class ProtoEnvelopeFactory {
    private static final int CURRENT_VERSION = 0;
    private static final BiMap<MessageType, Class> messageTypeMap;
    static {
        BiMap<MessageType, Class> map = HashBiMap.create(MessageType.values().length);

        map.put(MessageType.UNKNOWN, null);
        map.put(MessageType.BASIC_REPLY, BasicReply.class);
        map.put(MessageType.CONNECT_REQUEST, ConnectRequest.class);
        map.put(MessageType.PLAY_REQUEST_REQUEST, PlayRequestRequest.class);
        map.put(MessageType.SEND_VOTE_REQUEST, SendVoteRequest.class);
        map.put(MessageType.VOTABLE_SONGS_REQUEST, VotableSongsRequest.class);
        map.put(MessageType.VOTABLE_SONGS_REPLY, VotableSongsReply.class);
        map.put(MessageType.BROWSE_SONGS_REQUEST, BrowseSongsRequest.class);
        map.put(MessageType.BROWSE_SONGS_REPLY, BrowseSongsReply.class);
        map.put(MessageType.COIN_STATUS_REQUEST, ConnectRequest.class);
        map.put(MessageType.COIN_STATUS_REPLY, CoinStatusReply.class);

        messageTypeMap = Maps.unmodifiableBiMap(map);
    }

    public BiMap<MessageType, Class> getMessageTypeMap() {
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
                .setType(messageTypeMap.inverse().get(body.getClass()))
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
            byte[] bytes = BaseEncoding.base64().decode(base64.trim());
            return Envelope.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return Envelope.getDefaultInstance();
        }
    }

    public String envelopeToBase64(Envelope message) {
        return BaseEncoding.base64().encode(message.toByteArray()) + '\n';
    }
}
