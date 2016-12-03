package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ClientHandler;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.domain.net.RxTcpClient;
import com.musicocracy.fpgk.domain.net.RxTcpServer;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.spotify.sdk.android.player.SpotifyPlayer;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;

@Module
public class NetworkingModule {
    public static final String DEFAULT_PORT = "Default Port";

    @Provides
    @Singleton
    public ProtoEnvelopeFactory provideProtoEnvelopeFactory() {
        return new ProtoEnvelopeFactory();
    }

    @Provides
    @Singleton
    public RxTcpClient provideRxTcpClient() {
        return new RxTcpClient();
    }

    @Provides
    @Singleton
    public RxTcpServer provideRxTcpServer() {
        return new RxTcpServer();
    }

    @Provides
    @Singleton
    public ClientEventBus provideClientEventBus(RxTcpClient client, ProtoEnvelopeFactory factory) {
        return new ClientEventBus(client, factory);
    }

    @Provides
    @Singleton
    public ClientHandler provideClientHandler(ClientEventBus eventBus, Logger log) {
        return new ClientHandler(eventBus, log);
    }

    @Provides
    @Singleton
    public ServerEventBus provideServerEventBus(RxTcpServer server, ProtoEnvelopeFactory factory) {
        return new ServerEventBus(server, factory);
    }

    @Provides
    @Singleton
    public ServerHandler provideServerHandler(ServerEventBus eventBus, ReadOnlyPartySettings partySettings, Browser browser, SpotifyApi api, SpotifyPlayer player, Logger log, SpotifyPlayerHandler playerTimer) {
        return new ServerHandler(eventBus, partySettings, browser, api, player, log, playerTimer);
    }

    @Provides
    @Named(DEFAULT_PORT)
    @Singleton
    public int provideDefaultPort() {
        return 2025;
    }
}
