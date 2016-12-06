package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;

import kaaes.spotify.webapi.android.models.Track;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

public class ServerHandler {
    private static final Subscription[] emptySubs = new Subscription[0];
    private static final String TAG = "ServerHandler";
    private static final int NUM_BROWSE_RESULTS = 10;
    private final ServerEventBus eventBus;
    private final PartySettings partySettings;
    private final Browser browser;
    private final SpotifyApi api;
    private final Logger log;
    private final DjAlgorithm djAlgorithm;
    private final Database database;
    private final SharedSubject<String> newPlayRequest = SharedSubject.create();
    private SpotifyPlayerHandler spotifyPlayerHandler;
    private Subscription[] subscriptions = emptySubs;

    public ServerHandler(ServerEventBus eventBus, PartySettings partySettings,
                         Browser browser, SpotifyApi api, Logger log,
                         SpotifyPlayerHandler spotifyPlayerHandler, DjAlgorithm djAlgorithm,
                         Database database) {
        this.eventBus = eventBus;
        this.partySettings = partySettings;
        this.browser = browser;
        this.api = api;
        this.log = log;
        this.spotifyPlayerHandler = spotifyPlayerHandler;
        this.djAlgorithm = djAlgorithm;
        this.database = database;
    }

    public void onCreate() {
        api.setAccessToken(partySettings.getSpotifyToken());
        if (subscriptions == emptySubs) {
            subscriptions = new Subscription[] {
                    createLogSub(),
                    createClientConnectSub(),
                    createBrowseRequestSub(),
                    createVotableSongsRequestSub(),
                    createPlayRequestSub(),
                    createVoteRequestSub()
            };
        }
    }

