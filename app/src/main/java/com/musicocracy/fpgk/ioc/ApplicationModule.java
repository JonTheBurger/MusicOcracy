package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.model.dal.Database;
import com.musicocracy.fpgk.model.net.ClientEventBus;
import com.musicocracy.fpgk.model.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.model.net.RxTcpClient;
import com.musicocracy.fpgk.model.net.RxTcpServer;
import com.musicocracy.fpgk.model.net.ServerEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    // Modules provide instances of classes
    // A program can have multiple modules for the purpose of modularity, but for our purposes we'll only be using one.
    //  For example, we could have a module for the application, a module for the Database, and a module for Networking.
    private final CyberJukeboxApplication application;

    public ApplicationModule(CyberJukeboxApplication application) {
        this.application = application;
    }

    // Application
    @Provides   // @Provides tells Dagger that this method can be used in the DI graph to fulfill a CyberJukeBoxApplication dependency.
    @Singleton  // @Singleton tells Dagger that we only want to provide one CyberJukeboxApplication instance per application. Dagger will cache @Singletons automatically.
    public CyberJukeboxApplication provideCyberJukeboxApplication() {
        return this.application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return this.application;
    }

    // Database
    @Provides
    @Singleton
    public Database provideDatabase(Context context) {
        return OpenHelperManager.getHelper(context, Database.class);
    }

    // Networking
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
