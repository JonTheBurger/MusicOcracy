package com.musicocracy.fpgk.model.net;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.protobuf.Any;
import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.ConnectRequestAckMsg;
import com.musicocracy.fpgk.net.proto.ConnectRequestMsg;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.FooterMsg;
import com.musicocracy.fpgk.net.proto.GetVotableSongsMsg;
import com.musicocracy.fpgk.net.proto.HeaderMsg;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestAckMsg;
import com.musicocracy.fpgk.net.proto.PlayRequestMsg;
import com.musicocracy.fpgk.net.proto.SendVotableSongsMsg;
import com.musicocracy.fpgk.net.proto.SendVoteMsg;

public class ProtoEnvelopeFactory {
    static final BiMap<MessageType, Class> messageTypeMap;
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

        messageTypeMap = Maps.unmodifiableBiMap(map);
    }

    public EnvelopeMsg createEnvelopeFor(MessageLite body) {
        return createEnvelopeVersion0(body);
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
        HeaderMsg.Builder headerBuilder = HeaderMsg.newBuilder()
                .setType(messageTypeMap.inverse().get(body.getClass()));
        FooterMsg.Builder footerBuilder = FooterMsg.newBuilder();
        EnvelopeMsg.Builder envelopeBuilder = EnvelopeMsg.newBuilder()
                .setHeader(headerBuilder)
                .setBody(
                        Any.newBuilder()
                                .setValue(body.toByteString())
                )
                .setFooter(footerBuilder);

        return envelopeBuilder.build();
    }
}
