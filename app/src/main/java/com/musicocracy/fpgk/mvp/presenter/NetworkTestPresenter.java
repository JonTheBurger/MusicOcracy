package com.musicocracy.fpgk.mvp.presenter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.NetworkTestModel;
import com.musicocracy.fpgk.domain.net.ProtoMessageBySender;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.mvp.view.NetworkTestView;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkTestPresenter implements Presenter<NetworkTestView> {
    private final NetworkTestModel model;
    private NetworkTestView view;
    private Subscription clientSub;
    private Subscription serverSub;
    private Subscription clientLogSub;
    private Subscription serverLogSub;
    private Subscription clientConnectedSub;
    private Subscription serverRunningSub;
    private int clientMsg = 1;
    private int serverMsg = 1;

    public NetworkTestPresenter(final NetworkTestModel model) {
        this.model = model;
        clientSub = model.getClientReceiver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Envelope>() {
            @Override
            public void call(Envelope message) {
                String parsed = "ERROR: Unrecognized message";

                try {
                    if (message.getHeader().getType() == MessageType.BROWSE_SONGS_REPLY) {
                        parsed = BrowseSongsReply.parseFrom(message.getBody()).toString();
                    } else if (message.getHeader().getType() == MessageType.VOTABLE_SONGS_REPLY) {
                        parsed = VotableSongsReply.parseFrom(message.getBody()).toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                view.logClientEvent(parsed);
            }
        });
        serverSub = model.getServerReceiver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ProtoMessageBySender>() {
            @Override
            public void call(ProtoMessageBySender messageBySender) {
                BrowseSongsRequest received = BrowseSongsRequest.getDefaultInstance();
                try {
                    received = BrowseSongsRequest.parseFrom(messageBySender.message.getBody());
                    view.logServerEvent(received.toString());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                BrowseSongsReply response = BrowseSongsReply.getDefaultInstance();
                messageBySender.replyWith(response);
            }
        });
        clientLogSub = model.getClientLog().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                view.logClientEvent(s);
            }
        });
        serverLogSub = model.getServerLog().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                view.logServerEvent(s);
            }
        });
        clientConnectedSub = model.getClientIsRunningObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isConnected) {
                view.setClientRunning(isConnected);
            }
        });
        serverRunningSub = model.getServerIsRunningObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isRunning) {
                view.setServerRunning(isRunning);
            }
        });
    }

    public void serverToggle() throws InterruptedException {
        if (view.getServerToggle()) {
            model.startServer(Integer.parseInt(view.getPortText()));
        } else {
            model.stopServer();
        }
    }

    public void clientToggle() {
        if (view.getClientToggle()) {
            String host;
            if (view.getLocalHostToggle()) {
                host = "localhost";
            } else {
                host = view.getIpText();
            }

            model.startClient(host, Integer.parseInt(view.getPortText()));
        } else {
            model.stopClient();
        }
    }

    public void localHostToggle() {
        view.setLocalHost(view.getLocalHostToggle());
    }

    public void serverSend() {
        VotableSongsReply votableSongs = VotableSongsReply.newBuilder()
                .addSongs(VotableSongsReply.VotableSong.newBuilder()
                        .setArtist("Queen")
                        .setTitle("Killer Queen")
                        .setChoiceId(serverMsg)
                        .build())
                .addSongs(VotableSongsReply.VotableSong.newBuilder()
                        .setArtist("Queen")
                        .setTitle("Good Old Fashioned Lover Boy")
                        .setChoiceId(serverMsg + 1)
                        .build())
                .build();
        serverMsg++;
        model.serverSend(votableSongs);
    }

    public void clientSend() {
        BrowseSongsRequest request = BrowseSongsRequest.newBuilder()
                .setSongTitle("Bicycle" + String.valueOf(clientMsg))
                .build();
        clientMsg++;
        model.clientSend(request);
    }

    public void destroy() throws InterruptedException {
        RxUtils.safeUnsubscribe(clientSub);
        RxUtils.safeUnsubscribe(serverSub);
        RxUtils.safeUnsubscribe(clientLogSub);
        RxUtils.safeUnsubscribe(serverLogSub);
        RxUtils.safeUnsubscribe(clientConnectedSub);
        RxUtils.safeUnsubscribe(serverRunningSub);
        model.stopClient();
        model.stopServer();
    }

    @Override
    public void setView(NetworkTestView networkTestView) {
        this.view = networkTestView;

        view.setClientRunning(model.isClientRunning());
        view.setServerRunning(model.isServerRunning());
    }
}
