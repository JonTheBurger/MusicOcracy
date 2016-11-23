package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
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

    public void sendBrowseMsg(BrowseSongsRequest msg) {
        client.send(msg);
    }

    public Observable<BrowseSongsReply> getBrowseResponse() {
        return client.getObservable(MessageType.BROWSE_SONGS_REPLY)
                .map(new Func1<Envelope, BrowseSongsReply>() {
                    @Override
                    public BrowseSongsReply call(Envelope Envelope) {
                        try {
                            return BrowseSongsReply.parseFrom(Envelope.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            return BrowseSongsReply.getDefaultInstance();
                        }
                    }
                });
    }

    public void setToken(String token) {
        api.setAccessToken(token);
    }
}
