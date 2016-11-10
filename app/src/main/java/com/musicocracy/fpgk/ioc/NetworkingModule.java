package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.model.net.ClientEventBus;
import com.musicocracy.fpgk.model.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.model.net.RxTcpClient;
import com.musicocracy.fpgk.model.net.RxTcpServer;
import com.musicocracy.fpgk.model.net.ServerEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkingModule {
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
}
