package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;

import rx.Observable;
import rx.functions.Func1;

public class SongSelectModel {
    private final String TAG = "SongSelectModel";
    private final ClientEventBus client;
    private final Logger log;

    public SongSelectModel(ClientEventBus client, Logger log) {
        this.client = client;
        this.log = log;
    }

    public void sendBrowseMsg(BrowseSongsRequest msg) {
        client.send(msg);
    }

    public Observable<BrowseSongsReply> getBrowseReply() {
        return client.getObservable()
                .flatMap(new Func1<Envelope, Observable<BrowseSongsReply>>() {
                    @Override
                    public Observable<BrowseSongsReply> call(Envelope envelope) {
                        if (envelope == Envelope.getDefaultInstance()) {
                            return Observable.just(BrowseSongsReply.getDefaultInstance());
                        } else if (envelope.getHeader().getType() == MessageType.BROWSE_SONGS_REPLY) {
                            BrowseSongsReply reply = BrowseSongsReply.getDefaultInstance();
                            try {
                                reply = BrowseSongsReply.parseFrom(envelope.getBody());
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                                log.error(TAG, e.toString());
                            }
                            return Observable.just(reply);
                        } else {
                            return Observable.empty();
                        }
                    }
                });
    }

    public void sendVotableSongsRequestMsg(VotableSongsRequest msg) { client.send(msg); }

    public Observable<VotableSongsReply> getVotableSongsReply() {
        return client.getObservable(MessageType.VOTABLE_SONGS_REPLY)
                .map(new Func1<Envelope, VotableSongsReply>() {
                    @Override
                    public VotableSongsReply call(Envelope Envelope) {
                        try {
                            return VotableSongsReply.parseFrom(Envelope.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            return VotableSongsReply.getDefaultInstance();
                        }
                    }
                });
    }

    public Observable<BasicReply> getVoteRequestReply() {
        return client.getObservable(MessageType.BASIC_REPLY)
                .map(new Func1<Envelope, BasicReply>() {
                    @Override
                    public BasicReply call(Envelope envelope) {
                        try {
                            return BasicReply.parseFrom(envelope.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                            return BasicReply.getDefaultInstance();
                        }
                    }
                })
                .filter(new Func1<BasicReply, Boolean>() {
                    @Override
                    public Boolean call(BasicReply basicReply) {
                        return basicReply.getReplyingTo() == MessageType.PLAY_REQUEST_REQUEST;
                    }
                });
    }

    public Observable<BasicReply> getPlayRequestReply() {
        return client.getObservable(MessageType.BASIC_REPLY)
                .map(new Func1<Envelope, BasicReply>() {
                    @Override
                    public BasicReply call(Envelope envelope) {
                        try {
                            return BasicReply.parseFrom(envelope.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                            return BasicReply.getDefaultInstance();
                        }
                    }
                })
                .filter(new Func1<BasicReply, Boolean>() {
                    @Override
                    public Boolean call(BasicReply basicReply) {
                        return basicReply.getReplyingTo() == MessageType.PLAY_REQUEST_REQUEST;
                    }
                });
    }

    public void sendPlayRequest(PlayRequestRequest msg) { client.send(msg); }

    public void sendVoteRequest(SendVoteRequest msg) { client.send(msg); }
}
