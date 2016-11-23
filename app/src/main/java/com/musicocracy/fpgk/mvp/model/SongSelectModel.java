package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.net.proto.BrowseSongsAckMsg;
import com.musicocracy.fpgk.net.proto.BrowseSongsMsg;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import kaaes.spotify.webapi.android.SpotifyApi;
import rx.Observable;
import rx.functions.Func1;

public class SongSelectModel {
    private final Browser browser;
    private final SpotifyApi api;
    private final ClientEventBus client;

    public SongSelectModel(Browser browser, SpotifyApi api, ClientEventBus client) {
        this.browser = browser;
        this.api = api;
        this.client = client;
    }

    public void sendBrowseMsg(BrowseSongsMsg msg) {
        client.send(msg);
    }

    public Observable<BrowseSongsAckMsg> getBrowseResponse() {
        return client.getObservable(MessageType.BROWSE_SONGS_ACK)
                .map(new Func1<EnvelopeMsg, BrowseSongsAckMsg>() {
                    @Override
                    public BrowseSongsAckMsg call(EnvelopeMsg envelopeMsg) {
                        try {
                            return BrowseSongsAckMsg.parseFrom(envelopeMsg.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            return BrowseSongsAckMsg.getDefaultInstance();
                        }
                    }
                });
    }

    public void setToken(String token) {
        api.setAccessToken(token);
    }
}
