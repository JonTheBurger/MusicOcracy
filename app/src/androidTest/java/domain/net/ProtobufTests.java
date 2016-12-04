package domain.net;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.net.Base64Encoder;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProtobufTests {
    private static final ProtoEnvelopeFactory factory = new ProtoEnvelopeFactory(new Base64Encoder());

    private static PlayRequestRequest createPlayRequestRequest() {
        return PlayRequestRequest.newBuilder()
                .setMusicService(MusicService.SPOTIFY.name())
                .setRequesterId("REQUESTER_UNIQUE_ID_1")
                .setUri("SONG_ID_1")
                .build();
    }

    private static BrowseSongsRequest createBrowseSongMsg() {
        return BrowseSongsRequest.newBuilder()
                .setSongTitle("Bicycle" + String.valueOf(3))
                .build();
    }

    @Test
    public void protobufSerialization_instanceEqual_trueForMatching() throws InvalidProtocolBufferException {
        PlayRequestRequest sent = createPlayRequestRequest();
        Envelope sentEnvelope = factory.createEnvelopeFor(sent);
        byte[] bytes = sentEnvelope.toByteArray();
        Envelope receivedEnvelope = Envelope.parseFrom(bytes);

        PlayRequestRequest received = PlayRequestRequest.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }

    @Test
    public void protobufBase64Conversion_ConvertToAndBack_SameProto() throws InvalidProtocolBufferException {
        PlayRequestRequest sent = createPlayRequestRequest();
        Envelope sentEnvelope = factory.createEnvelopeFor(sent);

        String base64 = factory.envelopeToBase64(sentEnvelope);
        Envelope receivedEnvelope = factory.envelopeFromBase64(base64);
        PlayRequestRequest received = PlayRequestRequest.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }

    @Test
    public void protobufBase64Conversion_ConvertToAndBack_SameProto_Browse() throws InvalidProtocolBufferException {
        BrowseSongsRequest sent = createBrowseSongMsg();
        Envelope sentEnvelope = factory.createEnvelopeFor(sent);

        String base64 = factory.envelopeToBase64(sentEnvelope);
        Envelope receivedEnvelope = factory.envelopeFromBase64(base64);
        BrowseSongsRequest received = BrowseSongsRequest.parseFrom(receivedEnvelope.getBody());

        assertEquals(sent, received);
    }

    @Test
    public void protobufBrowseRequestReply_ConvertToAndBack_SameProto_Browse() throws InvalidProtocolBufferException {
        BrowseSongsReply.Builder builder = BrowseSongsReply.newBuilder();
        for (int i = 0; i < 10; i++) {
            builder .addSongs(BrowseSongsReply.BrowsableSong.newBuilder()
                    .setTitle("Title" + i)
                    // Gets the name of the first artist
                    .setArtist("Artist" + i)
                    .setUri("URI" + i)
                    .setMusicService("Spotify")
                    .build());
        }
        BrowseSongsReply sentProto = builder.build();
        Envelope sent = factory.createEnvelopeFor(sentProto);
        String base64 = factory.envelopeToBase64(sent);

        Envelope received = factory.envelopeFromBase64(base64);
        BrowseSongsReply receivedProto = BrowseSongsReply.parseFrom(received.getBody());

        assertEquals(sentProto, receivedProto);
    }
}