    private Subscription createLogSub() {
        return eventBus.getObservableLog()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log.verbose(TAG, s);
                    }
                });
    }

    private Subscription createClientConnectSub() {
        return eventBus.getObservable(MessageType.CONNECT_REQUEST)
                .subscribe(new Subscriber<ProtoMessageBySender>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected createClientConnectSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected createClientConnectSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(ProtoMessageBySender msgBySender) {
                        ConnectRequest request;
                        try {
                            request = ConnectRequest.parseFrom(msgBySender.message.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            request = ConnectRequest.getDefaultInstance();
                            e.printStackTrace();
                        }

                        BasicReply reply;
                        if (request != ConnectRequest.getDefaultInstance() && request.getPartyName().equals(partySettings.getPartyName())) {
                            reply = BasicReply.newBuilder().setSuccess(true).setMessage("").setReplyingTo(msgBySender.message.getHeader().getType()).build();
                            try {
                                database.getGuestDao().createOrUpdate(new Guest(partySettings.raw(), "g", request.getRequesterId(), new Timestamp(System.currentTimeMillis()), false));
                            } catch (SQLException e) {
                                log.error(TAG, e.toString());
                            }
                        } else {
                            reply = BasicReply.newBuilder().setSuccess(false).setMessage("Invalid party credentials").setReplyingTo(msgBySender.message.getHeader().getType()).build();
                        }
                        log.verbose(TAG, "Sent: " + reply);
                        msgBySender.replyWith(reply);
                    }
                });
    }

    private Subscription createBrowseRequestSub() {
        return eventBus.getObservable(MessageType.BROWSE_SONGS_REQUEST)
                .subscribe(new Subscriber<ProtoMessageBySender>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected createBrowseRequestSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected createBrowseRequestSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(ProtoMessageBySender msgBySender) {
                        BrowseSongsRequest request;
                        try {
                            request = BrowseSongsRequest.parseFrom(msgBySender.message.getBody());
                            log.verbose(TAG, "Successful parse");
                        } catch (InvalidProtocolBufferException e) {
                            log.error(TAG, e.toString());
                            e.printStackTrace();
                            request = BrowseSongsRequest.getDefaultInstance();
                        }

                        List<Track> browseTracks = browser.browseTracks(request.getSongTitle());
                        log.verbose(TAG, browseTracks.size() + " songs found");

                        BrowseSongsReply.Builder builder = BrowseSongsReply.newBuilder();
                        for (int i = 0; i < browseTracks.size() && i < NUM_BROWSE_RESULTS; i++) {
                            builder .addSongs(BrowseSongsReply.BrowsableSong.newBuilder()
                                    .setTitle(browseTracks.get(i).name)
                                    .setArtist(browseTracks.get(i).artists.get(0).name)
                                    .setUri(browseTracks.get(i).uri)
                                    .setMusicService("Spotify")
                                    .build());
                        }
                        BrowseSongsReply reply = builder.build();

                        log.verbose(TAG, "Sent (BrowseReply): " + reply);
                        msgBySender.replyWith(reply);
                    }
                });
    }

    private Subscription createVotableSongsRequestSub() {
        return eventBus.getObservable(MessageType.VOTABLE_SONGS_REQUEST)
                .subscribe(new Subscriber<ProtoMessageBySender>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected createVotableSongsRequestSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected createVotableSongsRequestSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(ProtoMessageBySender msgBySender) {
                        List<String> votableURIs = getVotableURIs();
                        List<Track> votableTracks = getVotableTracks(votableURIs);

                        VotableSongsReply.Builder builder = VotableSongsReply.newBuilder();
                        for (int i = 0; i < votableTracks.size(); i++) {
                            Track track = votableTracks.get(i);
                            builder .addSongs(VotableSongsReply.VotableSong.newBuilder()
                                    .setArtist(track.artists.get(0).name)
                                    .setTitle(track.name)
                                    .setChoiceId(i)
                                    .build());
                        }
                        VotableSongsReply reply = builder.build();

                        log.verbose(TAG, "Sending msg " + reply);
                        msgBySender.replyWith(reply);
                        log.verbose(TAG, "Send complete. ~" + reply.toByteArray().length + " byte body");
                    }
                });
    }

    private Subscription createPlayRequestSub() {
        return eventBus.getObservable(MessageType.PLAY_REQUEST_REQUEST)
            .subscribe(new Subscriber<ProtoMessageBySender>() {
                @Override
                public void onCompleted() {
                    log.warning(TAG, "Unexpected createPlayRequestSub: onCompleted");
                }
                @Override
                public void onError(Throwable e) {
                    log.error(TAG, "Unexpected createPlayRequestSub: onError " + e.toString());
                }

                @Override
                public void onNext(ProtoMessageBySender msgBySender) {
                    PlayRequestRequest request;
                    try {
                        log.verbose(TAG, "Successful parse");
                        request = PlayRequestRequest.parseFrom(msgBySender.message.getBody());
                    } catch (InvalidProtocolBufferException e) {
                        log.error(TAG, e.toString());
                        request = PlayRequestRequest.getDefaultInstance();
                    }

                    BasicReply.Builder builder = BasicReply.newBuilder().setReplyingTo(msgBySender.message.getHeader().getType());
                    try {
                        if (request == PlayRequestRequest.getDefaultInstance()) { throw new InvalidProtocolBufferException("Invalid Play Request"); }

                        djAlgorithm.request(request.getUri(), request.getRequesterId());
                        newPlayRequest.onNext(request.getUri());

                        builder.setSuccess(true).setMessage("");
                        spotifyPlayerHandler.play();
                    } catch (SQLException | IllegalArgumentException | InvalidProtocolBufferException e) {
                        log.error(TAG, e.toString());
                        builder.setSuccess(false).setMessage(e.getMessage());
                    }

                    BasicReply reply = builder.build();
                    log.verbose(TAG, "Sent: " + reply);
                    msgBySender.replyWith(reply);
                }
            });
    }

    private Subscription createVoteRequestSub() {
        return eventBus.getObservable(MessageType.SEND_VOTE_REQUEST)
                .subscribe(new Subscriber<ProtoMessageBySender>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected createVoteRequestSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected createVoteRequestSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(ProtoMessageBySender msgBySender) {
                        SendVoteRequest request;
                        try {
                            request = SendVoteRequest.parseFrom(msgBySender.message.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            request = SendVoteRequest.getDefaultInstance();
                            e.printStackTrace();
                        }

                        BasicReply.Builder builder = BasicReply.newBuilder().setReplyingTo(msgBySender.message.getHeader().getType());
                        try {
                            if (request == SendVoteRequest.getDefaultInstance()) { throw new InvalidProtocolBufferException("Invalid Vote Request"); }

                            List<String> votableSongURIs = getVotableURIs();
                            String voteURI = votableSongURIs.get(request.getChoiceId());

                            djAlgorithm.voteFor(voteURI, request.getRequesterId());

                            builder.setSuccess(true).setMessage("");
                        } catch (SQLException | IllegalArgumentException | InvalidProtocolBufferException e) {
                            log.error(TAG, e.toString());
                            builder.setSuccess(false).setMessage(e.getMessage());
                        }

                        BasicReply reply = builder.build();
                        log.verbose(TAG, "Sent: " + reply);
                        msgBySender.replyWith(reply);
                    }
                });
    }

    public List<String> getVotableURIs() {
        List<String> votableSongURIs = null;
        try {
            votableSongURIs = djAlgorithm.getVotableSongUris();
        } catch (SQLException e) {
            log.error(TAG, e.toString());
        }
        return votableSongURIs;
    }

    public List<Track> getVotableTracks(List<String> votableSongURIs) {
        List<Track> votableTracks = new ArrayList<>();
        for (String uri : votableSongURIs) {
            Track track = browser.getTrackByURI(uri);
            votableTracks.add(track);
        }
        return votableTracks;
    }

    public Observable<String> newPlayRequest() {
        return newPlayRequest.asObservable();
    }

    public void onDestroy() {
        spotifyPlayerHandler.onDestroy();

        for (Subscription subscription : subscriptions) {
            RxUtils.safeUnsubscribe(subscription);
        }
        subscriptions = emptySubs;
    }
}
