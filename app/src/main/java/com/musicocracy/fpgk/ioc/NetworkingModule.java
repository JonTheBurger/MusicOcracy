package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.domain.net.RxTcpClient;
import com.musicocracy.fpgk.domain.net.RxTcpServer;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    public ServerEventBus provideServerEventBus(RxTcpServer server, ProtoEnvelopeFactory factory) {
        return new ServerEventBus(server, factory);
    }

    @Provides
    @Singleton
    public ServerHandler provideServerHandler(ServerEventBus eventBus) {
        return new ServerHandler(eventBus);
    }

    @Provides
    @Named(DEFAULT_PORT)
    @Singleton
    public int provideDefaultPort() {
        return 2025;
    }
}
