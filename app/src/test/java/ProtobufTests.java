import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.PlayRequestMsg;

import org.junit.Test;

import static junit.framework.Assert.*;

public class ProtobufTests {
    @Test
    public void protobufSerialization_instanceEqual_trueForMatching() throws InvalidProtocolBufferException {
        ProtoEnvelopeFactory factory = new ProtoEnvelopeFactory();
        PlayRequestMsg sent = PlayRequestMsg.newBuilder()
                .setMusicService(MusicService.SPOTIFY.name())
                .setRequesterId("REQUESTER_UNIQUE_ID_1")
                .setSongId("SONG_ID_1")
                .build();
        EnvelopeMsg sentEnvelope = factory.createEnvelopeFor(sent);
        byte[] bytes = sentEnvelope.toByteArray();
        EnvelopeMsg receivedEnvelope = EnvelopeMsg.parseFrom(bytes);

        PlayRequestMsg received = PlayRequestMsg.parseFrom(receivedEnvelope.getBody().getValue());

        assertEquals(sent, received);
    }
}
