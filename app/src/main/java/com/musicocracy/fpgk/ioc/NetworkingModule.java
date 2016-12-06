package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.net.Base64Encoder;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ClientHandler;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.domain.net.RxTcpClient;
import com.musicocracy.fpgk.domain.net.RxTcpServer;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;

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
    public Base64Encoder provideBase64Encoder() {
        return new Base64Encoder();
    }

    @Provides
    @Singleton
    public ProtoEnvelopeFactory provideProtoEnvelopeFactory(Base64Encoder encoder) {
        return new ProtoEnvelopeFactory(encoder);
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
    public ServerHandler provideServerHandler(ServerEventBus eventBus,
                                              PartySettings partySettings, Browser browser,
                                              SpotifyApi api, Logger log,
                                              SpotifyPlayerHandler playerHandler,
                                              DjAlgorithm djAlgorithm, Database database) {
        return new ServerHandler(eventBus, partySettings, browser, api, log, playerHandler,
                djAlgorithm, database);
    }

    @Provides
    @Named(DEFAULT_PORT)
    @Singleton
    public int provideDefaultPort() {
        return 2025;
    }
}
