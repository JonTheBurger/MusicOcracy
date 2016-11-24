package com.musicocracy.fpgk.mvp.presenter;

import android.content.Context;

import com.musicocracy.fpgk.mvp.model.PartyConfigModel;
import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.view.PartyConfigView;

public class PartyConfigPresenter implements Presenter<PartyConfigView> {
    private final PartyConfigModel model;
    private PartyConfigView view;

    public PartyConfigPresenter(PartyConfigModel model) {
        this.model = model;
    }

    public void onCreate(Context context) {
        String address = NetworkUtils.getMyIpAddress(context);
        view.setPartyCode(NetworkUtils.ipAddressToBase36(address).toUpperCase());
    }

    public void confirmSettings() {
        model.getSettings().setPartyCode(view.getPartyCode());
        model.getSettings().setPartyName(view.getPartyName());
        model.getSettings().setTokens(view.getTokenCount());
        long refillMillis = (view.getTokenRefillMinutes() * 60 + view.getTokenRefillSeconds()) * 1000;
        model.getSettings().setTokenRefillMillis(refillMillis);

        model.startServer();
    }

    public void onBack() throws InterruptedException {
        model.stopServer();
    }

    @Override
    public void setView(PartyConfigView view) {
        this.view = view;
    }
}
