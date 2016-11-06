package com.musicocracy.fpgk.model.net;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.BrowseSongsAckMsg;
import com.musicocracy.fpgk.net.proto.BrowseSongsMsg;
import com.musicocracy.fpgk.net.proto.ConnectRequestAckMsg;
import com.musicocracy.fpgk.net.proto.ConnectRequestMsg;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.GetVotableSongsMsg;
import com.musicocracy.fpgk.net.proto.HeaderMsg;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestAckMsg;
import com.musicocracy.fpgk.net.proto.PlayRequestMsg;
import com.musicocracy.fpgk.net.proto.SendVotableSongsMsg;
import com.musicocracy.fpgk.net.proto.SendVoteMsg;

public class ProtoEnvelopeFactory {
    private static final int CURRENT_VERSION = 0;
    private static final BiMap<MessageType, Class> messageTypeMap;
    static {
        BiMap<MessageType, Class> map = HashBiMap.create(MessageType.values().length);

        map.put(MessageType.UNKNOWN, null);
        map.put(MessageType.CONNECT_REQUEST, ConnectRequestMsg.class);
        map.put(MessageType.CONNECT_REQUEST_ACK, ConnectRequestAckMsg.class);
        map.put(MessageType.PLAY_REQUEST, PlayRequestMsg.class);
        map.put(MessageType.PLAY_REQUEST_ACK, PlayRequestAckMsg.class);
        map.put(MessageType.SEND_VOTE, SendVoteMsg.class);
        map.put(MessageType.GET_VOTABLE_SONGS, GetVotableSongsMsg.class);
        map.put(MessageType.SEND_VOTABLE_SONGS, SendVotableSongsMsg.class);
        map.put(MessageType.BROWSE_SONGS, BrowseSongsMsg.class);
        map.put(MessageType.BROWSE_SONGS_ACK, BrowseSongsAckMsg.class);

        messageTypeMap = Maps.unmodifiableBiMap(map);
    }

    public BiMap<MessageType, Class> getMessageTypeMap() {
        return messageTypeMap;
    }

    public EnvelopeMsg createEnvelopeFor(MessageLite body) {
        return createEnvelopeFor(body, CURRENT_VERSION);
    }

    public EnvelopeMsg createEnvelopeFor(MessageLite body, int version) {
        switch (version) {
            case 0:
                return createEnvelopeVersion0(body);
            default:
                return EnvelopeMsg.getDefaultInstance();
        }
    }

    public EnvelopeMsg createEnvelopeVersion0(MessageLite body) {
        HeaderMsg header = HeaderMsg.newBuilder()
                .setType(messageTypeMap.inverse().get(body.getClass()))
                .setVersion(0)
                .build();
        EnvelopeMsg envelope = EnvelopeMsg.newBuilder()
                .setHeader(header)
                .setBody(body.toByteString())
                .build();

        return envelope;
    }

    public EnvelopeMsg envelopeFromBase64(String base64) {
        try {
            byte[] bytes = BaseEncoding.base64().decode(base64.trim());
            return EnvelopeMsg.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return EnvelopeMsg.getDefaultInstance();
        }
    }

    public String envelopeToBase64(EnvelopeMsg message) {
        return BaseEncoding.base64().encode(message.toByteArray()) + '\n';
    }
}
