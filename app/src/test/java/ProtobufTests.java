import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.net.proto.BrowseSongsMsg;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.PlayRequestMsg;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProtobufTests {
    private static final ProtoEnvelopeFactory factory = new ProtoEnvelopeFactory();

    private static PlayRequestMsg createPlayRequestMsg() {
        return PlayRequestMsg.newBuilder()
                .setMusicService(MusicService.SPOTIFY.name())
                .setRequesterId("REQUESTER_UNIQUE_ID_1")
                .setSongId("SONG_ID_1")
                .build();
    }

    private static BrowseSongsMsg createBrowseSongMsg() {
        return BrowseSongsMsg.newBuilder()
                .setArtist("Queen" + String.valueOf(1))
                .setSongTitle("Bicycle" + String.valueOf(3))
                .build();
    }

    @Test
    public void protobufSerialization_instanceEqual_trueForMatching() throws InvalidProtocolBufferException {
        PlayRequestMsg sent = createPlayRequestMsg();
        EnvelopeMsg sentEnvelope = factory.createEnvelopeFor(sent);
        byte[] bytes = sentEnvelope.toByteArray();
        EnvelopeMsg receivedEnvelope = EnvelopeMsg.parseFrom(bytes);

        PlayRequestMsg received = PlayRequestMsg.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }

    @Test
    public void protobufBase64Conversion_ConvertToAndBack_SameProto() throws InvalidProtocolBufferException {
        PlayRequestMsg sent = createPlayRequestMsg();
        EnvelopeMsg sentEnvelope = factory.createEnvelopeFor(sent);

        String base64 = factory.envelopeToBase64(sentEnvelope);
        EnvelopeMsg receivedEnvelope = factory.envelopeFromBase64(base64);
        PlayRequestMsg received = PlayRequestMsg.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }

    @Test
    public void protobufBase64Conversion_ConvertToAndBack_SameProto_Browse() throws InvalidProtocolBufferException {
        BrowseSongsMsg sent = createBrowseSongMsg();
        EnvelopeMsg sentEnvelope = factory.createEnvelopeFor(sent);

        String base64 = factory.envelopeToBase64(sentEnvelope);
        EnvelopeMsg receivedEnvelope = factory.envelopeFromBase64(base64);
        BrowseSongsMsg received = BrowseSongsMsg.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }
}
